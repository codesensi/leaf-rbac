package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;
import cn.codesensi.leaf.rbac.system.service.CaptchaService;
import cn.codesensi.leaf.rbac.system.strategy.captcha.CaptchaStrategy;
import cn.codesensi.leaf.rbac.system.strategy.captcha.CaptchaStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 验证码接口实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaStrategyFactory captchaStrategyFactory;


    /**
     * 登录
     *
     * @param captchaDTO 验证码生成入参
     * @return 验证码
     */
    @Override
    public CaptchaResultDTO captcha(CaptchaDTO captchaDTO) {
        // 获取对应策略并执行
        CaptchaStrategy strategy = captchaStrategyFactory.getStrategy(captchaDTO.getType());
        return strategy.captcha(captchaDTO);
    }
}
