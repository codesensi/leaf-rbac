package cn.codesensi.leaf.rbac.system.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成验证码请求参数
 */
@Data
public class CaptchaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    private String type;

    /**
     * 手机号 短信验证码登陆时必填
     */
    private String phone;
}
