package cn.codesensi.leaf.rbac.framework.context;

/**
 * 当前登录用户上下文的持有者 —— 通过 {@link ThreadLocal} 实现请求级别的用户信息隔离。
 * <p>
 * 在每个 HTTP 请求的过滤器中初始设置，同一请求内的各层（Controller → Service → Mapper）
 * 可通过此类获取当前操作人信息，无需在方法参数中显式传递。
 * </p>
 * <p>
 * <b>使用注意：</b>
 * <ul>
 *   <li>请求结束后必须在过滤器的 {@code finally} 块中调用 {@link #clear()}，否则 Tomcat 线程池
 *       复用时会污染下一个请求的上下文（即 ThreadLocal 内存泄漏）；</li>
 *   <li>异步线程中无法直接获取，需通过 {@code ThreadPoolConfig.AsyncContext} 提前快照并传递。</li>
 * </ul>
 * </p>
 *
 * @author codesensi
 * @since 1.0
 */
public class UserContextHolder {

    /**
     * 线程局部变量，存储当前请求线程的 {@link UserContext} 实例。
     * <p>
     * {@link ThreadLocal} 保证每个线程拥有独立的上下文副本，线程间互不干扰。
     */
    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程的操作人用户上下文。
     * <p>
     * 通常在 {@code Filter} 或 {@code Interceptor} 中用户认证通过后调用，
     * 将解析出的用户信息绑定到当前请求线程。
     * 同一请求内后续所有操作均可通过 {@link #get()} 获取。
     *
     * @param userContext 用户上下文（包含用户 ID 等信息），不为 {@code null}
     */
    public static void set(UserContext userContext) {
        USER_CONTEXT.set(userContext);
    }

    /**
     * 获取当前线程的操作人用户上下文。
     * <p>
     * 在各业务层中调用以获取当前请求的操作人信息。
     *
     * @return 用户上下文，未设置时返回 {@code null}
     */
    public static UserContext get() {
        return USER_CONTEXT.get();
    }

    /**
     * 清理当前线程的用户上下文，防止 ThreadLocal 内存泄漏。
     * <p>
     * <b>必须</b>在当前 HTTP 请求结束前调用（通常在 {@code Filter} 的 {@code finally} 块中），
     * 否则 Tomcat 线程池复用时，下一个请求会读到上一个请求的用户信息，造成严重的数据安全问题。
     * <p>
     * 异步线程任务执行完毕后也需要调用此方法。
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }

    /**
     * 便捷方法 —— 直接从当前线程上下文中获取用户 ID。
     * <p>
     * 等效于 {@code get() != null ? get().getUserId() : null}，
     * 是最常用的静态方法，业务代码中通常直接调用此方法而非显式使用 {@link #get()}。
     *
     * @return 用户 ID，未设置时返回 {@code null}
     */
    public static Long getUserId() {
        UserContext userContext = get();
        return userContext != null ? userContext.getUserId() : null;
    }

    /**
     * 便捷方法 —— 直接从当前线程上下文中获取用户名。
     * <p>
     * 等效于 {@code get() != null ? get().getUsername() : null}。
     *
     * @return 用户名，未设置时返回 {@code null}
     */
    public static String getUsername() {
        UserContext userContext = get();
        return userContext != null ? userContext.getUsername() : null;
    }

}
