package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.AssignMenusDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 角色分配菜单请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "角色分配菜单请求参数")
public class AssignMenusRequest extends AssignMenusDTO {

}
