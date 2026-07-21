package cn.codesensi.leaf.rbac.framework.interceptor;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器 —— 在当前请求通过 SaToken 认证后，从 Session 恢复用户信息到 ThreadLocal。
 * <p>
 * <b>为什么是 Interceptor 而不是 Filter：</b><br>
 * SaToken 的认证鉴权通过 {@link cn.dev33.satoken.interceptor.SaInterceptor} 实现，
 * 这是一个 Spring MVC {@link HandlerInterceptor}。而 {@code SaTokenContext}（ThreadLocal 请求上下文）
 * 在 {@code SaInterceptor.preHandle()} 中才初始化。<br>
 * 若使用 Servlet Filter，在过滤器链阶段调用 {@link StpUtil} 的任意方法都会抛出
 * {@code SaTokenContextException: 上下文尚未初始化}。<br>
 * 因此本拦截器必须在 {@code SaInterceptor} 之后执行，利用 {@link StpUtil} 读取已认证的会话信息。
 * </p>
 * <p>
 * <b>处理流程：</b>
 * </p>
 * <ol>
 *   <li>{@link #preHandle} — 检查当前请求是否已通过 SaToken 认证；
 *       若是，从 SaSession 读取 {@link UserContext} 快照并绑定到 {@link UserContextHolder}；</li>
 *   <li>Controller 执行业务逻辑，各层可通过 {@code UserContextHolder.get()} 获取当前用户信息；</li>
 *   <li>{@link #afterCompletion} — 清理 ThreadLocal，防止 Tomcat 线程池复用导致上下文串用。
 *       此方法即使在异常场景也会被调用，无需额外的 finally 块。</li>
 * </ol>
 * <p>
 * <b>降级策略：</b><br>
 * 当 SaToken Session 不可用时（如 Redis 故障），捕获异常并仅打印警告日志，
 * 不阻断请求，依赖方（如 {@link cn.codesensi.leaf.rbac.framework.listener.MybatisFlexListener}）
 * 会对空值进行防御性检查。
 * </p>
 *
 * @author codesensi
 * @see UserContextHolder
 * @see UserContext
 * @since 1.0
 */
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前 —— 从 SaToken Session 恢复用户上下文。
     * <p>
     * 若当前请求已通过 SaToken 认证，从 Session 中读取登录时缓存的用户快照，
     * 并绑定到当前线程的 ThreadLocal 中，供后续业务逻辑使用。
     * 未认证的请求（如登录接口、公开接口）直接放行。
     *
     * @param request  当前 HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理请求的目标
     * @return 始终返回 {@code true}，不阻断请求
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        if (StpUtil.isLogin()) {
            loadUserContextFromSession();
        }
        return true;
    }

    /**
     * 请求完成后 —— 清理 ThreadLocal 中的用户上下文。
     * <p>
     * 即使 {@link #preHandle} 或 Controller 执行过程中发生异常，
     * Spring 仍会保证调用此方法，确保线程池复用安全。
     *
     * @param request  当前 HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理请求的目标
     * @param ex       处理器执行过程中抛出的异常，无异常时为 {@code null}
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        UserContextHolder.clear();
    }

    /**
     * 从 SaToken Session 中读取用户上下文快照并绑定到当前线程。
     * <p>
     * 用户上下文在登录成功后由 {@code LoginService} 写入 SaToken Session，
     * 此处直接读取即可获得完整用户信息（ID、用户名、昵称等），无需额外的数据库查询。
     * </p>
     * <p>
     * 当 SaSession 不可用时（如 Redis 连接断开），捕获异常并降级处理：
     * 仅打印警告日志，让请求继续执行，依赖方会对空 UserContext 进行防御性检查。
     * </p>
     */
    private void loadUserContextFromSession() {
        try {
            SaSession session = StpUtil.getSession();
            Object userContextObj = session.get(AppConst.USER_CONTEXT);
            if (userContextObj instanceof UserContext userContext) {
                UserContextHolder.set(userContext);
            }
        } catch (Exception e) {
            // SaSession 不可用时降级，不阻断请求
            log.warn("无法从 SaToken Session 加载用户上下文，当前请求将以未绑定用户上下文的状态执行", e);
        }
    }
}
