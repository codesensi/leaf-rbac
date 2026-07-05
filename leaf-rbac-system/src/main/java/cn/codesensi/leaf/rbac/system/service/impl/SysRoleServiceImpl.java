package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import cn.codesensi.leaf.rbac.system.mapper.SysRoleMapper;
import cn.codesensi.leaf.rbac.system.service.SysRoleService;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleTableDef.SYS_ROLE;
import static cn.codesensi.leaf.rbac.system.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

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

    /**
     * 返回一个账号所拥有的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Override
    public List<String> listRoleCodeByUserId(Long userId) {
        // 获取去重后的角色列表
        List<SysRole> sysRoles = listRoleByUserId(userId);
        // 获取角色编码列表
        List<String> roleCodeList = sysRoles.stream()
                .map(SysRole::getCode)
                .filter(StrUtil::isNotBlank)
                .toList();
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
        return new QueryChain<>(sysRoleMapper)
                .select(SYS_ROLE.CODE)
                .from(SYS_ROLE)
                .leftJoin(SYS_USER_ROLE).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .leftJoin(SYS_USER).on(SYS_USER.ID.eq(SYS_USER_ROLE.USER_ID))
                .where(SYS_USER.ID.eq(userId))
                .groupBy(SYS_ROLE.CODE)
                .list();
    }

}
