package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.constants.RbacConst;
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
 * Sa-Token 拦截器配置
 *
 * @author codesensi
 * @since 2024/1/21 15:00
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 路由拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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
    }

    /**
     * Sa-Token 整合 jwt (Simple 简单模式)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

}
