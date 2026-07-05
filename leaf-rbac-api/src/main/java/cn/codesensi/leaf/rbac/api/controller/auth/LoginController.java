package cn.codesensi.leaf.rbac.api.controller.auth;

import cn.codesensi.leaf.rbac.api.request.LoginAccountRequest;
import cn.codesensi.leaf.rbac.api.request.LogoutRequest;
import cn.codesensi.leaf.rbac.api.request.TokenRefreshRequest;
import cn.codesensi.leaf.rbac.api.response.LoginResponse;
import cn.codesensi.leaf.rbac.api.response.TokenRefreshResponse;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.dto.*;
import cn.codesensi.leaf.rbac.system.service.LoginService;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
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
@RequestMapping()
@Tag(name = "登录接口", description = "登录接口")
public class LoginController {

    private final LoginService loginService;

    /**
     * 账号密码登录
     */
    @SaIgnore
    @Operation(summary = "账号密码登录")
    @PostMapping("/login/account")
    public LoginResponse loginAccount(@Validated @RequestBody LoginAccountRequest request) {
        LoginAccountInDTO loginAccountInDTO = BeanUtil.copyProperties(request, LoginAccountInDTO.class);
        LoginOutDTO loginOutDTO = loginService.loginAccount(loginAccountInDTO);
        return BeanUtil.copyProperties(loginOutDTO, LoginResponse.class);
    }

    /**
     * 刷新访问令牌
     */
    @SaIgnore
    @Operation(summary = "刷新访问令牌")
    @PostMapping("/token/refresh")
    public TokenRefreshResponse tokenRefresh(@Validated @RequestBody TokenRefreshRequest request) {
        TokenRefreshInDTO tokenRefreshInDTO = BeanUtil.copyProperties(request, TokenRefreshInDTO.class);
        TokenRefreshOutDTO tokenRefreshOutDTO = loginService.tokenRefresh(tokenRefreshInDTO);
        return BeanUtil.copyProperties(tokenRefreshOutDTO, TokenRefreshResponse.class);
    }

    /**
     * 退出登录
     */
    @SaIgnore
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        LogoutInDTO logoutInDTO = BeanUtil.copyProperties(request, LogoutInDTO.class);
        loginService.logout(logoutInDTO);
    }

}
