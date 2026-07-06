package cn.codesensi.leaf.rbac.system.listener;

import cn.codesensi.leaf.rbac.framework.event.LogLoginEvent;
import cn.codesensi.leaf.rbac.system.entity.LogLogin;
import cn.codesensi.leaf.rbac.system.service.LogLoginService;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 登录日志监听器 —— 异步处理登录日志持久化。
 * <p>
 * 监听 {@link LogLoginEvent} 事件，通过 {@link EventListener} 异步消费，
 * 将事件中的登录记录信息转换并写入数据库，不阻塞主业务线程。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LogLoginListener {

    private final LogLoginService logLoginService;

    /**
     * 异步处理登录日志记录。
     * <p>
     * 监听 {@link LogLoginEvent} 事件，将事件中的登录信息（用户名、登录类型、IP、
     * 登录时间等）通过 {@link BeanUtil#copyProperties} 转换为 {@link LogLogin} 实体，
     * 调用 {@link LogLoginService#save} 持久化到数据库。
     * 使用 {@link Async} 异步执行，不阻塞登录主流程。
     *
     * @param event 登录日志事件，包含登录类型、用户名、IP 等信息
     */
    @Async
    @EventListener
    public void logLoginRecord(LogLoginEvent event) {
        log.info("[logLoginRecord][收到 LogLoginEvent 事件][事件类型：{}，用户名：{}]", event.getEventType(), event.getUsername());
        LogLogin logLogin = BeanUtil.copyProperties(event, LogLogin.class);
        logLoginService.save(logLogin);
    }
}
