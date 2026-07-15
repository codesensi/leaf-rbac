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
public class CaptchaResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码唯一标识
     */
    @Schema(description = "验证码唯一标识", example = "7c6c8b6e8b6e4b6e8b6e7c6c8b6e8b6e")
    private String captchaKey;

    /**
     * 验证码内容
     */
    @Schema(description = "验证码内容", example = "123456")
    private String captchaValue;
}
