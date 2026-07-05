package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码生成结果
 */
@Data
@Accessors(chain = true)
public class CaptchaOutDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @Schema(description = "唯一标识")
    private String key;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String result;
}
