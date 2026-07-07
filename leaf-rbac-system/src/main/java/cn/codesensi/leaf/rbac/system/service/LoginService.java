package cn.codesensi.leaf.rbac.system.service;


import cn.codesensi.leaf.rbac.system.dto.*;

/**
 * 登录接口
 */
public interface LoginService {

    /**
     * 账号密码登录
     *
     * @param loginAccountInDTO 登录用户信息
     * @return 登录成功后信息
     */
    LoginOutDTO loginAccount(LoginAccountInDTO loginAccountInDTO);

    /**
     * 退出登录
     */
    void logout();
}
