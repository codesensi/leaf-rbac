package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存角色请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@Schema(description = "保存角色请求参数")
public class RoleSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码", example = "admin")
    private String code;

    /**
     * 角色排序
     */
    @Schema(description = "角色排序", example = "1")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

}
