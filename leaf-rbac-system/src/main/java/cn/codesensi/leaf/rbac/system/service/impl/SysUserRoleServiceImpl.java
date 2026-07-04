package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.system.entity.SysUserRole;
import cn.codesensi.leaf.rbac.system.mapper.SysUserRoleMapper;
import cn.codesensi.leaf.rbac.system.service.SysUserRoleService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

}
