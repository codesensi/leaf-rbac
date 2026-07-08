package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.dto.SysUserSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户信息表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取当前用户的菜单列表
     *
     * @return 路由菜单树
     */
    List<RouteDTO> getRoutes();

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    SysUser getUserInfo();

    /**
     * 保存用户信息
     *
     * @param sysUserSaveDTO 用户信息
     * @return 保存结果
     */
    boolean saveUser(SysUserSaveDTO sysUserSaveDTO);
}
