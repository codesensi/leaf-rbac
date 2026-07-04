package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

/**
 * 认证异常 —— 表示用户身份认证失败。
 * <p>
 * 继承 {@link BusinessException}，当用户未登录、token 过期或无效时抛出此异常，
 * 固定使用 {@link cn.codesensi.leaf.rbac.common.core.ResultCode#UNAUTHORIZED} 状态码。
 * 由全局异常处理器 {@link cn.codesensi.leaf.rbac.framework.handler.GlobalExceptionHandler}
 * 拦截后返回 401 未授权的标准响应。
 *
 * @author codesensi
 * @since 1.0
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String msg) {
        super(ResultCode.UNAUTHORIZED.getCode(), msg);
    }
}
