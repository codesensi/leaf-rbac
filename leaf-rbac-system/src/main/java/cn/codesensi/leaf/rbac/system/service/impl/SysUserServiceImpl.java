package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.enums.SysFlagEnum;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.framework.cache.CacheEvictService;
import cn.codesensi.leaf.rbac.system.converter.SysUserConverter;
import cn.codesensi.leaf.rbac.system.dto.AssignRolesDTO;
import cn.codesensi.leaf.rbac.system.dto.MenuDTO;
import cn.codesensi.leaf.rbac.system.dto.UserInfoDTO;
import cn.codesensi.leaf.rbac.system.dto.UserSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysMenu;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.entity.SysUserRole;
import cn.codesensi.leaf.rbac.system.mapper.SysUserMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysUserRoleService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 用户信息表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysMenuService sysMenuService;
    private final SysUserMapper sysUserMapper;
    private final SysUserConverter sysUserConverter;
    private final AppProperties appProperties;
    private final SysUserRoleService sysUserRoleService;
    private final CacheEvictService cacheEvictService;

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Cacheable(value = CacheConst.USER_INFO, key = "#userId")
    @Override
    public UserInfoDTO getCurrentUser(Long userId) {
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.ALL_COLUMNS)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        UserInfoDTO userInfoDTO = sysUserConverter.toUserInfoDTO(sysUser);
        // 角色集合
        List<String> roles = StpUtil.getRoleList();
        userInfoDTO.setRoles(roles);
        // 权限码集合
        List<String> perms = StpUtil.getPermissionList();
        userInfoDTO.setPermissions(perms);
        // 拥有的菜单
        List<SysMenu> menus = sysMenuService.getMenusByUserId(userId);
        List<MenuDTO> menuDTOS = sysUserConverter.toMenuDTOList(menus);
        userInfoDTO.setMenus(menuDTOS);
        return userInfoDTO;
    }

    /**
     * 保存用户信息
     *
     * @param userSaveDTO 用户信息
     */
    @Override
    public void saveUser(UserSaveDTO userSaveDTO) {
        String username = userSaveDTO.getUsername();
        // 校验用户名是否存在
        long count = QueryChain.of(sysUserMapper)
                .where(SYS_USER.USERNAME.eq(username))
                .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser sysUser = sysUserConverter.toEntity(userSaveDTO);
        // 若未输入昵称则保持昵称和用户名相同
        if (StrUtil.isBlank(userSaveDTO.getNickname())) {
            sysUser.setNickname(username);
        }

        // 默认随机头像
        if (StrUtil.isBlank(userSaveDTO.getAvatar())) {
            String avatar = String.format(appProperties.getAvatar(), username);
            sysUser.setAvatar(avatar);
        }

        // 默认密码
        String password = BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt());
        sysUser.setPassword(password);
        sysUserMapper.insert(sysUser, true);
    }

    /**
     * 配置用户角色
     *
     * @param assignRolesDTO 分配角色信息
     */
    @Override
    public void assignRoles(AssignRolesDTO assignRolesDTO) {
        Long userId = assignRolesDTO.getUserId();
        // 1. 校验用户是否存在
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.SYS_FLAG)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }

        // 2. 系统内置用户不允许修改角色
        if (SysFlagEnum.YES.getCode().equals(sysUser.getSysFlag())) {
            throw new BusinessException("系统内置用户不允许修改角色");
        }

        // 3. 删除旧关联
        sysUserRoleService.remove(SYS_USER_ROLE.USER_ID.eq(userId));
        // 同步清除用户的相关缓存（权限、菜单、用户信息）
        cacheEvictService.clearUserRoleCache(userId);
        cacheEvictService.clearUserPermCache(userId);
        cacheEvictService.clearUserInfoCache(userId);

        List<Long> roleIds = assignRolesDTO.getRoleIds();
        // 如果角色列表为空，则仅删除旧关联
        if (CollUtil.isNotEmpty(roleIds)) {
            // 角色ID去重
            roleIds = roleIds.stream().distinct().toList();
            // 4. 插入新关联（如果角色列表为空，则仅删除）
            List<SysUserRole> entities = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setRoleId(roleId);
                        return sysUserRole;
                    }).toList();
            // 批量插入
            sysUserRoleService.saveBatch(entities);
        }
    }

}
