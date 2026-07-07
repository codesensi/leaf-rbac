package cn.codesensi.leaf.rbac.api.response;

import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 生成验证码响应结果
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "生成验证码响应结果")
public class CaptchaResponse extends CaptchaOutDTO {
}
