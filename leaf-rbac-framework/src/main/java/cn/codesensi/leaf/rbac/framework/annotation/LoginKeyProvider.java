package cn.codesensi.leaf.rbac.framework.annotation;

/**
 * 登录标识提供者 —— 登录 DTO 实现此接口，供切面获取当前登录的账号标识。
 * <p>
 * 框架的 {@link cn.codesensi.leaf.rbac.framework.aspect.LogLoginAspect}
 * 不依赖具体 DTO 类型，只通过此接口获取登录标识（账号名/手机号等），
 * 实现切面与业务模块的解耦。
 * </p>
 *
 * @author codesensi
 * @see LogLogin
 * @since 1.0
 */
public interface LoginKeyProvider {

    /**
     * 登录类型。
     *
     * @return 登录类型，不为 {@code null}
     */
    String getLoginType();

    /**
     * 当前登录请求的账号标识。
     * <p>
     * 对于账号密码登录，返回用户名；对于手机号登录，返回手机号。
     * 该值将作为登录日志的 {@code loginKey} 和 {@code username} 字段。
     * </p>
     *
     * @return 登录标识，不为 {@code null}
     */
    String getLoginKey();

}
