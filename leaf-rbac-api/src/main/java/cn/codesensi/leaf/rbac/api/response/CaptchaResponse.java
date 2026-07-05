package cn.codesensi.leaf.rbac.api.response;

import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "验证码生成结果", description = "验证码生成结果")
public class CaptchaResponse extends CaptchaOutDTO {
}
