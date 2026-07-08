package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.common.enums.YesNoEnum;
import cn.codesensi.leaf.rbac.system.dto.MetaDTO;
import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.entity.SysMenu;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import cn.codesensi.leaf.rbac.system.mapper.SysMenuMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysRoleMenuService;
import cn.codesensi.leaf.rbac.system.service.SysRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.leaf.rbac.system.entity.table.SysMenuTableDef.SYS_MENU;
import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleTableDef.SYS_ROLE;

/**
 * 路由菜单表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleService sysRoleService;
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 返回一个账号所拥有的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Override
    public List<String> listPermsCodeByUserId(Long userId) {
        // 获取去重后的角色列表
        List<SysRole> sysRoles = sysRoleService.listRoleByUserId(userId);
        // 获取角色编码列表
        List<String> roleCodeList = sysRoles.stream()
                .map(SysRole::getCode)
                .filter(StrUtil::isNotBlank)
                .toList();
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }

        // 超级管理员角色的权限码
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return List.of(RbacConst.PERM_ADMIN_CODE);
        }

        // 获取角色拥有的权限码列表
        // Step 1: 根据角色CODE查出有效的角色ID（走 SYS_ROLE.CODE 索引）
        List<Long> roleIds = sysRoleService.queryChain()
                .select(SYS_ROLE.ID)
                .where(SYS_ROLE.CODE.in(roleCodeList))
                .and(SYS_ROLE.STATUS.eq(0))
                .listAs(Long.class);
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }

        // Step 2: 通过关联表查出菜单ID，再查菜单详情（两次均走索引）
        List<Long> menuIds = sysRoleMenuService.queryChain()
                .select(SYS_ROLE_MENU.MENU_ID)
                .where(SYS_ROLE_MENU.ROLE_ID.in(roleIds))
                .listAs(Long.class);
        if (CollUtil.isEmpty(menuIds)) {
            return List.of();
        }

        // Step 3: 主键批量查询菜单（走主键索引，天然无重复）
        return new QueryChain<>(sysMenuMapper)
                .select(SYS_MENU.PERMS)
                .where(SYS_MENU.ID.in(menuIds))
                .and(SYS_MENU.STATUS.eq(0))
                .orderBy(SYS_MENU.SORT, true) // 排序
                .listAs(String.class);
    }

    /**
     * 查询用户路由菜单树
     *
     * @param userId 用户id
     * @return 路由菜单树
     */
    @Override
    public List<RouteDTO> getRoutesByUserId(Long userId) {
        // 获取用户的角色编码列表
        List<String> roleCodeList = sysRoleService.listRoleCodeByUserId(userId);
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }

        // 超级管理员角色可查看所有菜单（包含已禁用的目录和菜单、不包含按钮级别）
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            List<SysMenu> allMenu = new QueryChain<>(sysMenuMapper)
                    .select(SYS_MENU.ALL_COLUMNS)
                    .where(SYS_MENU.TYPE.ne(3)) // 排除按钮类型
                    .orderBy(SYS_MENU.SORT, true) // 排序
                    .list();
            return buildRoutesTree(allMenu);
        }

        // 获取角色拥有的路由菜单列表（不包含按钮级别）
        // Step 1: 根据角色CODE查出有效角色ID（命中 SYS_ROLE(CODE, STATUS) 索引）
        List<Long> roleIds = sysRoleService.queryChain()
                .select(SYS_ROLE.ID)
                .where(SYS_ROLE.CODE.in(roleCodeList))
                .and(SYS_ROLE.STATUS.eq(0))
                .listAs(Long.class);
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }

        // Step 2: 通过关联表查出菜单ID（命中 SYS_ROLE_MENU(ROLE_ID) 索引）
        List<Long> menuIds = sysRoleMenuService.queryChain()
                .select(SYS_ROLE_MENU.MENU_ID)
                .where(SYS_ROLE_MENU.ROLE_ID.in(roleIds))
                .listAs(Long.class);
        if (CollUtil.isEmpty(menuIds)) {
            return List.of();
        }

        // Step 3: 主键批量查询路由菜单（走主键索引，无重复，带排序）
        List<SysMenu> menus = new QueryChain<>(sysMenuMapper)
                .select(SYS_MENU.ALL_COLUMNS)
                .where(SYS_MENU.ID.in(menuIds))
                .and(SYS_MENU.STATUS.eq(0))
                .and(SYS_MENU.TYPE.ne(3))      // 排除按钮类型
                .orderBy(SYS_MENU.SORT, true)  // 排序
                .list();
        return buildRoutesTree(menus);
    }

    /**
     * 构建路由菜单树
     *
     * @param menuList 菜单列表
     * @return RouteDTO 树结构
     */
    private List<RouteDTO> buildRoutesTree(List<SysMenu> menuList) {
        // 将菜单列表按父节点 ID 分组
        Map<Long, List<SysMenu>> menuGroupByPid = menuList.stream()
                .collect(Collectors.groupingBy(SysMenu::getPid));

        // 获取根节点（pid = 0）
        List<SysMenu> rootMenus = menuGroupByPid.getOrDefault(AppConst.ZERO_LONG, List.of());

        // 构建树结构,按 sort 排序
        return rootMenus.stream()
                .map(menu -> buildRouteDTO(menu, menuGroupByPid))
                .sorted(Comparator.comparingInt(route -> route.getMeta().getSort())) // 按 sort 排序
                .collect(Collectors.toList());
    }

    /**
     * 递归构建 RouteDTO 树结构
     *
     * @param menu           当前菜单
     * @param menuGroupByPid 按父节点 ID 分组的菜单列表
     * @return RouteDTO
     */
    private RouteDTO buildRouteDTO(SysMenu menu, Map<Long, List<SysMenu>> menuGroupByPid) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setPath(menu.getPath());
        routeDTO.setName(menu.getName());
        routeDTO.setComponent(menu.getComponent());

        // 设置 MetaDTO
        MetaDTO metaDTO = new MetaDTO();
        metaDTO.setTitle(menu.getTitle());
        metaDTO.setIcon(menu.getIcon());
        metaDTO.setShowLink(YesNoEnum.YES.getCode().equals(menu.getIsShow()));
        metaDTO.setSort(menu.getSort());
        metaDTO.setShowParent(YesNoEnum.YES.getCode().equals(menu.getIsShowParent()));
        metaDTO.setFrameSrc(menu.getFrameSrc());
        routeDTO.setMeta(metaDTO);

        // 递归构建子节点
        List<SysMenu> childrenMenus = menuGroupByPid.getOrDefault(menu.getId(), List.of());
        if (!childrenMenus.isEmpty()) {
            List<RouteDTO> children = childrenMenus.stream()
                    .map(childMenu -> buildRouteDTO(childMenu, menuGroupByPid))
                    .sorted(Comparator.comparingInt(route -> route.getMeta().getSort())) // 按 sort 排序
                    .collect(Collectors.toList());
            routeDTO.setChildren(children);
        }
        return routeDTO;
    }

}
