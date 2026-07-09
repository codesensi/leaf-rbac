package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.SysUserSaveDTO;
import cn.codesensi.leaf.rbac.system.dto.UserInfoDTO;
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
    UserInfoDTO getInfo(Long userId);

    /**
     * 保存用户信息
     *
     * @param sysUserSaveDTO 用户信息
     * @return 保存结果
     */
    boolean saveUser(SysUserSaveDTO sysUserSaveDTO);
}
