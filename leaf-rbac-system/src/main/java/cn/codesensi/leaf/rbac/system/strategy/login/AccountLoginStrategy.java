package cn.codesensi.leaf.rbac.system.strategy.login;

import cn.codesensi.leaf.rbac.common.enums.LoginType;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 账号密码登录策略实现类
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AccountLoginStrategy implements LoginStrategy {

    private final AppCaptchaProperties appCaptchaProperties;
    private final SysUserService sysUserService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 支持的登录方式
     */
    @Override
    public String getLoginType() {
        return LoginType.ACCOUNT.getCode();
    }

    /**
     * 登录
     * 支持用户名、手机号、邮箱
     *
     * @param loginDTO 登录参数
     */
    @Override
    public SysUser login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 校验必填项
        if (StrUtil.isBlank(username)) {
            throw new ValidationException("账号不能为空");
        }
        if (StrUtil.isBlank(password)) {
            throw new ValidationException("密码不能为空");
        }

        // 校验验证码
        if (appCaptchaProperties.isEnabled()) {
            if (StrUtil.isBlank(loginDTO.getCaptchaKey())) {
                throw new ValidationException("验证码唯一标识为空");
            }
            String captchaValue = loginDTO.getCaptchaValue();
            if (StrUtil.isBlank(captchaValue)) {
                throw new ValidationException("验证码为空");
            }
            // 与缓存中的值对比
            String captchaCache = stringRedisTemplate.opsForValue().getAndDelete(CacheUtil.getCaptchaImagePrefix().concat(loginDTO.getCaptchaKey()));
            if (StrUtil.isBlank(captchaCache)) {
                throw new BusinessException("验证码不存在");
            }
            if (!captchaValue.equals(captchaCache)) {
                throw new BusinessException("验证码错误");
            }
        }

        // 校验用户及密码
        SysUser sysUser = sysUserService.queryChain()
                .select(SYS_USER.ID, SYS_USER.USERNAME, SYS_USER.PHONE, SYS_USER.EMAIL, SYS_USER.NICKNAME, SYS_USER.PASSWORD)
                .where(SYS_USER.USERNAME.eq(username))
                .or(SYS_USER.PHONE.eq(username))
                .or(SYS_USER.EMAIL.eq(username))
                .one();
        if (ObjUtil.isNull(sysUser) || !BCrypt.checkpw(password, sysUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        return sysUser;
    }

}
