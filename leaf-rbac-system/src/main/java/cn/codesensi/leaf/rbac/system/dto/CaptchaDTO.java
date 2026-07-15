package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成验证码请求参数
 */
@Data
@Accessors(chain = true)
public class CaptchaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @NotBlank(message = "验证码类型不能为空")
    @Schema(description = "验证码类型", example = "image")
    private String type;

    /**
     * 手机号 短信验证码登陆时必填
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
