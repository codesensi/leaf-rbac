package cn.codesensi.leaf.rbac.framework.annotation;

import cn.codesensi.leaf.rbac.common.enums.OperateType;

import java.lang.annotation.*;

/**
 * 操作日志注解。
 * <p>
 * 标注在 Controller 方法上，配合 AOP（{@link cn.codesensi.leaf.rbac.framework.aspect.LogOperateAspect}）
 * 自动记录接口调用日志，包括操作模块、操作类型、描述、请求参数和响应数据等。
 * <p>
 * 使用示例：
 * <pre>{@code
 * @LogOperate(module = "用户管理", type = "INSERT", desc = "新增用户")
 * @PostMapping("save")
 * public Result<Void> save(@RequestBody SysUser user) {
 *     return sysUserService.save(user);
 * }
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperate {


    /**
     * 操作所属模块名称，用于按模块分类统计和查询。
     * <p>
     * 例如："用户管理"、"角色管理"、"系统配置"等。
     */
    String module() default "";

    /**
     * 操作类型标识，用于区分不同的业务操作。
     * <p>
     * 建议使用 {@code INSERT}、{@code UPDATE}、{@code DELETE}、{@code SELECT}、{@code EXPORT}、{@code IMPORT} 等常量值。
     */
    OperateType type() default OperateType.UNKNOWN;

    /**
     * 操作描述，简要说明该操作的业务含义。
     * <p>
     * 会写入操作日志表，用于后续审计查询。如 "新增用户"、"修改角色"、"删除部门"。
     */
    String desc() default "";


    /**
     * 是否记录请求参数。
     * <p>
     * 为 {@code true} 时将请求参数序列化为 JSON 存入日志，
     * 敏感接口或参数数据量过大时可设为 {@code false} 避免记录。
     */
    boolean recordParams() default true;

    /**
     * 是否记录响应结果。
     * <p>
     * 为 {@code true} 时将响应结果序列化为 JSON 存入日志，
     * 对大数据量或敏感数据的响应可设为 {@code false} 避免性能损耗或信息泄露。
     */
    boolean recordResult() default false;

    /**
     * 需要忽略的参数名列表。
     * <p>
     * 配置后这些参数不会记录到日志中，适用于 {@code password}、{@code token}、{@code secret} 等敏感字段。
     * <p>
     * 使用示例：
     * <pre>{@code
     * @LogOperate(module = "用户管理", type = "INSERT", desc = "新增用户",
     *             ignoreFields = {"password", "confirmPassword"})
     * }</pre>
     */
    String[] ignoreFields() default {};
}
