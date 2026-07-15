package cn.codesensi.leaf.rbac.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class AssignMenusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long roleId;

    /**
     * 菜单ID列表
     */
    @Schema(description = "菜单ID列表", example = "[1,2,3]")
    @JsonSerialize(contentUsing = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private List<Long> menuIds;

}
