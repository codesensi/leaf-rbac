package cn.codesensi.leaf.rbac.framework.handler;

import cn.codesensi.leaf.rbac.common.core.Result;
import cn.codesensi.leaf.rbac.common.exception.AuthorizationException;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.exception.SystemException;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 *
 * @author codesensi
 * @since 2020/06/07
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public Result<Void> handleAuthorizationException(AuthorizationException e) {
        log.error("授权异常：", e);
        return Result.forbidden(e.getMsg());
    }

    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(ValidationException e) {
        log.error("参数验证异常：", e);
        return Result.badRequest(e.getMsg());
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(SystemException.class)
    public Result<Void> handleSystemException(SystemException e) {
        log.error("系统异常：", e);
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(SaTokenException.class)
    public Result<Void> handleSaTokenException(SaTokenException e) {
        log.error("授权异常：", e);
        switch (e) {
            case NotLoginException notLoginException -> {
                if (NotLoginException.TOKEN_FREEZE.equals(notLoginException.getType())) {
                    return Result.forbidden("账号已被冻结");
                }
                return Result.unauthorized("未登录或登录已过期");
            }
            case NotRoleException ignored1 -> {
                return Result.forbidden("角色认证校验未通过");
            }
            case NotPermissionException ignored2 -> {
                return Result.forbidden("权限认证校验未通过");
            }
            default -> {
                return Result.forbidden(e.getMessage());
            }
        }
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        String path = e.getResourcePath();
        log.warn("资源不存在：{}", path);
        return Result.notFound("[" + path + "]不存在");
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未处理的异常：", e);
        return Result.systemError(e.getMessage());
    }
}
