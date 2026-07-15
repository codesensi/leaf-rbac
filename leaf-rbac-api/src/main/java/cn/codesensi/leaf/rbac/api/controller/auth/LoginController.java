package cn.codesensi.leaf.rbac.api.controller.auth;

import cn.codesensi.leaf.rbac.api.mapper.LoginMapper;
import cn.codesensi.leaf.rbac.api.request.LoginAccountRequest;
import cn.codesensi.leaf.rbac.api.response.LoginResponse;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.dto.LoginAccountDTO;
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
    private final LoginMapper loginMapper;

    /**
     * 账号密码登录
     */
    @SaIgnore
    @Operation(summary = "账号密码登录")
    @PostMapping("/login/account")
    public LoginResponse loginAccount(@Validated @RequestBody LoginAccountRequest request) {
        LoginAccountDTO inDTO = loginMapper.toInDTO(request);
        LoginResultDTO loginResultDTO = loginService.loginAccount(inDTO);
        return loginMapper.toResponse(loginResultDTO);
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout() {
        loginService.logout();
    }

}
