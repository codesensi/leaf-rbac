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
 * 用户角色关联表 实体类。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_user_role", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class SysUserRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

}
