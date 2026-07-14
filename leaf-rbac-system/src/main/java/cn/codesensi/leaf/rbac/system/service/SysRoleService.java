package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.RoleSaveInDTO;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 角色信息表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 返回一个账号所拥有的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> listRoleCodeByUserId(Long userId);

    /**
     * 返回一个账号所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> listRoleByUserId(Long userId);

    /**
     * 保存角色信息
     *
     * @param roleSaveInDTO 角色信息
     */
    void saveRole(RoleSaveInDTO roleSaveInDTO);
}
