package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.common.enums.CaptchaType;
import cn.codesensi.leaf.rbac.framework.annotation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成验证码请求参数
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
@Schema(description = "生成验证码请求参数")
public class CaptchaRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码类型
     */
    @NotBlank(message = "验证码类型不能为空")
    @InEnum(enumClass = CaptchaType.class, message = "验证码类型不在指定范围内")
    @Schema(description = "验证码类型", example = "image")
    private String type;

    /**
     * 手机号 短信验证码登陆时必填
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
