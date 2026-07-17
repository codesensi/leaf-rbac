package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.common.enums.CaptchaType;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties;
import cn.codesensi.leaf.rbac.common.properties.AppCaptchaProperties.ImageType;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;
import cn.hutool.core.util.IdUtil;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 图形验证码策略实现类。
 * <p>
 * 通过 {@link EnumMap} 将 {@link ImageType} 映射到 {@link Captcha} 工厂，
 * 消除运行时字符串拼接 + 反射的类加载方式，改为编译期确定。
 * </p>
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ImageCaptchaStrategy implements CaptchaStrategy {

    /**
     * 图形验证码类型 → 验证码实例工厂的映射表。
     * <p>
     * {@link EnumMap} 保证每个 {@link ImageType} 都有对应条目，
     * 新增类型时编译期即可发现缺失。
     * </p>
     */
    private static final Map<ImageType, Supplier<Captcha>> CAPTCHA_FACTORY = new EnumMap<>(ImageType.class);

    static {
        CAPTCHA_FACTORY.put(ImageType.SPEC, SpecCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.GIF, GifCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.CHINESE, ChineseCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.CHINESE_GIF, ChineseGifCaptcha::new);
        CAPTCHA_FACTORY.put(ImageType.ARITHMETIC, ArithmeticCaptcha::new);
    }

    private final AppCaptchaProperties appCaptchaProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 支持的验证码生成方式
     */
    @Override
    public String getCaptchaType() {
        return CaptchaType.IMAGE.getCode();
    }

    /**
     * 生成图形验证码
     */
    @Override
    public CaptchaResultDTO captcha(CaptchaDTO captchaDTO) {
        ImageType imageType = appCaptchaProperties.getImageType();
        // 编译期确定的工厂创建，无需反射
        Captcha captcha = CAPTCHA_FACTORY.get(imageType).get();

        // 算术验证码额外记录运算公式便于调试
        if (captcha instanceof ArithmeticCaptcha arithmeticCaptcha) {
            log.debug("算术验证码运算公式：{}", arithmeticCaptcha.getArithmeticString());
        }

        String keyUuid = IdUtil.fastSimpleUUID();
        String text = captcha.text();
        log.debug("图形验证码唯一标识：{}，验证码内容：{}", keyUuid, text);

        // 放入缓存
        stringRedisTemplate.opsForValue().set(
                CacheUtil.getCaptchaImagePrefix().concat(keyUuid),
                text,
                appCaptchaProperties.getImageExpire(),
                TimeUnit.SECONDS);

        // 返回结果
        CaptchaResultDTO captchaResultDTO = new CaptchaResultDTO();
        captchaResultDTO.setCaptchaKey(keyUuid);
        captchaResultDTO.setCaptchaValue(captcha.toBase64());
        return captchaResultDTO;
    }

}
