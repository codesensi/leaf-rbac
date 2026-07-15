package cn.codesensi.leaf.rbac.system.strategy.captcha;

import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;

/**
 * 验证码策略接口类
 */
public interface CaptchaStrategy {

    /**
     * 生成验证码
     */
    CaptchaResultDTO captcha(CaptchaDTO captchaDTO);
}
