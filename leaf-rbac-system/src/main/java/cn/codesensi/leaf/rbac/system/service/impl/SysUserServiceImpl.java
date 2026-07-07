package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.mapper.SysUserMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 获取当前用户的路由菜单树
     *
     * @return 路由菜单树
     */
    @Override
    public List<RouteDTO> getRoutes() {
        return sysMenuService.getRoutesByUserId(StpUtil.getLoginIdAsLong());
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public SysUser getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        return sysUserMapper.selectOneById(userId);
    }

}
