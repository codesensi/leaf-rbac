package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class RoleSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 20, message = "角色名称长度不能超过20")
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 20, message = "角色编码长度不能超过20")
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
