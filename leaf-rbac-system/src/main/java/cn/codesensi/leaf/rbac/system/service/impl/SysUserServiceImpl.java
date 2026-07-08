package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import cn.codesensi.leaf.rbac.common.exception.BusinessException;
import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import cn.codesensi.leaf.rbac.system.dto.RouteDTO;
import cn.codesensi.leaf.rbac.system.dto.SysUserSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.mapper.SysUserMapper;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
     * 获取当前用户的路由菜单树
     *
     * @return 路由菜单树
     */
    @Override
    public List<RouteDTO> getRoutes() {
        long userId = StpUtil.getLoginIdAsLong();
        return sysMenuService.getRoutesByUserId(userId);
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
