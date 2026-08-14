package cn.codesensi.leaf.rbac.framework.filter;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 链路追踪 ID（TraceId）过滤器。
 * <p>
 * 在每次 HTTP 请求的入口处，为当前线程注入一个全局唯一的 {@code traceId}，
 * 并写入 SLF4J 的 MDC（Mapped Diagnostic Context）上下文中，
 * 使得该请求在整个处理链路中的所有日志都能关联到同一个 traceId，
 * 便于问题追踪和日志聚合。
 * <p>
 * <b>核心特性：</b>
 * <ul>
 *   <li><b>跨服务传递</b> — 优先从请求头 {@code X-Trace-Id} 获取 traceId，
 *       支持上游服务向下游透传，形成完整的调用链路；</li>
 *   <li><b>本地生成</b> — 如果请求头中不存在 traceId，则使用 {@link IdUtil#fastSimpleUUID()}
 *       自动生成一个简洁的 UUID；</li>
 *   <li><b>防止污染</b> — 在 {@code finally} 块中确保清理 MDC 上下文，
 *       避免线程池复用导致的 traceId 串用问题；</li>
 *   <li><b>最早执行</b> — 通过 {@code @Order(Ordered.HIGHEST_PRECEDENCE + 1)} 确保本过滤器
 *       在过滤器链中优先执行，使后续所有组件都能拿到 traceId。</li>
 * </ul>
 * <p>
 * 配合日志配置中的 {@code [%X{traceId}]} 占位符，即可在日志中打印 traceId。
 * 示例日志输出：{@code 2026-06-29 10:00:00.123 INFO [main] [a1b2c3d4] com.example.Service : ...}
 *
 * <b>请求日志职责边界（与 {@link CacheRequestBodyFilter}、Logbook 分工，勿重复叠加）：</b>
 * <ul>
 *   <li><b>traceId 生成/透传/清理</b> — 仅本过滤器负责（写入 MDC {@code traceId}）；</li>
 *   <li><b>请求体缓存（供业务多读）</b> — {@link CacheRequestBodyFilter} 负责；</li>
 *   <li><b>请求/响应体日志 + 敏感字段脱敏</b> — 统一由 Logbook（{@code logbook.obfuscate}）承担，
 *       禁止再启用 {@link RequestLogFilter}（历史实现，已废弃）。</li>
 * </ul>
 *
 * @author codesensi
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceIdFilter implements Filter {

    /**
     * 对每个 HTTP 请求执行链路追踪 ID 的注入与清理。
     * <p>
     * 处理流程：
     * <ol>
     *   <li>生成或获取 traceId（优先从请求头获取，支持跨服务传递）；</li>
     *   <li>将 traceId 放入 MDC 上下文；</li>
     *   <li>放行请求，执行后续过滤器及业务逻辑；</li>
     *   <li>在 {@code finally} 块中清理 MDC，避免线程上下文污染。</li>
     * </ol>
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
            // 生成或获取 traceId（优先从请求头获取，支持跨服务传递）
            String traceId = generateTraceId(request);
            MDC.put(AppConst.TRACE_ID, traceId);
            chain.doFilter(request, response);
        } finally {
            // 确保清理，避免线程复用导致上下文污染
            MDC.remove(AppConst.TRACE_ID);
        }
    }

    /**
     * 生成或获取调用链路的 traceId。
     * <p>
     * 如果当前请求是 HTTP 请求且请求头中包含 {@code X-Trace-Id}，
     * 则直接取用（用于跨服务链路透传）；
     * 否则生成一个新的简洁 UUID 作为 traceId。
     *
     * @param request 当前请求
     * @return 有效的 traceId
     */
    private String generateTraceId(ServletRequest request) {
        if (request instanceof HttpServletRequest httpRequest) {
            // 尝试从请求头获取（支持上游服务传递）
            String headerTraceId = httpRequest.getHeader("X-Trace-Id");
            if (StrUtil.isNotBlank(headerTraceId)) {
                return headerTraceId;
            }
        }
        // 生成新的 traceId
        return IdUtil.fastSimpleUUID();
    }
}
