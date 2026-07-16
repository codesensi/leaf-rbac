package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.common.enums.LoginType;
import cn.codesensi.leaf.rbac.framework.annotation.InEnum;
import cn.codesensi.leaf.rbac.framework.annotation.LoginKeyProvider;
import cn.codesensi.leaf.rbac.framework.annotation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求参数
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
@Schema(description = "登录请求参数")
public class LoginRequest implements Serializable, LoginKeyProvider {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录类型
     * account: 账号密码登录
     * phone: 手机验证码登录
     * email: 邮箱验证码登录
     */
    @NotBlank(message = "登录类型不能为空")
    @InEnum(enumClass = LoginType.class, message = "登录类型不在指定范围内")
    @Schema(description = "登录类型", example = "account")
    private String type;

    /**
     * 用户账号
     */
    @Schema(description = "登录账号", example = "admin")
    private String username;

    /**
     * 手机号
     */
    @Phone(message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

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

    /**
     * 返回登录类型。
     * <p>
     * 实现自 {@link LoginKeyProvider}，供登录日志切面获取登录方式。
     * </p>
     *
     * @return 登录类型，如 account / phone / email
     */
    @Override
    public String getLoginType() {
        return type;
    }

    /**
     * 返回登录标识。
     * <p>
     * 实现自 {@link LoginKeyProvider}，供登录日志切面在不依赖具体 DTO 类型的前提下
     * 获取账号标识（用户名/手机号/邮箱）。
     * </p>
     *
     * @return 登录标识
     */
    @Override
    public String getLoginKey() {
        if (LoginType.ACCOUNT.getCode().equals(type)) {
            return username;
        } else if (LoginType.PHONE.getCode().equals(type)) {
            return phone;
        } else if (LoginType.EMAIL.getCode().equals(type)) {
            return email;
        }
        return type;
    }
}
