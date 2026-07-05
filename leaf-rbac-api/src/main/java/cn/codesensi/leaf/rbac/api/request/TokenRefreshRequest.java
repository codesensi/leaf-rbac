package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.TokenRefreshInDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 刷新访问令牌参数
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "刷新访问令牌参数", description = "刷新访问令牌参数")
public class TokenRefreshRequest extends TokenRefreshInDTO {

}
