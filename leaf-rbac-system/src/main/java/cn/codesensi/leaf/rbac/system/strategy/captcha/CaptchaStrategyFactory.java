package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码策略工厂
 */
@Slf4j
@Component
public class CaptchaStrategyFactory {

    private final Map<String, CaptchaStrategy> captchaStrategyMap = new ConcurrentHashMap<>();

    @Autowired
    public CaptchaStrategyFactory(List<CaptchaStrategy> strategies) {
        for (CaptchaStrategy strategy : strategies) {
            captchaStrategyMap.put(strategy.getCaptchaType(), strategy);
        }
    }

    /**
     * 获取对应验证码策略类
     *
     * @param captchaType 验证码策略枚举
     */
    public CaptchaStrategy getStrategy(String captchaType) {
        CaptchaStrategy strategy = captchaStrategyMap.get(captchaType);
        if (strategy == null) {
            throw new BusinessException("不支持的验证码生成方式: " + captchaType);
        }
        return strategy;
    }
}
