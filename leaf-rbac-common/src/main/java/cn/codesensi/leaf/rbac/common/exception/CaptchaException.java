package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

/**
 * 验证码异常 —— 表示验证码相关的业务错误。
 * <p>
 * 继承 {@link BusinessException}，用于验证码生成、发送、校验等场景的异常抛出。
 * </p>
 *
 * @author codesensi
 * @since 1.0
 */
public class CaptchaException extends BusinessException {

    /**
     * 构造默认状态码（500）的验证码异常。
     *
     * @param msg 错误描述信息
     */
    public CaptchaException(String msg) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg);
    }

    /**
     * 构造指定状态码的验证码异常。
     *
     * @param code 业务错误码
     * @param msg  错误描述信息
     */
    public CaptchaException(int code, String msg) {
        super(code, msg);
    }

}
