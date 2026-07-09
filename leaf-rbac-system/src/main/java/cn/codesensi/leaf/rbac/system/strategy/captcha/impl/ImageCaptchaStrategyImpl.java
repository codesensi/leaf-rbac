package cn.codesensi.leaf.rbac.system.strategy.captcha.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import cn.codesensi.leaf.rbac.system.strategy.captcha.CaptchaStrategy;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 图形验证码策略实现类
 */
@RequiredArgsConstructor
@Slf4j
@Service("imageCaptchaStrategy")
public class ImageCaptchaStrategyImpl implements CaptchaStrategy {

    private final AppCaptchaProperties appCaptchaProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成图形验证码
     */
    @Override
    public CaptchaOutDTO captcha(CaptchaInDTO captchaInDTO) {
        AppCaptchaProperties.ImageType imageType = appCaptchaProperties.getImageType();
        String name = imageType.name();
        // 构建类名
        name = name.toLowerCase();
        name = StrUtil.upperFirst(name);
        String className = name + "Captcha";
        CaptchaOutDTO captchaOutDTO = new CaptchaOutDTO();
        try {
            Class<?> clazz = Class.forName("com.wf.captcha." + className);
            Captcha captcha = (Captcha) clazz.getDeclaredConstructor().newInstance();
            // 算术验证码
            if (captcha instanceof ArithmeticCaptcha) {
                String arithmeticString = ((ArithmeticCaptcha) captcha).getArithmeticString();
                log.info("算术验证码运算公式：{}", arithmeticString);
            }
            String keyUuid = IdUtil.fastSimpleUUID();
            // 验证码结果
            String text = captcha.text();
            log.info("图形验证码唯一标识：{}，验证码内容：{}", keyUuid, text);
            // 放入缓存
            stringRedisTemplate.opsForValue().set(CacheUtil.getCaptchaImagePrefix().concat(keyUuid), text, appCaptchaProperties.getImageExpire(), TimeUnit.SECONDS);
            // 返回结果
            captchaOutDTO.setCaptchaKey(keyUuid);
            captchaOutDTO.setCaptchaValue(captcha.toBase64());
        } catch (Exception e) {
            log.error("图形验证码生成失败：", e);
            throw new BusinessException("图形验证码生成失败");
        }
        return captchaOutDTO;
    }

}
