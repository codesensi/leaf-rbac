package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.common.enums.CaptchaType;
import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信验证码策略实现类
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SmsCaptchaStrategy implements CaptchaStrategy {

    private final AppCaptchaProperties appCaptchaProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 支持的验证码生成方式
     */
    @Override
    public String getCaptchaType() {
        return CaptchaType.SMS.getCode();
    }

    /**
     * 生成短信验证码
     */
    @Override
    public CaptchaResultDTO captcha(CaptchaDTO captchaDTO) {
        String phone = captchaDTO.getPhone();
        if (StrUtil.isBlank(phone)) {
            throw new ValidationException("手机号不能为空");
        }
        // 生成验证码
        String result = RandomUtil.randomNumbers(appCaptchaProperties.getSmsLength());
        log.debug("短信验证码手机号：{}，验证码内容：{}", phone, result);

        // TODO 发短信

        // 放入缓存
        stringRedisTemplate.opsForValue().set(
                CacheUtil.getCaptchaSmsPrefix().concat(phone),
                result,
                appCaptchaProperties.getSmsExpire(),
                TimeUnit.SECONDS);
        // 返回结果
        CaptchaResultDTO captchaResultDTO = new CaptchaResultDTO();
        captchaResultDTO.setCaptchaKey(phone);
        captchaResultDTO.setCaptchaValue(result);
        return captchaResultDTO;
    }
}
