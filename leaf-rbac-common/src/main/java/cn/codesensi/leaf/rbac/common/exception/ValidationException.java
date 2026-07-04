package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

/**
 * 参数校验异常 —— 表示请求参数不符合校验规则。
 * <p>
 * 继承 {@link BusinessException}，用于参数校验失败场景，例如：必填字段为空、格式不正确、
 * 数值超出范围等。默认使用 {@link cn.codesensi.leaf.rbac.common.core.ResultCode#BAD_REQUEST}
 * 状态码，也可通过 {@link #ValidationException(int, String)} 指定自定义错误码。
 * 由全局异常处理器 {@link cn.codesensi.leaf.rbac.framework.handler.GlobalExceptionHandler}
 * 拦截后返回 400 参数错误的标准响应。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 使用默认 400 状态码
 * throw new ValidationException("用户名不能为空");
 * // 指定自定义错误码
 * throw new ValidationException(1001, "手机号格式不正确");
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
public class ValidationException extends BusinessException {

    public ValidationException(String msg) {
        super(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    public ValidationException(int code, String msg) {
        super(code, msg);
    }
}
