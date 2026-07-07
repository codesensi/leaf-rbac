package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.system.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "角色信息表实体类")
@Table("sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Id
    @Schema(description = "角色ID")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String code;

    /**
     * 父角色ID
     */
    @Schema(description = "父角色ID")
    private Long pid;

    /**
     * 角色排序
     */
    @Schema(description = "角色排序")
    private Integer sort;

    /**
     * 角色状态:0-启用,1-禁用
     */
    @Schema(description = "角色状态:0-启用,1-禁用")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
