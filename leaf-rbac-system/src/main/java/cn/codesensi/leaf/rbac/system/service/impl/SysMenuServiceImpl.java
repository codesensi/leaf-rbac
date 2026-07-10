package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Cacheable(value = CacheConst.USER_PERM, key = "#userId")
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
        return QueryChain.of(sysMenuMapper)
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
    @Cacheable(value = CacheConst.USER_MENU, key = "#userId")
    @Override
    public List<SysMenu> getMenusByUserId(Long userId) {
        // 获取用户的角色编码列表
        List<String> roleCodeList = sysRoleService.listRoleCodeByUserId(userId);
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }

        // 超级管理员角色可查看所有菜单（包含已禁用的目录和菜单、不包含按钮级别）
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return QueryChain.of(sysMenuMapper)
                    .select(SYS_MENU.ID, SYS_MENU.PID, SYS_MENU.NAME, SYS_MENU.PATH, SYS_MENU.PARAM, SYS_MENU.COMPONENT, SYS_MENU.TITLE, SYS_MENU.TYPE, SYS_MENU.SORT, SYS_MENU.ICON, SYS_MENU.PERMS, SYS_MENU.IS_LINK, SYS_MENU.IS_FRAME, SYS_MENU.FRAME_SRC, SYS_MENU.IS_SHOW, SYS_MENU.IS_SHOW_PARENT, SYS_MENU.STATUS, SYS_MENU.REMARK, SYS_MENU.SYS_FLAG)
                    .where(SYS_MENU.TYPE.ne(3)) // 排除按钮类型
                    .orderBy(SYS_MENU.SORT, true) // 排序
                    .list();
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
        return QueryChain.of(sysMenuMapper)
                .select(SYS_MENU.ID, SYS_MENU.PID, SYS_MENU.NAME, SYS_MENU.PATH, SYS_MENU.PARAM, SYS_MENU.COMPONENT, SYS_MENU.TITLE, SYS_MENU.TYPE, SYS_MENU.SORT, SYS_MENU.ICON, SYS_MENU.PERMS, SYS_MENU.IS_LINK, SYS_MENU.IS_FRAME, SYS_MENU.FRAME_SRC, SYS_MENU.IS_SHOW, SYS_MENU.IS_SHOW_PARENT, SYS_MENU.STATUS, SYS_MENU.REMARK, SYS_MENU.SYS_FLAG)
                .where(SYS_MENU.ID.in(menuIds))
                .and(SYS_MENU.STATUS.eq(0))
                .and(SYS_MENU.TYPE.ne(3))      // 排除按钮类型
                .orderBy(SYS_MENU.SORT, true)  // 排序
                .list();
    }

}
