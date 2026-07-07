package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成验证码响应结果
 */
@Data
@Accessors(chain = true)
public class CaptchaOutDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码唯一标识
     */
    @Schema(description = "验证码唯一标识")
    private String captchaKey;

    /**
     * 验证码内容
     */
    @Schema(description = "验证码内容")
    private String captchaValue;
}
