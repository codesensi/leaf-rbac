package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.constants.ThreadConst;
import cn.codesensi.leaf.rbac.common.properties.ThreadPoolProperties;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.hutool.core.util.IdUtil;
import io.micrometer.context.ContextRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池配置类 —— 统一管理应用中的线程池 Bean。
 * <p>
 * 提供两类线程池：
 * <ul>
 *   <li><b>异步任务线程池</b>（{@link #asyncExecutor}）— 用于执行 {@code @Async} 异步任务，
 *       通过 {@link ContextPropagatingTaskDecorator} 自动从主线程传递 MDC（含 traceId）
 *       和当前操作用户信息到子线程（依赖 Micrometer ContextRegistry 注册的访问器）；</li>
 *   <li><b>定时任务线程池</b>（{@link #scheduledExecutorService()}）— 用于执行定时/延迟任务，
 *       （预留钩子，可按需在 beforeExecute 中生成新 traceId）。</li>
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
     * 拒绝策略名称与处理器实例的映射表。
     * <p>
     * Key 与配置文件 {@code thread.pool.rejected-execution-handler} 项的值对应，
     * 支持以下四种 JDK 内置策略：
     * <ul>
     *   <li>{@code CallerRunsPolicy} — 调用线程直接执行该任务（默认策略，通过降低任务提交速率实现背压）；</li>
     *   <li>{@code AbortPolicy} — 直接抛出 {@link RejectedExecutionException}（最激进，明确告知拒绝）；</li>
     *   <li>{@code DiscardPolicy} — 静默丢弃当前被拒绝的任务，任务丢失不可见；</li>
     *   <li>{@code DiscardOldestPolicy} — 丢弃队列头部的等待任务（最旧的），然后重试提交当前任务。</li>
     * </ul>
     */
    private static final Map<String, RejectedExecutionHandler> REJECTED_HANDLER_MAP = Map.of(
            "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy(),
            "AbortPolicy", new ThreadPoolExecutor.AbortPolicy(),
            "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy(),
            "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    /**
     * 上下文传播 TaskDecorator —— 自动将主线程的 MDC 和 UserContext 传递到子线程。
     * <p>
     * 使用 Spring 6.2+ 提供的 {@link ContextPropagatingTaskDecorator}，它依赖 Micrometer 的
     * {@link ContextRegistry} 中注册的 {@link io.micrometer.context.ThreadLocalAccessor} 来实现
     * 上下文的自动捕获和恢复，无需手动编写 capture/restore/clear 逻辑。
     * </p>
     * <p>
     * 配合以下两个注册器使用：
     * <ul>
     *   <li>{@link cn.codesensi.leaf.rbac.framework.registry.UserContextAccessorRegistry} — 注册 UserContext 的访问器；</li>
     *   <li>{@link cn.codesensi.leaf.rbac.framework.registry.MdcAccessorRegistry} — 注册 MDC 的访问器。</li>
     * </ul>
     *
     * @return 上下文传播装饰器
     */
    @Bean
    public TaskDecorator taskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    /**
     * 异步任务线程池 —— 用于执行 {@code @Async} 标注的异步方法。
     * <p>
     * 通过 {@link ThreadPoolTaskExecutor} 由 Spring 管理生命周期，具备以下特性：
     * <ul>
     *   <li>核心/最大线程数、队列容量、超时时间均从配置 {@link ThreadPoolProperties} 读取；</li>
     *   <li>支持优雅停机（等待已提交任务完成再关闭）；</li>
     *   <li>通过 {@link ContextPropagatingTaskDecorator} 自动将主线程的 MDC 上下文
     *       （含 traceId）和当前操作用户信息传递到子线程，无需手动编写上下文传递逻辑；</li>
     *   <li>标注 {@link Primary}，是全局默认的 {@link Executor} Bean。</li>
     * </ul>
     *
     * @param taskDecorator 上下文传播装饰器
     * @return 异步任务线程池
     */
    @Primary
    @Bean(name = ThreadConst.ASYNC_EXECUTOR_NAME)
    public Executor asyncExecutor(TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.getAllowCoreThreadTimeout());
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.getWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(threadPoolProperties.getAwaitTerminationSeconds());
        executor.setThreadNamePrefix("async-thread-");
        // 根据配置获取拒绝策略，未匹配时兜底使用 CallerRunsPolicy（由提交任务的线程自行执行）
        RejectedExecutionHandler handler = REJECTED_HANDLER_MAP.getOrDefault(
                threadPoolProperties.getRejectedExecutionHandler(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.setRejectedExecutionHandler(handler);
        // 上下文传播装饰器：基于 Micrometer ContextRegistry 自动捕获主线程上下文并在子线程中恢复
        executor.setTaskDecorator(taskDecorator);
        // 上下文传递装饰器：异步线程执行前捕获主线程上下文，执行后清理，实现链路追踪与操作人隔离
        // executor.setTaskDecorator(runnable -> {
        //     AsyncContext asyncContext = AsyncContext.capture();
        //     return () -> {
        //         try {
        //             asyncContext.restore();
        //             runnable.run();
        //         } finally {
        //             asyncContext.clear();
        //         }
        //     };
        // });
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务线程池 —— 用于执行定时/延迟任务（如 {@code @Scheduled}、延迟队列等）。
     * <p>
     * 继承 {@link ScheduledThreadPoolExecutor}。当前使用默认行为，无额外上下文传递。
     * <p>
     * 若后续需要为定时任务生成独立的 traceId，可取消注释下方 {@code beforeExecute/afterExecute}
     * 的重写逻辑，在每次任务执行前自动生成新的 traceId 注入 MDC，执行结束后清理，
     * 实现任务级别的链路追踪隔离。
     * </p>
     * <p>
     * 线程工厂使用 Apache Commons Lang3 的 {@link BasicThreadFactory}，
     * 线程命名格式 {@code schedule-thread-N}，均为守护线程（daemon=true），
     * 不会阻止 JVM 退出。
     * </p>
     *
     * @return 定时任务线程池
     */
    @Bean(name = ThreadConst.SCHEDULED_EXECUTOR_SERVICE)
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(
                threadPoolProperties.getCorePoolSize(),
                BasicThreadFactory.builder().namingPattern("schedule-thread-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()) {

            // @Override
            // public void beforeExecute(Thread thread, Runnable runnable) {
            //     super.beforeExecute(thread, runnable);
            //     // 为当前定时任务生成一个全新的 traceId 并放入 MDC，实现任务级别的链路追踪隔离
            //     MDC.put(AppConst.TRACE_ID, IdUtil.fastSimpleUUID());
            // }
            //
            // @Override
            // public void afterExecute(Runnable runnable, Throwable throwable) {
            //     super.afterExecute(runnable, throwable);
            //     // 任务结束后清理 MDC，防止内存泄漏
            //     MDC.remove(AppConst.TRACE_ID);
            // }
        };
    }

    /**
     * 异步线程上下文快照 —— 将主线程的两个上下文拍摄快照后在子线程中恢复。
     * <p>
     * <b>已弃用</b>：当前通过 {@link ContextPropagatingTaskDecorator} + Micrometer
     * {@link ContextRegistry} 自动完成上下文传播，不再使用此手动快照方案。
     * 保留此 record 作为手动方案参考。
     * </p>
     * <p>
     * 捕获内容：
     * <ul>
     *   <li>{@link MDC} 上下文 — 日志链路追踪 ID（traceId）；</li>
     *   <li>{@link UserContext} — 当前操作人的完整信息（用户ID、用户名、昵称等），
     *       从 {@link UserContextHolder} 获取（主线程在 Filter 中已从 SaToken Session 恢复）。</li>
     * </ul>
     *
     * @param mdcContext  MDC 上下文快照
     * @param userContext 用户上下文快照
     */
    private record AsyncContext(Map<String, String> mdcContext, UserContext userContext) {

        /**
         * 在主线程中拍摄上下文快照。
         * <p>
         * 捕获当前线程的 MDC 上下文副本和完整的 {@link UserContext} 数据。
         * </p>
         *
         * @return 包含 MDC 和用户上下文的快照记录
         */
        static AsyncContext capture() {
            Map<String, String> mdc = MDC.getCopyOfContextMap();
            UserContext userContext = UserContextHolder.get();
            return new AsyncContext(mdc, userContext);
        }

        /**
         * 在子线程中恢复捕获的上下文。
         * <p>
         * 按顺序恢复 MDC → {@link UserContext}，确保子线程拥有与主线程相同的
         * 日志追踪和操作用户信息。每一项均做非空校验，避免空值覆盖子线程中已有的上下文。
         * </p>
         */
        void restore() {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }
            if (userContext != null) {
                UserContextHolder.set(userContext);
            }
        }

        /**
         * 任务执行完毕后清理子线程的上下文。
         * <p>
         * 依次清理 MDC → {@link UserContext}，
         * 防止 ThreadLocal 在 Tomcat 线程池复用场景下产生上下文污染或内存泄漏。
         * 此方法应在 {@code finally} 块中调用，确保异常场景也能正确清理。
         * </p>
         */
        void clear() {
            MDC.clear();
            UserContextHolder.clear();
        }
    }
}
