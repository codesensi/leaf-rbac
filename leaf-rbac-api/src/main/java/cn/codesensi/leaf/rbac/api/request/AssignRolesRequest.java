package cn.codesensi.leaf.rbac.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户配置角色请求参数
 */

@Data
@Schema(description = "用户配置角色请求")
public class AssignRolesRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    /**
     * 角色ID列表（空列表表示移除所有角色）
     */
    @Schema(description = "角色ID列表（空列表表示移除所有角色）", example = "[1, 2, 3]")
    private List<Long> roleIds;
}