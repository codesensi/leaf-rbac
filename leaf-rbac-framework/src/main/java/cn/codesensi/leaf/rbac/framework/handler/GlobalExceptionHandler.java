package cn.codesensi.leaf.rbac.framework.handler;

import cn.codesensi.leaf.rbac.common.core.Result;
import cn.codesensi.leaf.rbac.common.exception.*;
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

    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(ValidationException e) {
        log.error("参数验证异常：", e);
        return Result.badRequest(e.getMsg());
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常：", e);
        return Result.unauthorized(e.getMsg());
    }

    @ExceptionHandler(AuthorizationException.class)
    public Result<Void> handleAuthorizationException(AuthorizationException e) {
        log.error("授权异常：", e);
        return Result.forbidden(e.getMsg());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        String path = e.getResourcePath();
        log.warn("资源不存在：{}", path);
        return Result.notFound("[" + path + "]不存在");
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleGenericException(Exception e) {
        log.error("未处理的异常：", e);
        return Result.systemError(e.getMessage());
    }
}
