package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.framework.base.BaseEntity;
import cn.codesensi.leaf.rbac.framework.listener.MybatisFlexListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色信息表 实体类。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_role", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class SysRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Id
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 父角色ID
     */
    private Long pid;

    /**
     * 角色排序
     */
    private Integer sort;

    /**
     * 角色状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    private Integer sysFlag;

}
