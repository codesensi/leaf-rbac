package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.system.dto.CaptchaInDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaOutDTO;

/**
 * 验证码策略接口类
 */
public interface CaptchaStrategy {

    /**
     * 生成验证码
     */
    CaptchaOutDTO captcha(CaptchaInDTO captchaInDTO);
}
