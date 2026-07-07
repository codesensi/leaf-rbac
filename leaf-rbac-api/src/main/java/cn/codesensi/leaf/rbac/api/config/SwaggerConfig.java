package cn.codesensi.leaf.rbac.api.config;

import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 配置
 */
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    private final AppProperties appProperties;

    @Bean
    public OpenAPI openApi() {
        String appName = appProperties.getName();
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .description(appName + "文档")
                        .contact(new Contact().name(appProperties.getAuthor()))
                        .version("v" + appProperties.getVersion())
                );
    }
}
