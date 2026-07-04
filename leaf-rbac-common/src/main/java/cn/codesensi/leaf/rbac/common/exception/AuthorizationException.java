package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

public class AuthorizationException extends BusinessException {

    public AuthorizationException(String msg) {
        super(ResultCode.FORBIDDEN.getCode(), msg);
    }
}
