package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@Schema(description = "登录响应结果")
public class LoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIs...")
    private String accessToken;

    /**
     * 访问令牌过期时间（毫秒值）
     */
    @Schema(description = "访问令牌过期时间（毫秒值）", example = "86400000")
    private Long expires;

    /**
     * 访问令牌名称
     */
    @Schema(description = "访问令牌名称", example = "Authorization")
    private String tokenName;

    /**
     * 访问令牌前缀
     */
    @Schema(description = "访问令牌前缀", example = "Bearer")
    private String tokenPrefix;
}
