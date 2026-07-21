package cn.codesensi.leaf.rbac.framework.registry;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * UserContext 线程上下文访问器注册器。
 * <p>
 * 将 {@link UserContextHolder} 中基于 {@link ThreadLocal} 的用户上下文注册到 Micrometer
 * 的 {@link ContextRegistry} 中，使得在跨线程传递上下文时（如 {@code @Async} 异步方法、
 * {@link java.util.concurrent.CompletableFuture}、响应式编程等场景），
 * Micrometer 的 {@code ContextSnapshotFactory} 能够自动捕获当前线程的 UserContext，
 * 并在目标线程中恢复，从而解决 ThreadLocal 在异步场景下的上下文丢失问题。
 * </p>
 * <p>
 * 配合 {@link cn.codesensi.leaf.rbac.framework.config.ThreadPoolConfig} 中的
 * {@code TaskDecorator} 使用，两者共同保证异步线程池中的用户上下文一致性。
 * </p>
 *
 * @author codesensi
 * @since 1.0
 * @see UserContextHolder
 * @see ContextRegistry
 * @see ThreadLocalAccessor
 */
@Component
public class UserContextAccessorRegistry {

    /**
     * 在 Bean 初始化完成后，将 UserContext 的 ThreadLocal 访问器注册到全局 ContextRegistry。
     * <p>
     * 注册后，Micrometer 的上下文捕获机制在快照当前上下文时，会通过 {@code getValue()}
     * 获取 UserContext，并在目标线程中通过 {@code setValue()} 恢复。
     */
    @PostConstruct
    public void register() {
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(new ThreadLocalAccessor<UserContext>() {

                    /**
                     * 返回当前访问器在 ContextRegistry 中的唯一标识键，
                     * 用于区分不同类型的上下文。
                     *
                     * @return 键值 {@link AppConst#USER_CONTEXT}
                     */
                    @Override
                    public Object key() {
                        return AppConst.USER_CONTEXT;
                    }

                    /**
                     * 在当前线程中捕获并返回 UserContext。
                     * <p>
                     * 在创建上下文快照时被调用，返回值将绑定到快照中，
                     * 后续在目标线程中通过 {@link #setValue(UserContext)} 恢复。
                     *
                     * @return 当前线程的 UserContext，可能为 {@code null}
                     */
                    @Override
                    public UserContext getValue() {
                        return UserContextHolder.get();
                    }

                    /**
                     * 在目标线程中恢复 UserContext。
                     * <p>
                     * 当异步任务在目标线程中执行时，该方法被调用以将之前捕获的
                     * UserContext 设置到目标线程的 ThreadLocal 中。
                     *
                     * @param value 之前捕获的 UserContext
                     */
                    @Override
                    public void setValue(UserContext value) {
                        UserContextHolder.set(value);
                    }

                    /**
                     * 在目标线程中重置 UserContext。
                     * <p>
                     * 当目标线程中没有对应的上下文需要恢复时调用，
                     * 清空 ThreadLocal 防止上下文污染。
                     */
                    @Override
                    public void setValue() {
                        UserContextHolder.clear();
                    }
                });
    }
}
