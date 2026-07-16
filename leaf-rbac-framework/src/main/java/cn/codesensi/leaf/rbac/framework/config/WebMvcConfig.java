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
 * Web MVC 拦截器链配置 —— 统一注册应用的拦截器并控制执行顺序。
 * <p>
 * 按注册顺序排列，即请求处理时的执行链路：
 * </p>
 * <ol>
 *   <li><b>SaInterceptor</b> — SaToken 鉴权拦截器（order=1），
 *       执行登录校验、封禁校验和超级管理员角色校验，
 *       同时初始化 SaTokenContext 线程级上下文，供后续 StpUtil 调用；</li>
 *   <li><b>{@link UserContextInterceptor}</b> — 用户上下文恢复拦截器（order=2），
 *       鉴权通过后，从 SaToken Session 读取登录时缓存的 {@code UserContext} 快照，
 *       绑定到 {@link cn.codesensi.leaf.rbac.framework.context.UserContextHolder} 的
 *       ThreadLocal 中，供当前请求各业务层零成本获取操作人信息；</li>
 *   <li><b>{@link DemoModeInterceptor}</b> — 演示模式保护拦截器（order=3），
 *       当 {@code app.demo-mode} 开启时拦截所有写操作（POST/PUT/DELETE），
 *       防止演示环境数据被篡改。</li>
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
     * 注册应用级拦截器链。
     * <p>
     * 三个拦截器按 order 升序依次执行，前一个放行后下一个才能执行：
     * </p>
     * <ul>
     *   <li><b>SaInterceptor (order=1)</b> — 路由级鉴权，放行 swagger、登录等公开路径，
     *       匹配到的路径要求登录 + 封禁检测 + 角色校验；</li>
     *   <li><b>UserContextInterceptor (order=2)</b> — 鉴权通过后，从 SaSession 加载
     *       用户上下文到 ThreadLocal，供 Controller/Service 层使用；</li>
     *   <li><b>DemoModeInterceptor (order=3)</b> — 演示模式下放行 GET/HEAD 及登录登出路径，
     *       拦截写操作请求。</li>
     * </ul>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. SaToken 鉴权拦截器：初始化 SaTokenContext + 登录校验 + 封禁校验 + 角色校验
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 所有请求（排除 swagger 等公开路径）需登录 + 账号未封禁
                    SaRouter.match(RbacConst.ROOT_PATH).notMatch(RbacConst.SWAGGER_PATH).check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkDisable(StpUtil.getLoginIdAsLong());
                    });
                    // 系统管理类接口（/sys/**、/log/**、/conf/**）需 superadmin 角色
                    SaRouter.match(RbacConst.SYS_PATH, RbacConst.LOG_PATH, RbacConst.CONF_PATH)
                            // 排除用户基本信息接口（登录后即可访问）
                            .notMatch(RbacConst.SYS_USER_INFO_PATH)
                            .check(r -> StpUtil.checkRole(RbacConst.ROLE_ADMIN_CODE));
                })).addPathPatterns(RbacConst.ROOT_PATH)
                .order(1);

        // 2. 用户上下文拦截器：鉴权通过后，从 SaSession 恢复 UserContext 到 ThreadLocal
        registry.addInterceptor(new UserContextInterceptor())
                .addPathPatterns(RbacConst.ROOT_PATH)
                .order(2);

        // 3. 演示模式拦截器：演示环境下仅允许查询和登录/登出，拒绝所有写操作
        registry.addInterceptor(new DemoModeInterceptor(appProperties))
                .addPathPatterns(RbacConst.ROOT_PATH)
                // 获取验证码、登录、登出接口不受演示模式限制
                .excludePathPatterns(RbacConst.CAPTCHA_PATH, RbacConst.LOGIN_PATH, RbacConst.LOGOUT_PATH)
                .order(3);
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
