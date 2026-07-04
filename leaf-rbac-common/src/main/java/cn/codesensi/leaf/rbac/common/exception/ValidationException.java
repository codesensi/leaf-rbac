package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

public class ValidationException extends BusinessException {

    public ValidationException(String msg) {
        super(ResultCode.BAD_REQUEST.getCode(), msg);
    }

    public ValidationException(int code, String msg) {
        super(code, msg);
    }
}
