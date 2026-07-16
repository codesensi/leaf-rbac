package cn.codesensi.leaf.rbac.api.controller.auth;

import cn.codesensi.leaf.rbac.api.converter.LoginConverter;
import cn.codesensi.leaf.rbac.api.request.LoginRequest;
import cn.codesensi.leaf.rbac.api.response.LoginResponse;
import cn.codesensi.leaf.rbac.common.enums.LoginEventType;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.framework.annotation.LogLogin;
import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;
import cn.codesensi.leaf.rbac.system.service.LoginService;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 前端控制器
 *
 * @author codesensi
 * @since 2024-07-21 11:09:56
 */
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "登录接口", description = "登录接口")
public class LoginController {

    private final LoginService loginService;
    private final LoginConverter loginConverter;

    /**
     * 登录
     */
    @LogLogin(type = LoginEventType.LOGIN)
    @SaIgnore
    @Operation(summary = "登录")
    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        LoginDTO loginDTO = loginConverter.toDTO(request);
        LoginResultDTO loginResultDTO = loginService.login(loginDTO);
        return loginConverter.toResponse(loginResultDTO);
    }

    /**
     * 退出登录
     */
    @LogLogin(type = LoginEventType.LOGOUT)
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout() {
        loginService.logout();
    }

}
