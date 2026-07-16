package cn.codesensi.leaf.rbac.framework.annotation;

import cn.codesensi.leaf.rbac.common.enums.LoginEventType;

import java.lang.annotation.*;

/**
 * 登录日志注解。
 * <p>
 * 标注在登录/登出方法上，配合 AOP（{@link cn.codesensi.leaf.rbac.framework.aspect.LogLoginAspect}）
 * 自动记录登录日志，包括登录标识、请求元数据、执行耗时及结果状态。
 * <p>
 * 使用示例：
 * <pre>{@code
 * @LogLogin(EventType.LOGIN)
 * public LoginResultDTO loginAccount(LoginAccountDTO dto) {
 *     // ...
 * }
 *
 * @LogLogin(EventType.LOGOUT)
 * public void logout() {
 *     // ...
 * }
 * }</pre>
 *
 * @author codesensi
 * @see LoginEventType
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogLogin {

    /**
     * 事件类型，指定该方法是登录还是登出。
     * <ul>
     *   <li>{@link LoginEventType#LOGIN} — 账号密码登录，切面会自动从方法参数中提取用户名；</li>
     *   <li>{@link LoginEventType#LOGOUT} — 退出登录，切面在方法执行前获取 userId。</li>
     * </ul>
     */
    LoginEventType type();
}
