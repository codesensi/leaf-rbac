package cn.codesensi.leaf.rbac.framework.aspect;

import cn.codesensi.leaf.rbac.common.enums.YesNoEnum;
import cn.codesensi.leaf.rbac.system.dto.LoginAccountDTO;
import cn.codesensi.leaf.rbac.system.helper.LogLoginHelper;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 登录日志 AOP 切面 —— 通过环绕通知统一采集登录/登出日志，
 * 将日志记录与业务逻辑完全分离。
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogLoginAspect {

    private final LogLoginHelper logLoginHelper;

    /**
     * 环绕通知 —— 拦截账号密码登录，统一处理登录日志
     */
    @Around("execution(* cn.codesensi.leaf.rbac.system.service.impl.LoginServiceImpl.loginAccount(..))")
    public Object aroundLogin(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        String username = null;
        String params = null;
        Long userId = null;
        Integer status = YesNoEnum.YES.getCode();
        String errorMsg = null;

        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof LoginAccountDTO loginAccountDTO) {
                username = loginAccountDTO.getUsername();
                params = JSONUtil.toJsonStr(loginAccountDTO);
            }
        }

        try {
            Object result = pjp.proceed();
            // 登录成功：从当前会话获取 userId
            userId = StpUtil.getLoginIdAsLong();
            return result;
        } catch (Exception e) {
            status = YesNoEnum.NO.getCode();
            errorMsg = e.getMessage();
            throw e;
        } finally {
            logLoginHelper.publishLoginEvent(username, username, userId, status, errorMsg, params, start);
        }
    }

    /**
     * 环绕通知 —— 拦截退出登录，统一处理登出日志
     */
    @Around("execution(* cn.codesensi.leaf.rbac.system.service.impl.LoginServiceImpl.logout(..))")
    public Object aroundLogout(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        // 在 proceed 前获取 userId（登出后 token 失效无法获取）
        long userId = StpUtil.getLoginIdAsLong();

        Object result = pjp.proceed();

        logLoginHelper.publishLogoutEvent(userId, start);
        return result;
    }

}
