package cn.codesensi.leaf.rbac.system.strategy.captcha.impl;

import cn.codesensi.leaf.rbac.common.exception.ValidationException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import cn.codesensi.leaf.rbac.system.strategy.captcha.CaptchaStrategy;
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
@Service("smsCaptchaStrategy")
public class SmsCaptchaStrategyImpl implements CaptchaStrategy {

    private final AppCaptchaProperties appCaptchaProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成短信验证码
     */
    @Override
    public CaptchaOutDTO captcha(CaptchaInDTO captchaInDTO) {
        String phone = captchaInDTO.getPhone();
        if (StrUtil.isBlank(phone)) {
            throw new ValidationException("手机号不能为空");
        }
        // 生成验证码
        String result = RandomUtil.randomNumbers(6);
        log.info("短信验证码手机号：{}，验证码内容：{}", phone, result);

        // TODO 发短信

        // 放入缓存
        stringRedisTemplate.opsForValue().set(CacheUtil.getCaptchaSmsPrefix().concat(phone), result, appCaptchaProperties.getSmsExpire(), TimeUnit.SECONDS);
        // 返回结果
        CaptchaOutDTO captchaOutDTO = new CaptchaOutDTO();
        captchaOutDTO.setCaptchaKey(phone);
        captchaOutDTO.setCaptchaValue(result);
        return captchaOutDTO;
    }
}
