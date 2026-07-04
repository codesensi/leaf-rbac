package cn.codesensi.leaf.rbac.framework.filter;

import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 请求体缓存过滤器 —— 将原始请求包装为 {@link ContentCachingRequestWrapper}，缓存请求体内容。
 * <p>
 * <b>为什么需要：</b><br>
 * Servlet 的 {@code InputStream} 只能被读取一次（默认流读取完毕后就关闭了），
 * 但项目中 {@link cn.codesensi.leaf.rbac.framework.util.RequestUtil#getRequestBody(HttpServletRequest)}、
 * 操作日志等场景需要多次读取请求体。<br>
 * 本过滤器将请求包装为 {@link ContentCachingRequestWrapper}，将原始请求体内容缓存到内存字节数组中，
 * 后续在任何位置都可以通过 {@code wrapper.getContentAsByteArray()} 重复获取。
 * <p>
 * <b>注意：</b><br>
 * - 文件上传请求（{@code multipart/form-data}）会跳过包装，避免大文件导致内存溢出；<br>
 * - 通过 {@code requestCacheLimit} 限制最大缓存字节数，超出部分不会被缓存，但仍可在一次读取中正常使用。
 * <p>
 * <b>执行顺序：</b><br>
 * 设为最高优先级（{@link Ordered#HIGHEST_PRECEDENCE}），确保在任何读取请求体的逻辑之前完成包装。
 *
 * @author codesensi
 * @see ContentCachingRequestWrapper
 * @see cn.codesensi.leaf.rbac.framework.util.RequestUtil#getRequestBody(HttpServletRequest)
 * @since 1.0
 */
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CacheRequestBodyFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;

    /**
     * 将请求包装为 {@link ContentCachingRequestWrapper}，缓存请求体以便重复读取。
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>如果是 {@code multipart/form-data} 请求（文件上传），跳过包装直接放行，
     *       避免大文件占用内存；</li>
     *   <li>其他请求用 {@link ContentCachingRequestWrapper} 包装后传递到过滤器链下游。</li>
     * </ol>
     *
     * @param request     原始 HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 文件上传请求跳过包装，避免大文件缓存导致内存溢出
        if (request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 将原始请求包装为可缓存请求体的包装器，传递给后续过滤器
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, appProperties.getRequestCacheLimit());
        filterChain.doFilter(wrappedRequest, response);
    }
}
