package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.constants.Const;
import cn.codesensi.leaf.rbac.common.constants.ThreadConst;
import cn.codesensi.leaf.rbac.common.properties.ThreadPoolProperties;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池配置类 —— 统一管理应用中的线程池 Bean。
 * <p>
 * 提供两类线程池：
 * <ul>
 *   <li><b>异步任务线程池</b>（{@link #asyncExecutor()}）— 用于执行 {@code @Async} 异步任务，
 *       自动从主线程传递 {@link RequestAttributes} 和 MDC（含 traceId）到子线程；</li>
 *   <li><b>定时任务线程池</b>（{@link #scheduledExecutorService()}）— 用于执行定时/延迟任务，
 *       每次执行前自动生成新的 traceId 并注入 MDC，执行结束后清理。</li>
 * </ul>
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;

    /**
     * 拒绝策略映射表（key 与配置项 {@code thread.pool.rejected-execution-handler} 对应）。
     * <p>
     * 支持四种策略：
     * <ul>
     *   <li>{@code CallerRunsPolicy} — 由调用线程执行（默认，降低提交速度）；</li>
     *   <li>{@code AbortPolicy} — 直接抛出 {@link RejectedExecutionException}；</li>
     *   <li>{@code DiscardPolicy} — 静默丢弃任务；</li>
     *   <li>{@code DiscardOldestPolicy} — 丢弃队列中最旧的任务后重试。</li>
     * </ul>
     */
    private static final Map<String, RejectedExecutionHandler> REJECTED_HANDLER_MAP = Map.of(
            "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy(),
            "AbortPolicy", new ThreadPoolExecutor.AbortPolicy(),
            "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy(),
            "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    /**
     * 异步任务线程池 —— 用于执行 {@code @Async} 标注的异步方法。
     * <p>
     * 通过 {@link ThreadPoolTaskExecutor} Spring 管理生命周期，具备以下特性：
     * <ul>
     *   <li>核心/最大线程数、队列容量、超时时间均从配置 {@link ThreadPoolProperties} 读取；</li>
     *   <li>支持优雅停机（等待已提交任务完成）；</li>
     *   <li>通过 {@link #setTaskDecorator} 自动将主线程的 {@link RequestAttributes} 和
     *       MDC 上下文（含 traceId）传递到子线程，并在执行完毕后清理，防止内存泄漏。</li>
     * </ul>
     *
     * @return 异步任务线程池
     */
    @Primary
    @Bean(name = ThreadConst.ASYNC_EXECUTOR_NAME)
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.getAllowCoreThreadTimeout());
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.getWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(threadPoolProperties.getAwaitTerminationSeconds());
        executor.setThreadNamePrefix("async-thread-");
        // 根据配置获取拒绝策略，未匹配时默认使用CallerRunsPolicy
        RejectedExecutionHandler handler = REJECTED_HANDLER_MAP.getOrDefault(threadPoolProperties.getRejectedExecutionHandler(), new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setRejectedExecutionHandler(handler);
        // 链路追踪：通过装饰器传递上下文
        executor.setTaskDecorator(runnable -> {
            // 获取主线程的上下文
            RequestAttributes context = RequestContextHolder.currentRequestAttributes();
            // 获取主线程的 MDC 上下文（包含 TraceId）
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    // 在子线程中恢复上下文
                    RequestContextHolder.setRequestAttributes(context);
                    if (mdcContext != null) {
                        MDC.setContextMap(mdcContext);
                    }
                    runnable.run();
                } finally {
                    // 任务执行完毕后清理，防止内存泄漏
                    RequestContextHolder.resetRequestAttributes();
                    MDC.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务线程池 —— 用于执行定时/延迟任务（如 {@code @Scheduled}、延迟队列等）。
     * <p>
     * 继承 {@link ScheduledThreadPoolExecutor} 并重写 {@code beforeExecute/afterExecute}，
     * 在每次任务执行前自动生成新的 traceId 注入 MDC，执行结束后清理，实现任务级别的链路追踪隔离。
     * <p>
     * 线程工厂使用内置 {@link ThreadFactory}，线程命名格式 {@code schedule-thread-N}，均为守护线程。
     *
     * @return 定时任务线程池
     */
    @Bean(name = ThreadConst.SCHEDULED_EXECUTOR_SERVICE)
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),
                BasicThreadFactory.builder().namingPattern("schedule-thread-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()) {

            @Override
            public void beforeExecute(Thread thread, Runnable runnable) {
                super.beforeExecute(thread, runnable);
                // 为当前定时任务生成一个全新的 traceId 并放入 MDC
                MDC.put(Const.TRACE_ID, IdUtil.fastSimpleUUID());
            }

            @Override
            public void afterExecute(Runnable runnable, Throwable throwable) {
                super.afterExecute(runnable, throwable);
                // 任务结束后清理 MDC，防止内存泄漏
                MDC.remove(Const.TRACE_ID);
            }
        };
    }

}
