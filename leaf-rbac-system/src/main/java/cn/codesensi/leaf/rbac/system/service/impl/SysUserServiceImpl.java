package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.dto.SysUserSaveDTO;
import cn.codesensi.leaf.rbac.system.dto.UserInfoDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.mapper.SysUserMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final AppProperties appProperties;

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Cacheable(value = CacheConst.USER_INFO, key = "#userId")
    @Override
    public UserInfoDTO getInfo(Long userId) {
        SysUser sysUser = QueryChain.of(sysUserMapper)
                .select(SYS_USER.ALL_COLUMNS)
                .where(SYS_USER.ID.eq(userId))
                .one();
        if (ObjUtil.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }
        UserInfoDTO userInfoDTO = BeanUtil.copyProperties(sysUser, UserInfoDTO.class);
        // 角色集合
        List<String> roles = StpUtil.getRoleList();
        userInfoDTO.setRoles(roles);
        // 权限码集合
        List<String> perms = StpUtil.getPermissionList();
        userInfoDTO.setPermissions(perms);
        // 拥有的菜单
        List<RouteDTO> routes = sysMenuService.getRoutesByUserId(userId);
        userInfoDTO.setRoutes(routes);
        return userInfoDTO;
    }

    /**
     * 保存用户信息
     *
     * @param sysUserSaveDTO 用户信息
     * @return 保存结果
     */
    @Override
    public boolean saveUser(SysUserSaveDTO sysUserSaveDTO) {
        String username = sysUserSaveDTO.getUsername();
        // 校验用户名是否存在
        long count = QueryChain.of(sysUserMapper)
                .where(SYS_USER.USERNAME.eq(username))
                .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser sysUser = BeanUtil.copyProperties(sysUserSaveDTO, SysUser.class);
        // 若未输入昵称则保持昵称和用户名相同
        if (StrUtil.isBlank(sysUserSaveDTO.getNickname())) {
            sysUser.setNickname(username);
        }

        // 默认随机头像
        if (StrUtil.isBlank(sysUserSaveDTO.getAvatar())) {
            String avatar = String.format(appProperties.getAvatar(), username);
            sysUser.setAvatar(avatar);
        }

        // 默认密码
        String password = BCrypt.hashpw(AppConst.DEFAULT_PASSWORD, BCrypt.gensalt());
        sysUser.setPassword(password);

        // 创建人
        sysUser.setCreator(StpUtil.getLoginIdAsLong());
        int insert = sysUserMapper.insert(sysUser, true);
        return insert > 0;
    }

}
