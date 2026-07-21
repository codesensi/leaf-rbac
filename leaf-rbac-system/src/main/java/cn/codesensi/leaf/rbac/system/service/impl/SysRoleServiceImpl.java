package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.common.enums.SysFlagEnum;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.framework.cache.CacheEvictService;
import cn.codesensi.leaf.rbac.system.converter.SysRoleConverter;
import cn.codesensi.leaf.rbac.system.dto.AssignMenusDTO;
import cn.codesensi.leaf.rbac.system.dto.RoleSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import cn.codesensi.leaf.rbac.system.entity.SysRoleMenu;
import cn.codesensi.leaf.rbac.system.mapper.SysRoleMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysRoleMenuService;
import cn.codesensi.leaf.rbac.system.service.SysRoleService;
import cn.codesensi.leaf.rbac.system.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.leaf.rbac.system.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * 角色信息表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleConverter sysRoleConverter;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final CacheEvictService cacheEvictService;
    private final SysMenuService sysMenuService;

    /**
     * 返回一个账号所拥有的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Cacheable(value = CacheConst.USER_ROLE, key = "#userId")
    @Override
    public List<String> listRoleCodeByUserId(Long userId) {
        // 获取去重后的角色列表
        List<SysRole> sysRoles = listRoleByUserId(userId);
        // 获取角色编码列表
        List<String> roleCodeList = sysRoles.stream()
                .map(SysRole::getCode)
                .filter(StrUtil::isNotBlank)
                .toList();
        if (CollUtil.isEmpty(roleCodeList)) {
            return List.of();
        }
        // 超级管理员角色编码
        if (roleCodeList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            return List.of(RbacConst.ROLE_ADMIN_CODE);
        }
        return roleCodeList;
    }

    /**
     * 返回一个账号所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> listRoleByUserId(Long userId) {
        // Step 1: 查角色ID（天然无重复）
        List<Long> roleIds = sysUserRoleService.queryChain()
                .select(SYS_USER_ROLE.ROLE_ID)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .listAs(Long.class);

        // 显式判空：无关联角色时直接返回空集合，跳过 Step 2
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }

        // Step 2: 主键批量查询（走索引，无需去重）
        return QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ALL_COLUMNS)
                .where(SYS_ROLE.ID.in(roleIds))
                .list();
    }

    /**
     * 保存角色信息
     *
     * @param roleSaveDTO 角色信息
     */
    @Override
    public void saveRole(RoleSaveDTO roleSaveDTO) {
        String code = roleSaveDTO.getCode();
        // 校验角色编码是否存在
        long count = QueryChain.of(sysRoleMapper)
                .where(SYS_ROLE.CODE.eq(code))
                .count();
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole sysRole = sysRoleConverter.toEntity(roleSaveDTO);
        sysRoleMapper.insert(sysRole, true);
    }

    /**
     * 分配角色菜单权限
     *
     * @param assignMenusDTO 角色菜单权限信息
     */
    @Override
    public void assignMenus(AssignMenusDTO assignMenusDTO) {
        Long roleId = assignMenusDTO.getRoleId();
        // 1. 校验角色是否存在
        SysRole sysRole = QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.SYS_FLAG)
                .where(SYS_ROLE.ID.eq(roleId))
                .one();
        if (ObjUtil.isNull(sysRole)) {
            throw new BusinessException("角色不存在");
        }

        // 系统内置角色不允许修改权限
        if (SysFlagEnum.YES.getCode().equals(sysRole.getSysFlag())) {
            throw new BusinessException("系统内置角色不允许修改权限");
        }

        // 2. 删除旧关联
        sysRoleMenuService.remove(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        // 同步清除角色下属所有用户的相关缓存（权限、菜单、用户信息）
        List<Long> userIds = sysUserRoleService.queryChain()
                .select(SYS_USER_ROLE.USER_ID)
                .where(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .listAs(Long.class);
        for (Long userId : userIds) {
            cacheEvictService.clearUserPermCache(userId);
            cacheEvictService.clearUserMenuCache(userId);
            cacheEvictService.clearUserInfoCache(userId);
        }

        List<Long> menuIds = assignMenusDTO.getMenuIds();
        // 3. 补全所有父菜单
        Set<Long> allMenuIds = new HashSet<>();
        // 如果菜单列表为空，则仅删除旧关联
        if (CollUtil.isNotEmpty(menuIds)) {
            // 菜单ID去重
            menuIds = menuIds.stream().distinct().toList();
            // 获取所有菜单的祖先ID（包含自身）
            Set<Long> ancestors = sysMenuService.getMenuAncestorsByIds(menuIds);
            allMenuIds.addAll(ancestors);
        }
        if (CollUtil.isNotEmpty(allMenuIds)) {
            // 4. 插入新关联（如果菜单列表为空，则仅删除）
            List<SysRoleMenu> entities = allMenuIds.stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    }).toList();
            // 批量插入
            sysRoleMenuService.saveBatch(entities);
        }
    }

}
