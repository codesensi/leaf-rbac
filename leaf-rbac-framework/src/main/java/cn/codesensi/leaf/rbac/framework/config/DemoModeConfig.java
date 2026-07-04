package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.framework.interceptor.DemoModeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 演示模式拦截器配置
 *
 * @author codesensi
 */
@RequiredArgsConstructor
@Configuration
public class DemoModeConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    /**
     * 注册演示模式拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DemoModeInterceptor(appProperties))
                .addPathPatterns("/**");
    }
}
