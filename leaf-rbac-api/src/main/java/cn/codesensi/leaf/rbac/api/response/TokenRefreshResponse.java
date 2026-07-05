package cn.codesensi.leaf.rbac.api.response;

import cn.codesensi.leaf.rbac.system.dto.TokenRefreshOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 刷新访问令牌结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "刷新访问令牌结果", description = "刷新访问令牌结果")
public class TokenRefreshResponse extends TokenRefreshOutDTO {

}
