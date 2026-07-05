package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.Const;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.properties.AppSecurityProperties;
import cn.codesensi.leaf.rbac.system.dto.*;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.LoginService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 登录接口实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final AppCaptchaProperties appCaptchaProperties;
    private final AppSecurityProperties appSecurityProperties;
    private final SysUserService sysUserService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 账号密码登录
     *
     * @param loginAccountInDTO 登录用户信息
     * @return 登录成功后信息
     */
    @Override
    public LoginOutDTO loginAccount(LoginAccountInDTO loginAccountInDTO) {
        // 校验验证码
        if (appCaptchaProperties.getEnabled()) {
            if (StrUtil.isBlank(loginAccountInDTO.getCaptchaKey())) {
                throw new ValidationException("验证码唯一标识为空");
            }
            String captcha = loginAccountInDTO.getCaptcha();
            if (StrUtil.isBlank(captcha)) {
                throw new ValidationException("验证码为空");
            }
            // 与缓存中的值对比
            String captchaCache = stringRedisTemplate.opsForValue().get(loginAccountInDTO.getCaptchaKey());
            if (StrUtil.isBlank(captchaCache)) {
                throw new BusinessException("验证码不存在");
            }
            if (!captcha.equals(captchaCache)) {
                throw new BusinessException("验证码错误");
            }
        }
        SysUser sysUser = sysUserService.queryChain()
                .where(SYS_USER.USERNAME.eq(loginAccountInDTO.getUsername()))
                .one();
        if (ObjUtil.isNull(sysUser) || !BCrypt.checkpw(loginAccountInDTO.getPassword(), sysUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        Long userId = sysUser.getId();
        // 校验账户是否封禁
        StpUtil.checkDisable(userId);
        // 登录
        StpUtil.login(userId);

        LoginOutDTO loginOutDTO = new LoginOutDTO();
        loginOutDTO.setAccessToken(StpUtil.getTokenValue());
        // 获取刷新令牌
        Long refreshTokenTimeout = appSecurityProperties.getRefreshTokenTimeout();
        String refreshToken = SaTempUtil.createToken(userId, refreshTokenTimeout);
        loginOutDTO.setRefreshToken(refreshToken);
        // 访问令牌过期时间
        long accessTokenTimeout = StpUtil.getTokenTimeout();
        loginOutDTO.setExpires(LocalDateTimeUtil.now().plusSeconds(accessTokenTimeout).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        // 其他信息
        loginOutDTO.setUsername(sysUser.getUsername());
        loginOutDTO.setNickname(sysUser.getNickname());
        loginOutDTO.setAvatar(sysUser.getAvatar());
        // 角色集合
        List<String> roles = StpUtil.getRoleList();
        loginOutDTO.setRoles(roles);
        // 权限码集合
        List<String> perms = StpUtil.getPermissionList();
        loginOutDTO.setPermissions(perms);
        return loginOutDTO;
    }

    /**
     * 刷新访问令牌
     *
     * @return 刷新访问令牌结果
     */
    @Override
    public TokenRefreshOutDTO tokenRefresh(TokenRefreshInDTO tokenRefreshInDTO) {
        String refreshToken = tokenRefreshInDTO.getRefreshToken();
        // 访问令牌过期时间
        long accessTokenTimeout = StpUtil.getTokenTimeout();
        // 访问令牌过期后才可以获取新值
        Object userId = SaTempUtil.parseToken(refreshToken);
        if (accessTokenTimeout <= Const.ZERO_INT) {
            if (ObjUtil.isNull(userId)) {
                throw new BusinessException("登录已失效");
            }
            // 登出
            StpUtil.logout();
            // 重新登录，生成新访问令牌
            StpUtil.login(userId);
            // 访问令牌过期时间重新赋值
            accessTokenTimeout = StpUtil.getTokenTimeout();
        }

        TokenRefreshOutDTO tokenRefreshOutDTO = new TokenRefreshOutDTO();
        tokenRefreshOutDTO.setAccessToken(StpUtil.getTokenValue());
        tokenRefreshOutDTO.setRefreshToken(refreshToken);
        tokenRefreshOutDTO.setExpires(LocalDateTimeUtil.now().plusSeconds(accessTokenTimeout).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        return tokenRefreshOutDTO;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(LogoutInDTO logoutInDTO) {
        String accessToken = logoutInDTO.getAccessToken();
        if (StrUtil.isNotBlank(accessToken)) {
            Object userId = StpUtil.getLoginIdByToken(accessToken);
            if (ObjUtil.isNotNull(userId)) {
                StpUtil.logout(userId);
            }
        }
        // 同步删除刷新令牌
        String refreshToken = logoutInDTO.getRefreshToken();
        if (StrUtil.isNotBlank(refreshToken)) {
            SaTempUtil.deleteToken(refreshToken);
        }
    }

}
