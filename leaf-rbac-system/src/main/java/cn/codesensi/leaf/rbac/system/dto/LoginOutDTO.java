package cn.codesensi.leaf.rbac.system.dto;

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
 */
@Data
@Accessors(chain = true)
public class LoginOutDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String accessToken;

    /**
     * 访问令牌过期时间（毫秒值）
     */
    @Schema(description = "访问令牌过期时间（毫秒值）")
    private Long expires;

    /**
     * 访问令牌名称
     */
    @Schema(description = "访问令牌名称")
    private String tokenName;

    /**
     * 访问令牌前缀
     */
    @Schema(description = "访问令牌前缀")
    private String tokenPrefix;

}
