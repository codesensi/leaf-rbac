package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.entity.SysMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 路由菜单表 服务层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 返回一个账号所拥有的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> listPermsCodeByUserId(Long userId);

    /**
     * 查询用户路由菜单树
     *
     * @param userId 用户id
     * @return 路由菜单树
     */
    List<RouteDTO> getRoutesByUserId(Long userId);

}
