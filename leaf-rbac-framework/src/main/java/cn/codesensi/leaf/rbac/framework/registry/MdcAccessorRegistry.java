package cn.codesensi.leaf.rbac.framework.registry;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MDC 线程上下文访问器注册器。
 * <p>
 * 将 SLF4J {@link MDC} 中基于 {@link ThreadLocal} 的日志诊断上下文注册到 Micrometer
 * 的 {@link ContextRegistry} 中，使得在跨线程传递上下文时（如 {@code @Async} 异步方法、
 * {@link java.util.concurrent.CompletableFuture} 等场景），Micrometer 的
 * {@code ContextSnapshotFactory} 能够自动捕获当前线程的 MDC（包含 traceId 等诊断信息），
 * 并在目标线程中恢复，从而解决异步场景下日志链路断裂的问题。
 * </p>
 * <p>
 * 配合 {@link cn.codesensi.leaf.rbac.framework.filter.TraceIdFilter} 和
 * {@link cn.codesensi.leaf.rbac.framework.config.ThreadPoolConfig} 中的
 * {@code TaskDecorator} 使用，三者共同保证异步线程池中的日志链路一致性。
 * </p>
 *
 * @author codesensi
 * @since 1.0
 * @see ContextRegistry
 * @see ThreadLocalAccessor
 * @see MDC
 */
@Component
public class MdcAccessorRegistry {

    /**
     * 在 Bean 初始化完成后，将 MDC 的 ThreadLocal 访问器注册到全局 ContextRegistry。
     * <p>
     * 注册后，Micrometer 的上下文捕获机制在快照当前上下文时，会通过 {@code getValue()}
     * 获取 MDC 上下文的副本，并在目标线程中通过 {@code setValue()} 恢复。
     */
    @PostConstruct
    public void register() {
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(new ThreadLocalAccessor<Map<String, String>>() {

                    /**
                     * 返回当前访问器在 ContextRegistry 中的唯一标识键，
                     * 用于区分不同类型的上下文。
                     *
                     * @return 键值 {@link AppConst#MDC_CONTEXT}
                     */
                    @Override
                    public Object key() {
                        return AppConst.MDC_CONTEXT;
                    }

                    /**
                     * 在当前线程中捕获并返回 MDC 上下文的完整副本。
                     * <p>
                     * 使用 {@link MDC#getCopyOfContextMap()} 获取不可变快照，
                     * 避免原始 MDC 被后续修改影响已捕获的快照。
                     * 在创建上下文快照时被调用，返回值将绑定到快照中。
                     *
                     * @return 当前线程 MDC 上下文的副本，可能为 {@code null}
                     */
                    @Override
                    public Map<String, String> getValue() {
                        return MDC.getCopyOfContextMap();
                    }

                    /**
                     * 在目标线程中恢复 MDC 上下文。
                     * <p>
                     * 当异步任务在目标线程中执行时，该方法被调用以将之前捕获的
                     * MDC 上下文设置到目标线程中，确保日志输出包含原始的 traceId
                     * 等诊断信息。
                     *
                     * @param value 之前捕获的 MDC 上下文副本
                     */
                    @Override
                    public void setValue(Map<String, String> value) {
                        MDC.setContextMap(value);
                    }

                    /**
                     * 在目标线程中重置 MDC 上下文。
                     * <p>
                     * 当目标线程中没有对应的 MDC 上下文需要恢复时调用，
                     * 清空 MDC 防止上一轮请求的日志上下文中遗留信息污染当前日志输出。
                     */
                    @Override
                    public void setValue() {
                        MDC.clear();
                    }
                });
    }
}
