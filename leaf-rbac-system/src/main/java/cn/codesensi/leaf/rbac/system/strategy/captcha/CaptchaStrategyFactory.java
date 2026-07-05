package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.common.exception.CaptchaException;
import cn.codesensi.leaf.rbac.system.enums.CaptchaStrategyType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码策略工厂
 */
@Slf4j
@Component
public class CaptchaStrategyFactory {

    /**
     * 通过Spring容器的方式注入
     */
    @Resource
    private Map<String, CaptchaStrategy> captchaStrategyMap = new ConcurrentHashMap<>();

    /**
     * 获取对应验证码策略类
     *
     * @param captchaStrategyType 验证码策略枚举
     */
    public CaptchaStrategy getCaptchaStrategy(CaptchaStrategyType captchaStrategyType) {
        if (!captchaStrategyMap.containsKey(captchaStrategyType.getClassName())) {
            log.error("没有对应的验证码策略：{}", captchaStrategyType.getCode());
            throw new CaptchaException("验证码生成失败");
        }
        return captchaStrategyMap.get(captchaStrategyType.getClassName());
    }
}
