package cn.codesensi.leaf.rbac.system.enums;

import lombok.Getter;

/**
 * 验证码策略类型
 * sms-smsCaptchaStrategy-短信验证码
 * image-imageCaptchaStrategy-图形验证码
 */
@Getter
public enum CaptchaStrategyType {
    SMS("sms", "smsCaptchaStrategy", "短信验证码"),
    IMAGE("image", "imageCaptchaStrategy", "图形验证码"),
    ;

    /**
     * 验证码类型
     */
    private final String code;

    /**
     * 实现类名称
     */
    private final String className;

    /**
     * 验证码类型说明
     */
    private final String message;

    CaptchaStrategyType(String code, String className, String message) {
        this.code = code;
        this.className = className;
        this.message = message;
    }
}
