package cn.codesensi.leaf.rbac.system.service;


import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;

/**
 * 登录接口
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param loginDTO 登录用户信息
     * @return 登录成功后信息
     */
    LoginResultDTO login(LoginDTO loginDTO);

    /**
     * 退出登录
     */
    void logout();
}
