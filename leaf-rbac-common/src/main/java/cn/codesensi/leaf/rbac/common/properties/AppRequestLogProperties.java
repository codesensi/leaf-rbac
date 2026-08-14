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
     * <p>
     * 注意：本配置为历史实现（仅供已废弃的 {@code RequestLogFilter} 使用），
     * 真实生效的脱敏是 {@code logbook.obfuscate.json-body-fields}。维护时请与之一致。
     */
    private List<String> sensitiveFields = Arrays.asList("accessToken", "password", "oldPassword", "newPassword", "confirmPassword");
}