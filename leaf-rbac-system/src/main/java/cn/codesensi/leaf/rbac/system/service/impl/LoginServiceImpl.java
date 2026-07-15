package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.codesensi.leaf.rbac.system.dto.LoginAccountDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.LoginService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 登录接口实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final AppCaptchaProperties appCaptchaProperties;
    private final SysUserService sysUserService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 账号密码登录
     *
     * @param loginAccountDTO 登录用户信息
     * @return 登录成功后信息
     */
    @Override
    public LoginResultDTO loginAccount(LoginAccountDTO loginAccountDTO) {
        // 校验验证码
        if (appCaptchaProperties.getEnabled()) {
            if (StrUtil.isBlank(loginAccountDTO.getCaptchaKey())) {
                throw new ValidationException("验证码唯一标识为空");
            }
            String captchaValue = loginAccountDTO.getCaptchaValue();
            if (StrUtil.isBlank(captchaValue)) {
                throw new ValidationException("验证码为空");
            }
            // 与缓存中的值对比
            String captchaCache = stringRedisTemplate.opsForValue().getAndDelete(CacheUtil.getCaptchaImagePrefix().concat(loginAccountDTO.getCaptchaKey()));
            if (StrUtil.isBlank(captchaCache)) {
                throw new BusinessException("验证码不存在");
            }
            if (!captchaValue.equals(captchaCache)) {
                throw new BusinessException("验证码错误");
            }
        }

        // 校验用户（同时获取用于上下文的字段）
        SysUser sysUser = sysUserService.queryChain()
                .select(SYS_USER.ID, SYS_USER.PASSWORD, SYS_USER.USERNAME, SYS_USER.NICKNAME)
                .where(SYS_USER.USERNAME.eq(loginAccountDTO.getUsername()))
                .one();
        if (ObjUtil.isNull(sysUser) || !BCrypt.checkpw(loginAccountDTO.getPassword(), sysUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

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
