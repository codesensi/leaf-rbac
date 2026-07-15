package cn.codesensi.leaf.rbac.system.helper;

import cn.codesensi.leaf.rbac.common.enums.EventType;
import cn.codesensi.leaf.rbac.common.enums.LoginType;
import cn.codesensi.leaf.rbac.common.enums.YesNoEnum;
import cn.codesensi.leaf.rbac.framework.event.LogLoginEvent;
import cn.codesensi.leaf.rbac.framework.util.Ip2regionUtil;
import cn.codesensi.leaf.rbac.framework.util.IpUtil;
import cn.codesensi.leaf.rbac.framework.util.ServletUtil;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 登录日志辅助类 —— 统一构建并发布登录/登出日志事件，
 * 将请求元数据（IP、UserAgent、地区等）的采集与业务逻辑解耦。
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LogLoginHelper {

    private final ApplicationEventPublisher eventPublisher;
    private final SysUserService sysUserService;

    /**
     * 发布登录日志事件
     *
     * @param loginKey 登录标识（账号/手机号）
     * @param username 登录人账号
     * @param userId   登录人ID（失败时可能为 null）
     * @param status   状态：1-成功，0-失败
     * @param errorMsg 错误信息（成功时为 null）
     * @param params   请求参数 JSON
     * @param start    业务开始时间戳（ms），用于计算耗时
     */
    public void publishLoginEvent(String loginKey, String username, Long userId,
                                  Integer status, String errorMsg,
                                  String params, long start) {
        LogLoginEvent event = buildEvent(EventType.LOGIN,
                LoginType.ACCOUNT.getCode(),
                loginKey,
                username,
                userId,
                status,
                errorMsg,
                params,
                start);
        eventPublisher.publishEvent(event);
    }

    /**
     * 发布登出日志事件（按 userId 内部查询用户名）
     *
     * @param userId 登录人ID
     * @param start  业务开始时间戳（ms），用于计算耗时
     */
    public void publishLogoutEvent(Long userId, long start) {
        String username = null;
        if (ObjUtil.isNotNull(userId)) {
            SysUser sysUser = sysUserService.queryChain()
                    .select(SYS_USER.USERNAME)
                    .where(SYS_USER.ID.eq(userId))
                    .one();
            username = ObjUtil.isNotNull(sysUser) ? sysUser.getUsername() : null;
        }
        LogLoginEvent event = buildEvent(EventType.LOGOUT,
                null,
                username,
                username,
                userId,
                YesNoEnum.YES.getCode(),
                null,
                null,
                start);
        eventPublisher.publishEvent(event);
    }

    /**
     * 构建登录日志事件，统一采集请求元数据
     */
    private LogLoginEvent buildEvent(EventType eventType, Integer loginType, String loginKey,
                                     String username, Long userId,
                                     Integer status, String errorMsg,
                                     String params, long start) {
        LogLoginEvent.LogLoginEventBuilder builder = LogLoginEvent.builder()
                .source(StrUtil.blankToDefault(username, "unknown"))
                .eventType(eventType.getCode())
                .loginType(loginType)
                .loginKey(loginKey)
                .username(username)
                .userId(userId)
                .status(status)
                .errorMsg(errorMsg)
                .params(params)
                .durationMs(System.currentTimeMillis() - start);

        // 采集请求元数据
        try {
            String ipAddr = IpUtil.getIpAddr();
            if (StrUtil.isNotBlank(ipAddr)) {
                builder.requestIp(ipAddr);
                builder.requestArea(Ip2regionUtil.search(ipAddr));
            }

            String userAgentStr = ServletUtil.getUserAgent();
            if (StrUtil.isNotBlank(userAgentStr)) {
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
                builder.requestOs(userAgent.getOperatingSystem().getName());
                builder.requestDevice(userAgent.getOperatingSystem().getDeviceType().getName());
                builder.requestBrowser(userAgent.getBrowser().getName());
            }
        } catch (Exception e) {
            log.warn("采集登录日志请求元数据异常", e);
        }

        return builder.build();
    }

}
