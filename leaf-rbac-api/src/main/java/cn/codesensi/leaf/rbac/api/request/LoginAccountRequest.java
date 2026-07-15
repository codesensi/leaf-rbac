package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.LoginAccountDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 账号密码登录请求参数
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "账号密码登录请求参数")
public class LoginAccountRequest extends LoginAccountDTO {

}
