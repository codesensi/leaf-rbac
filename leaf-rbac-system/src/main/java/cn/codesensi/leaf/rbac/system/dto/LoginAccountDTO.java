package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账号密码登录请求参数
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
@Accessors(chain = true)
@Schema(description = "账号密码登录请求参数")
public class LoginAccountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @Schema(description = "登录账号", example = "admin")
    private String username;

    /**
     * 用户密码
     */
    @Schema(description = "登录密码", example = "123456")
    private String password;

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
