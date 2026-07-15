package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.UserSaveDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 保存用户请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "保存用户请求参数")
public class UserSaveRequest extends UserSaveDTO implements Serializable {

}
