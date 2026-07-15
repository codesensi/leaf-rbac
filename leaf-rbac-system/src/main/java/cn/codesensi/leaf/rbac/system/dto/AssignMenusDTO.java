package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 角色分配菜单请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@Schema(description = "角色分配菜单请求参数")
public class AssignMenusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    /**
     * 菜单ID列表
     */
    @Schema(description = "菜单ID列表", example = "[1,2,3]")
    private List<Long> menuIds;

}
