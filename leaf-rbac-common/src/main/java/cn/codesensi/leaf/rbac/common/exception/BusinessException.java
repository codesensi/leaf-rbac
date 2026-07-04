package cn.codesensi.leaf.rbac.common.exception;

/**
 * 业务异常 —— 表示业务逻辑处理过程中可预见的错误。
 * <p>
 * 继承 {@link BaseException}，用于在 Service 层或 Controller 层主动抛出业务错误，
 * 例如：数据不满足业务规则、资源状态冲突、操作不被允许等。
 * 由全局异常处理器 {@link cn.codesensi.leaf.rbac.framework.handler.GlobalExceptionHandler}
 * 统一捕获并返回标准错误响应。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 业务校验失败
 * throw new BusinessException(2001, "该用户名已被注册");
 * // 带原始异常
 * throw new BusinessException(2002, "文件处理失败", e);
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
public class BusinessException extends BaseException {

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
