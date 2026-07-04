package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

/**
 * 授权异常 —— 表示用户权限不足。
 * <p>
 * 继承 {@link BusinessException}，当已认证用户尝试访问无权限的资源或执行无权限的操作时
 * 抛出此异常，固定使用 {@link cn.codesensi.leaf.rbac.common.core.ResultCode#FORBIDDEN} 状态码。
 * 由全局异常处理器 {@link cn.codesensi.leaf.rbac.framework.handler.GlobalExceptionHandler}
 * 拦截后返回 403 禁止访问的标准响应。
 *
 * @author codesensi
 * @since 1.0
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String msg) {
        super(ResultCode.FORBIDDEN.getCode(), msg);
    }
}
