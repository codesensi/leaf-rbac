package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.UserInfoOutDTO;
import cn.codesensi.leaf.rbac.system.dto.UserSaveInDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import com.mybatisflex.core.service.IService;

/**
 * 用户信息表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoOutDTO getInfo(Long userId);

    /**
     * 保存用户信息
     *
     * @param userSaveInDTO 用户信息
     */
    void saveUser(UserSaveInDTO userSaveInDTO);
}
