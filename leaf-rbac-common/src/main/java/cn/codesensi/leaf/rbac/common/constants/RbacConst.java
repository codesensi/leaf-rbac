package cn.codesensi.leaf.rbac.common.constants;

/**
 * 权限常量
 */
public class RbacConst {

    /**
     * 超级管理员名称
     */
    public static final String USER_ADMIN_NAME = "admin";

    /**
     * 超级管理员角色标识
     */
    public static final String ROLE_ADMIN_CODE = "admin";

    /**
     * 超级管理员权限码
     */
    public static final String PERM_ADMIN_CODE = "*:*:*";

    /**
     * 根接口路径
     */
    public static final String ROOT_PATH = "/**";

    /**
     * SWAGGER接口路径
     */
    public static final String[] SWAGGER_PATH = {"/doc.html", "/favicon.ico", "/v3/api-docs/**", "/webjars/**"};

    /**
     * 系统管理接口路径
     */
    public static final String SYS_PATH = "/sys/**";
}
