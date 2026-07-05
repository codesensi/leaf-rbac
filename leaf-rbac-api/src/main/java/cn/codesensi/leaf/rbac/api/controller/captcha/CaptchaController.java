package cn.codesensi.leaf.rbac.api.controller.captcha;

import cn.codesensi.leaf.rbac.api.request.CaptchaRequest;
import cn.codesensi.leaf.rbac.api.response.CaptchaResponse;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import cn.codesensi.leaf.rbac.system.strategy.captcha.CaptchaStrategyContext;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码 前端控制器
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@Tag(name = "验证码接口", description = "验证码生成接口")
@RequestMapping()
public class CaptchaController {

    private final CaptchaStrategyContext captchaStrategyContext;

    /**
     * 生成验证码
     */
    @SaIgnore
    @Operation(summary = "生成验证码", description = "生成验证码")
    @GetMapping("/captcha")
    public CaptchaResponse captcha(@Validated @ParameterObject CaptchaRequest request) {
        CaptchaInDTO captchaInDTO = BeanUtil.copyProperties(request, CaptchaInDTO.class);
        CaptchaOutDTO captchaOutDTO = captchaStrategyContext.captcha(captchaInDTO);
        return BeanUtil.copyProperties(captchaOutDTO, CaptchaResponse.class);
    }
}
