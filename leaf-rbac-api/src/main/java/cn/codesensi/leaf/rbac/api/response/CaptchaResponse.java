package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 生成验证码响应结果
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Schema(description = "生成验证码响应结果")
public class CaptchaResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码唯一标识
     */
    @Schema(description = "验证码唯一标识", example = "7c6c8b6e8b6e4b6e8b6e7c6c8b6e8b6e")
    private String captchaKey;

    /**
     * 验证码内容
     */
    @Schema(description = "验证码内容", example = "123456")
    private String captchaValue;
}
