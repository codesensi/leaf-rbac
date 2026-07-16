package cn.codesensi.leaf.rbac.system.strategy.login;

import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;

/**
 * 登录策略接口类
 */
public interface LoginStrategy {

    /**
     * 支持的登录方式
     */
    String getLoginType();

    /**
     * 登录
     */
    SysUser login(LoginDTO loginDTO);

}
