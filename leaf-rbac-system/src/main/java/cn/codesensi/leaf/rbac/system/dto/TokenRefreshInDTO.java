package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新访问令牌参数
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@Accessors(chain = true)
public class TokenRefreshInDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌")
    private String refreshToken;

}
