package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.LogoutInDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 退出登录参数
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "退出登录参数", description = "退出登录参数")
public class LogoutRequest extends LogoutInDTO {

}
