package cn.codesensi.leaf.rbac.api.response;

import cn.codesensi.leaf.rbac.system.dto.LoginOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 登录响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "登录响应结果")
public class LoginResponse extends LoginOutDTO {

}
