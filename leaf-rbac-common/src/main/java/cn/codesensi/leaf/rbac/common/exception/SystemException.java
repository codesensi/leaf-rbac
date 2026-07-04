package cn.codesensi.leaf.rbac.common.exception;

public class SystemException extends BaseException {

    public SystemException(int code, String msg) {
        super(code, msg);
    }

    public SystemException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
