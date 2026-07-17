package cn.codesensi.leaf.rbac.framework.filter;

import cn.codesensi.leaf.rbac.common.properties.AppRequestLogProperties;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求日志过滤器
 */
@RequiredArgsConstructor
@Slf4j
@Component
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

        // 1. 构建基本信息
        StringBuilder logBuilder = new StringBuilder();

        logBuilder.append("\n========== Request Log ==========\n");
        logBuilder.append("Method   : ").append(request.getMethod()).append("\n");
        logBuilder.append("URL      : ").append(request.getRequestURL()).append("\n");
        logBuilder.append("Status   : ").append(response.getStatus()).append("\n");
        logBuilder.append("Duration : ").append(duration).append("ms\n");

        // 2. 请求参数（Query String + 表单参数）
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            String params = paramMap.entrySet()
                    .stream()
                    // 过滤掉值为 null 的参数
                    .filter(entry -> entry.getValue() != null)
                    .map(entry -> entry.getKey()
                            .concat("=")
                            .concat(String.join(",", entry.getValue())))
                    .collect(Collectors.joining("&"));
            // 脱敏
            params = sensitiveMask(params);
            logBuilder.append("Params   : ").append(params).append("\n");
        }

        // 3. 请求体（Body）
        byte[] requestBody = request.getContentAsByteArray();
        if (requestBody.length > 0) {
            String body = new String(requestBody, StandardCharsets.UTF_8);
            // 脱敏
            body = sensitiveMask(body);
            logBuilder.append("Body     : ").append(body).append("\n");
        }

        // 4. 响应体（可选，生产环境建议关闭）
        if (appRequestLogProperties.isIncludeResponseBody()) {
            byte[] responseBody = response.getContentAsByteArray();
            if (responseBody.length > 0) {
                String resp = new String(responseBody, StandardCharsets.UTF_8);
                resp = truncateAndMask(resp);
                logBuilder.append("Response : ").append(resp).append("\n");
            }
        }

        logBuilder.append("=================================\n");

        // 统一打印（使用 INFO 级别，便于查看）
        log.info(logBuilder.toString());
    }

    /**
     * 脱敏处理
     */
    private String sensitiveMask(String text) {
        if (text == null) {
            return null;
        }
        for (String field : appRequestLogProperties.getSensitiveFields()) {
            // 简单脱敏：将 password=123 替换为 password=***
            text = text.replaceAll(field + "=[^&]*", field + "=***");
        }
        return text;
    }

    /**
     * 截断处理
     */
    private String truncateAndMask(String text) {
        if (text == null) {
            return null;
        }
        // 先脱敏
        String masked = sensitiveMask(text);
        // 再截断
        int maxLen = appRequestLogProperties.getMaxBodyLength();
        if (masked.length() > maxLen) {
            return masked.substring(0, maxLen) + "... (truncated)";
        }
        return masked;
    }
}
