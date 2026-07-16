package cn.codesensi.leaf.rbac.system.dto;

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
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录类型
     * account: 账号密码登录
     * phone: 手机验证码登录
     * email: 邮箱验证码登录
     */
    private String type;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户密码（账号登录时必填）
     */
    private String password;

    /**
     * 验证码唯一标识
     */
    private String captchaKey;

    /**
     * 验证码内容
     */
    private String captchaValue;

}
