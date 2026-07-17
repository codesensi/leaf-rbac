package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.request-log")
public class AppRequestLogProperties {

    /**
     * 是否启用请求日志
     */
    private boolean enabled = true;

    /**
     * 是否包含响应体
     */
    private boolean includeResponseBody = false;

    /**
     * 响应体最大长度
     */
    private int maxBodyLength = 1000;

    /**
     * 敏感字段列表
     */
    private List<String> sensitiveFields = Arrays.asList("password", "accessToken");
}