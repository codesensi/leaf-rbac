package cn.codesensi.leaf.rbac.framework.filter;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.framework.context.UserContext;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用户上下文过滤器 —— 在每个 HTTP 请求的入口处，从 SaToken Session 恢复用户上下文到 ThreadLocal。
 * <p>
 * <b>执行顺序：</b><br>
 * Bucket4j 的 {@code DefaultServletRateLimitFilter} 默认注册在
 * {@code @Order(HIGHEST_PRECEDENCE + 10)}，早于 SaTokenContextFilter
 * 初始化上下文。因此本过滤器需在 SaTokenContextFilter (-104) 之后执行，
 * 确保 {@link StpUtil#isLogin()} 可用。
 * </p>
 * <p>
 * <b>过滤器链（@Order 值排序）：</b>
 * </p>
 * <ol>
 *   <li><b>TraceIdFilter (HIGHEST_PRECEDENCE)</b> — 链路追踪；</li>
 *   <li><b>CacheRequestBodyFilter (HIGHEST_PRECEDENCE + 1)</b> — 缓存请求体；</li>
 *   <li><b>Bucket4j DefaultServletRateLimitFilter (HIGHEST_PRECEDENCE + 10)</b> — Bucket4j 限流；
 *       （注：如需在 Bucket4j 中使用 {@link UserContextHolder}，需在 YAML 中配置
 *       {@code filter-order: -102} 使限流操作延后至上下文加载完成）</li>
 *   <li>…其他 Filter…</li>
 *   <li><b>SaTokenContextFilterForJakartaServlet (Order(-104))</b> — SaToken 初始化上下文；</li>
 *   <li><b>UserContextFilter (Order(-103))</b> — 恢复用户上下文（当前）；</li>
 *   <li>SaInterceptor → UserContextInterceptor → DemoModeInterceptor → Controller。</li>
 * </ol>
 * <p>
 * <b>降级策略：</b><br>
 * 当 SaToken Session 不可用时（如 Redis 故障），捕获异常并仅打印警告日志，
 * 不阻断请求，依赖方需对空 UserContext 进行防御性检查。
 * </p>
 *
 * @author codesensi
 * @see UserContextHolder
 * @see UserContext
 * @since 1.0
 */
@Slf4j
@Order(-103)
@Component
public class UserContextFilter implements Filter {

    /**
     * 在每个 HTTP 请求的入口处，从 SaToken Session 恢复用户上下文到 ThreadLocal，
     * 并在请求结束后清理，防止线程上下文污染。
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param chain    过滤器链
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            // 若已登录，从 SaToken Session 加载用户上下文到 ThreadLocal
            loadUserContextIfLoggedIn();
            chain.doFilter(request, response);
        } finally {
            // 确保清理，防止 Tomcat 线程池复用导致上下文串用
            UserContextHolder.clear();
        }
    }

    /**
     * 若当前请求已通过 SaToken 认证，从 Session 中读取用户上下文快照并绑定到当前线程。
     * <p>
     * 用户上下文在登录成功后由 {@code LoginService} 写入 SaToken Session，
     * 此处直接读取即可获得完整用户信息（ID、用户名、昵称等），无需额外的数据库查询。
     * </p>
     */
    private void loadUserContextIfLoggedIn() {
        try {
            if (StpUtil.isLogin()) {
                SaSession session = StpUtil.getSession();
                Object userContextObj = session.get(AppConst.USER_CONTEXT);
                if (userContextObj instanceof UserContext userContext) {
                    UserContextHolder.set(userContext);
                }
            }
        } catch (Exception e) {
            // SaSession 不可用时降级，不阻断请求
            log.warn("无法从 SaToken Session 加载用户上下文，当前请求将以未绑定用户上下文的状态执行", e);
        }
    }
}
