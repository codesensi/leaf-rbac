package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "获取验证码请求参数", description = "获取验证码请求参数对象")
public class CaptchaRequest extends CaptchaInDTO {
}
