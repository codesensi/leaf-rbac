package cn.codesensi.leaf.rbac.api.response;

import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(description = "生成验证码响应结果")
public class CaptchaResponse extends CaptchaResultDTO {
}
