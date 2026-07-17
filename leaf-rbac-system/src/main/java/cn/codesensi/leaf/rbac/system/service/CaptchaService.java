package cn.codesensi.leaf.rbac.system.service;


import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;

/**
 * 验证码接口
 */
public interface CaptchaService {

    /**
     * 登录
     *
     * @param captchaDTO 验证码生成入参
     * @return 验证码
     */
    CaptchaResultDTO captcha(CaptchaDTO captchaDTO);

}
