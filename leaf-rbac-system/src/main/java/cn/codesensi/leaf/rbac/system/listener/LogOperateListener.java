package cn.codesensi.leaf.rbac.system.listener;

import cn.codesensi.leaf.rbac.framework.event.LogOperateEvent;
import cn.codesensi.leaf.rbac.system.entity.LogOperate;
import cn.codesensi.leaf.rbac.system.service.LogOperateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志事件监听器 —— 异步消费 {@link LogOperateEvent} 事件并持久化到数据库。
 * <p>
 * 配合 {@link cn.codesensi.leaf.rbac.framework.aspect.LogOperateAspect} 使用：
 * AOP 切面采集日志信息后发布事件，本监听器通过 {@code @Async} 异步写入，
 * 避免日志持久化阻塞主业务流程。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LogOperateListener {

    private final LogOperateService logOperateService;

    /**
     * 异步消费操作日志事件，将事件数据拷贝为实体并保存到数据库。
     * <p>
     * 使用 {@link BeanUtils#copyProperties(Object, Object)} 将事件对象
     * 中的同名字段复制到 {@link LogOperate} 实体，然后调用 Service 持久化。
     * <p>
     * 异步执行（{@code @Async}），不阻塞请求线程。
     *
     * @param event 操作日志事件对象，由 {@code LogOperateAspect} 发布
     */
    @Async
    @EventListener
    public void logRecord(LogOperateEvent event) {
        log.info("[logRecord][收到 LogOperateEvent 事件][模块：{}，描述：{}]", event.getModule(), event.getDescr());
        LogOperate logOperate = new LogOperate();
        BeanUtils.copyProperties(event, logOperate);
        logOperateService.save(logOperate);
    }
}
