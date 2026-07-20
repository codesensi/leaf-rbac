package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.AssignRolesDTO;
import cn.codesensi.leaf.rbac.system.dto.UserInfoDTO;
import cn.codesensi.leaf.rbac.system.dto.UserSaveDTO;
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
    UserInfoDTO getCurrentUser(Long userId);

    /**
     * 保存用户信息
     *
     * @param userSaveDTO 用户信息
     */
    void saveUser(UserSaveDTO userSaveDTO);

    /**
     * 配置用户角色
     *
     * @param assignRolesDTO 分配角色信息
     */
    void assignRoles(AssignRolesDTO assignRolesDTO);
}
