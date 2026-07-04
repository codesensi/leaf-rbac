package cn.codesensi.leaf.rbac.common.exception;

import cn.codesensi.leaf.rbac.common.core.ResultCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(String msg) {
        super(ResultCode.UNAUTHORIZED.getCode(), msg);
    }
}
