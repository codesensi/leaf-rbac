package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.LoginService;
import cn.codesensi.leaf.rbac.system.strategy.login.LoginStrategy;
import cn.codesensi.leaf.rbac.system.strategy.login.LoginStrategyFactory;
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

/**
 * 登录接口实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final LoginStrategyFactory loginStrategyFactory;

    /**
     * 登录
     *
     * @param loginDTO 登录用户信息
     * @return 登录成功后信息
     */

    @Override
    public LoginResultDTO login(LoginDTO loginDTO) {
        // 获取对应策略并执行
        LoginStrategy strategy = loginStrategyFactory.getStrategy(loginDTO.getType());
        SysUser sysUser = strategy.login(loginDTO);

        Long userId = sysUser.getId();
        // 校验账户是否封禁
        StpUtil.checkDisable(userId);
        // 登录
        StpUtil.login(userId);
        // 登录成功后，将完整用户上下文存入 SaToken Session，供后续请求的 UserContextFilter 恢复到线程变量中
        saveUserContextToSession(sysUser);

        // 构建登录响应
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setAccessToken(StpUtil.getTokenValue());
        long accessTokenTimeout = StpUtil.getTokenTimeout();
        loginResultDTO.setExpires(LocalDateTimeUtil.now()
                .plusSeconds(accessTokenTimeout)
                .toInstant(ZoneOffset.of("+8"))
                .toEpochMilli());
        loginResultDTO.setTokenName(SaManager.getConfig().getTokenName());
        loginResultDTO.setTokenPrefix(SaManager.getConfig().getTokenPrefix());
        return loginResultDTO;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 将用户上下文保存到 SaToken Session 并绑定到当前线程。
     * <p>
     * 登录是个特殊的"认证前→认证后"请求：{@code UserContextInterceptor.preHandle()}
     * 阶段用户尚未登录，无法恢复上下文；但登录成功后（Controller → Service → 切面记录登录日志等），
     * 当前请求的后续链路仍然需要从 {@link cn.codesensi.leaf.rbac.framework.context.UserContextHolder}
     * 获取用户信息。因此这里做了两件事：
     * </p>
     * <ol>
     *   <li>存入 SaToken Session — 供后续请求的拦截器恢复；</li>
     *   <li>绑定到当前线程 — 供当前请求的后续逻辑使用（如 MybatisFlex 审计字段填充）。</li>
     * </ol>
     *
     * @param sysUser 用户信息
     */
    private void saveUserContextToSession(SysUser sysUser) {
        String username = sysUser.getUsername();
        UserContext userContext = UserContext.builder()
                .userId(sysUser.getId())
                .username(username)
                .nickname(sysUser.getNickname())
                .loginKey(username)
                .build();
        // 存入 SaToken Session，供后续请求拦截器恢复
        StpUtil.getSession().set(AppConst.USER_CONTEXT_KEY, userContext);
        // 同时绑定到当前线程，供当前请求（如登录日志等）使用
        UserContextHolder.set(userContext);
    }

}
