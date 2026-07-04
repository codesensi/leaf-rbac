package cn.codesensi.leaf.rbac.common.exception;

/**
 * 系统异常 —— 表示不可预料的系统级错误。
 * <p>
 * 继承 {@link BaseException}，用于抛出系统内部错误，例如：数据库连接失败、
 * 第三方服务调用异常、配置错误、文件读写失败等非业务逻辑层面的异常。
 * 由全局异常处理器 {@link cn.codesensi.leaf.rbac.framework.handler.GlobalExceptionHandler}
 * 统一捕获并返回标准错误响应，通常伴随着完整的堆栈信息以便排查。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 系统配置异常
 * throw new SystemException(4001, "数据库连接配置缺失");
 * // 第三方服务异常
 * throw new SystemException(4002, "短信服务调用失败", e);
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
public class SystemException extends BaseException {

    public SystemException(int code, String msg) {
        super(code, msg);
    }

    public SystemException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
