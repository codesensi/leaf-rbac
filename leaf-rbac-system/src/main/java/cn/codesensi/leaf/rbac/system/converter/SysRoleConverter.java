package cn.codesensi.leaf.rbac.system.converter;

import cn.codesensi.leaf.rbac.system.dto.RoleSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import org.mapstruct.Mapper;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter {

    /**
     * RoleSaveDTO → SysRole
     */
    SysRole toEntity(RoleSaveDTO roleSaveDTO);

}
