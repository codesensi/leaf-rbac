package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import cn.codesensi.leaf.rbac.system.entity.SysUserRole;
import cn.codesensi.leaf.rbac.system.mapper.SysRoleMapper;
import cn.codesensi.leaf.rbac.system.mapper.SysUserRoleMapper;
import cn.codesensi.leaf.rbac.system.service.SysUserRoleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.leaf.rbac.system.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * 用户角色关联表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    private final SysRoleMapper sysRoleMapper;

    /**
     * 返回一个账号所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> listRoleByUserId(Long userId) {
        return QueryChain.of(sysRoleMapper)
                .select(SYS_ROLE.ALL_COLUMNS)
                .leftJoin(SYS_USER_ROLE).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .list();
    }

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
}
