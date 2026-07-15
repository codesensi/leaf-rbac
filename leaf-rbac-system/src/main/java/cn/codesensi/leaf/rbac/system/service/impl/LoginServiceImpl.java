package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.enums.EventType;
import cn.codesensi.leaf.rbac.common.enums.LoginType;
import cn.codesensi.leaf.rbac.common.enums.YesNoEnum;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.framework.event.LogLoginEvent;
import cn.codesensi.leaf.rbac.framework.util.Ip2regionUtil;
import cn.codesensi.leaf.rbac.framework.util.IpUtil;
import cn.codesensi.leaf.rbac.framework.util.ServletUtil;
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
import cn.hutool.json.JSONUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 账号密码登录
     *
     * @param loginAccountDTO 登录用户信息
     * @return 登录成功后信息
     */
    @Override
    public LoginResultDTO loginAccount(LoginAccountDTO loginAccountDTO) {
        long start = System.currentTimeMillis();
        String username = loginAccountDTO.getUsername();
        LogLoginEvent.LogLoginEventBuilder builder = LogLoginEvent.builder();

        try {
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
            SysUser sysUser = sysUserService.queryChain()
                    .where(SYS_USER.USERNAME.eq(username))
                    .one();
            if (ObjUtil.isNull(sysUser) || !BCrypt.checkpw(loginAccountDTO.getPassword(), sysUser.getPassword())) {
                throw new BusinessException("账号或密码错误");
            }

            Long userId = sysUser.getId();
            builder.userId(userId);

            // 校验账户是否封禁
            StpUtil.checkDisable(userId);
            // 登录
            StpUtil.login(userId);

            LoginResultDTO loginResultDTO = new LoginResultDTO();
            loginResultDTO.setAccessToken(StpUtil.getTokenValue());
            // 访问令牌过期时间
            long accessTokenTimeout = StpUtil.getTokenTimeout();
            loginResultDTO.setExpires(LocalDateTimeUtil.now().plusSeconds(accessTokenTimeout).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            loginResultDTO.setTokenName(SaManager.getConfig().getTokenName());
            loginResultDTO.setTokenPrefix(SaManager.getConfig().getTokenPrefix());

            builder.status(YesNoEnum.YES.getCode());
            return loginResultDTO;
        } catch (Exception e) {
            builder.status(YesNoEnum.NO.getCode());
            builder.errorMsg(e.getMessage());
            throw e;
        } finally {
            // 发布登录日志记录事件
            try {
                builder.source(username);
                builder.loginType(LoginType.ACCOUNT.getCode());
                builder.eventType(EventType.LOGIN.getCode());
                builder.loginKey(username);
                builder.username(username);
                builder.params(JSONUtil.toJsonStr(loginAccountDTO));
                String ipAddr = IpUtil.getIpAddr();
                builder.requestIp(ipAddr);
                builder.requestArea(Ip2regionUtil.search(ipAddr));
                UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getUserAgent());
                builder.requestOs(userAgent.getOperatingSystem().getName());
                builder.requestDevice(userAgent.getOperatingSystem().getDeviceType().getName());
                builder.requestBrowser(userAgent.getBrowser().getName());
                builder.durationMs(System.currentTimeMillis() - start);
                eventPublisher.publishEvent(builder.build());
            } catch (Exception e) {
                log.warn("发布登录日志记录事件异常", e);
            }
        }
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        long start = System.currentTimeMillis();
        Long userId = StpUtil.getLoginIdAsLong();
        // 执行退出登录
        StpUtil.logout();
        // 发布退出登录日志记录事件
        try {
            SysUser sysUser = sysUserService.queryChain()
                    .select(SYS_USER.USERNAME)
                    .where(SYS_USER.ID.eq(userId))
                    .one();
            if (ObjUtil.isNull(sysUser)) {
                throw new BusinessException("用户不存在");
            }
            String username = sysUser.getUsername();
            LogLoginEvent.LogLoginEventBuilder builder = LogLoginEvent.builder();
            builder.source(username);
            builder.eventType(EventType.LOGOUT.getCode());
            builder.userId(userId);
            builder.username(username);
            builder.status(YesNoEnum.YES.getCode());
            String ipAddr = IpUtil.getIpAddr();
            builder.requestIp(ipAddr);
            builder.requestArea(Ip2regionUtil.search(ipAddr));
            UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getUserAgent());
            builder.requestOs(userAgent.getOperatingSystem().getName());
            builder.requestDevice(userAgent.getOperatingSystem().getDeviceType().getName());
            builder.requestBrowser(userAgent.getBrowser().getName());
            builder.durationMs(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(builder.build());
        } catch (Exception e) {
            log.warn("发布退出登录日志记录事件异常：", e);
        }
    }

}
