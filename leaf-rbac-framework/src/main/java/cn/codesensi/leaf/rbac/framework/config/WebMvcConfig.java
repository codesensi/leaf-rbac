package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.framework.interceptor.DemoModeInterceptor;
import cn.codesensi.leaf.rbac.framework.interceptor.UserContextInterceptor;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 鉴权及用户上下文拦截器配置。
 * <p>
 * 注册顺序即为执行顺序：
 * <ol>
 *   <li>{@code SaInterceptor} — 鉴权（登录校验、封禁校验、角色校验），
 *       同时初始化 {@code SaTokenContext} 上下文；</li>
 *   <li>{@link UserContextInterceptor} — 从 SaToken Session 恢复用户上下文快照到
 *       {@link cn.codesensi.leaf.rbac.framework.context.UserContextHolder}，
 *       使业务层可直接获取当前操作人的完整信息。</li>
 * </ol>
 *
 * @author codesensi
 * @since 2024/1/21 15:00
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    /**
     * 注册鉴权拦截器和用户上下文拦截器，按注册顺序依次执行。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. SaToken 鉴权拦截器：登录校验 + 封禁校验 + 角色校验
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验 + 封禁校验
            SaRouter.match(RbacConst.ROOT_PATH).notMatch(RbacConst.SWAGGER_PATH).check(r -> {
                StpUtil.checkLogin();
                StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
            });
            // 系统功能：超级管理员角色
            SaRouter.match(RbacConst.SYS_PATH, RbacConst.LOG_PATH, RbacConst.CONF_PATH)
                    // 忽略用户信息接口
                    .notMatch(RbacConst.SYS_USER_INFO_PATH)
                    .check(r -> StpUtil.checkRole(RbacConst.ROLE_ADMIN_CODE));
        })).addPathPatterns(RbacConst.ROOT_PATH);

        // 2. 用户上下文拦截器：从 SaToken Session 恢复完整用户信息到 ThreadLocal
        registry.addInterceptor(new UserContextInterceptor()).addPathPatterns(RbacConst.ROOT_PATH);

        // 3. Demo 模式拦截器：只读不允许修改数据
        registry.addInterceptor(new DemoModeInterceptor(appProperties))
                .addPathPatterns(RbacConst.ROOT_PATH)
                // 获取验证码、登录接口允许演示模式下操作
                .excludePathPatterns(RbacConst.CAPTCHA_PATH, RbacConst.LOGIN_PATH, RbacConst.LOGOUT_PATH);
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
