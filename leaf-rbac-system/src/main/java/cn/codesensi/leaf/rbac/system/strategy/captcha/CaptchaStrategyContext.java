package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;
import cn.codesensi.leaf.rbac.system.enums.CaptchaStrategyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 验证码策略上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaStrategyContext {

    /**
     * 策略类型缓存，避免每次请求遍历枚举
     */
    private static final Map<String, CaptchaStrategyType> STRATEGY_TYPE_MAP = Arrays.stream(CaptchaStrategyType.values())
            .collect(Collectors.toUnmodifiableMap(CaptchaStrategyType::getCode, type -> type));

    private final CaptchaStrategyFactory captchaStrategyFactory;

    /**
     * 生成验证码
     */
    public CaptchaOutDTO captcha(CaptchaInDTO captchaInDTO) {
        // 将属性的值转换成具体的枚举
        String type = captchaInDTO.getType();
        CaptchaStrategyType captchaStrategyType = STRATEGY_TYPE_MAP.get(type);
        if (captchaStrategyType == null) {
            log.error("未匹配到验证码策略：{}", type);
            throw new BusinessException("验证码生成失败");
        }
        // 获取策略实现
        CaptchaStrategy captchaStrategy = captchaStrategyFactory.getCaptchaStrategy(captchaStrategyType);
        return captchaStrategy.captcha(captchaInDTO);
    }
}
