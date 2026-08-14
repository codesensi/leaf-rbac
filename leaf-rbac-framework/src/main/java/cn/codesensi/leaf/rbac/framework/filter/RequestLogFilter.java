package cn.codesensi.leaf.rbac.framework.filter;

import cn.codesensi.leaf.rbac.common.properties.AppRequestLogProperties;
import cn.codesensi.leaf.rbac.framework.util.SensitiveMaskUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 请求日志过滤器
 * <p>
 * <b>已废弃（DEPRECATED）</b>：本过滤器为历史实现，已被 Logbook（{@code logbook.obfuscate}）
 * 取代，当前不注册（{@code // @Component}），请勿再启用。
 * <p>
 * 请求日志职责边界：traceId 由 {@link TraceIdFilter} 生成（MDC {@code traceId}）、
 * 请求体缓存由 {@link CacheRequestBodyFilter}、请求/响应日志与脱敏统一由 Logbook 承担。
 */
@Deprecated
@RequiredArgsConstructor
@Slf4j
// @Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RequestLogFilter extends OncePerRequestFilter {

    private final AppRequestLogProperties appRequestLogProperties;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull FilterChain chain) throws ServletException, IOException {

        if (!appRequestLogProperties.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;
            // 从 CacheRequestBodyFilter 中获取请求体（需确保已缓存）
            if (request instanceof ContentCachingRequestWrapper requestWrapper) {
                // 打印请求日志（在 finally 中确保即使抛出异常也能打印）
                logRequest(requestWrapper, responseWrapper, duration);
            }
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 打印请求日志
     */
    private void logRequest(ContentCachingRequestWrapper request,
                            ContentCachingResponseWrapper response,
                            long duration) {

        // 获取请求体和响应体
        byte[] reqBody = request.getContentAsByteArray();
        byte[] respBody = response.getContentAsByteArray();
        String reqBodyStr = reqBody.length > 0 ? new String(reqBody, StandardCharsets.UTF_8) : null;
        String respBodyStr = respBody.length > 0 ? new String(respBody, StandardCharsets.UTF_8) : null;

        List<String> sensitiveFields = appRequestLogProperties.getSensitiveFields();
        // 请求参数脱敏
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            queryString = SensitiveMaskUtil.maskQueryString(queryString, sensitiveFields);
        }

        // 请求体脱敏
        if (StrUtil.isNotBlank(reqBodyStr)) {
            reqBodyStr = SensitiveMaskUtil.maskJson(reqBodyStr, sensitiveFields);
        }

        // 响应体脱敏
        boolean includeResponseBody = appRequestLogProperties.isIncludeResponseBody();
        if (StrUtil.isNotBlank(respBodyStr) && includeResponseBody) {
            respBodyStr = SensitiveMaskUtil.maskJson(respBodyStr, sensitiveFields);
            // 截断处理
            int maxBodyLength = appRequestLogProperties.getMaxBodyLength();
            if (respBodyStr.length() > maxBodyLength) {
                respBodyStr = StrUtil.subPre(respBodyStr, maxBodyLength).concat("...(truncated)");
            }
        }

        String logBuilder = "\n========== Request Log ==========\n" +
                "Method   : " + request.getMethod() + "\n" +
                "URL      : " + request.getRequestURL() + "\n" +
                "Status   : " + response.getStatus() + "\n" +
                "Duration : " + duration + "ms\n";
        if (StrUtil.isNotBlank(queryString)) {
            logBuilder += "Params   : " + queryString + "\n";
        }
        if (StrUtil.isNotBlank(reqBodyStr)) {
            logBuilder += "Body     : " + reqBodyStr + "\n";
        }
        if (StrUtil.isNotBlank(respBodyStr) && includeResponseBody) {
            logBuilder += "Response : " + respBodyStr + "\n";
        }
        logBuilder += "=================================\n";
        // 统一打印（使用 INFO 级别，便于查看）
        log.info(logBuilder);
    }
}
