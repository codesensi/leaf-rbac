package cn.codesensi.leaf.rbac.system.strategy.login;

import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录策略工厂
 */
@Slf4j
@Component
public class LoginStrategyFactory {

    private final Map<String, LoginStrategy> loginStrategyMap = new ConcurrentHashMap<>();

    @Autowired
    public LoginStrategyFactory(List<LoginStrategy> strategies) {
        for (LoginStrategy strategy : strategies) {
            loginStrategyMap.put(strategy.getLoginType(), strategy);
        }
    }

    /**
     * 获取对应登录策略类
     *
     * @param loginType 登录策略枚举
     */
    public LoginStrategy getStrategy(String loginType) {
        LoginStrategy strategy = loginStrategyMap.get(loginType);
        if (strategy == null) {
            throw new BusinessException("不支持的登录方式: " + loginType);
        }
        return strategy;
    }
}
