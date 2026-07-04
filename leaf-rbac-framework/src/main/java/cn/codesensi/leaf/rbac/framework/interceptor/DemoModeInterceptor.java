package cn.codesensi.leaf.rbac.framework.interceptor;

import cn.codesensi.leaf.rbac.common.exception.AuthorizationException;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 演示模式拦截器。
 * <p>
 * 当 {@link AppProperties#getDemoMode()} 为 {@code true} 时，
 * 拦截所有 POST、PUT、DELETE 等写操作请求，并抛出 {@link AuthorizationException}，
 * 防止演示环境中的数据被非授权修改。
 *
 * @author codesensi
 * @since 1.0
 */
@RequiredArgsConstructor
public class DemoModeInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (appProperties.getDemoMode()) {
            String method = request.getMethod();
            if (!RequestMethod.GET.name().equals(method) && !RequestMethod.HEAD.name().equals(method)) {
                throw new AuthorizationException("演示模式不允许操作哦~");
            }
        }
        return true;
    }
}
