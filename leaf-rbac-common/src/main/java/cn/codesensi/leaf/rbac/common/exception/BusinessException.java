package cn.codesensi.leaf.rbac.common.exception;

public class BusinessException extends BaseException {

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
