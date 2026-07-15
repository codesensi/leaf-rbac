package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.request.AssignMenusRequest;
import cn.codesensi.leaf.rbac.api.request.RoleSaveRequest;
import cn.codesensi.leaf.rbac.system.dto.AssignMenusDTO;
import cn.codesensi.leaf.rbac.system.dto.RoleSaveDTO;
import org.mapstruct.Mapper;

/**
 * 角色相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * RoleSaveRequest → RoleSaveDTO
     */
    RoleSaveDTO toSaveDTO(RoleSaveRequest request);

    /**
     * AssignMenusRequest → AssignMenusDTO
     */
    AssignMenusDTO toAssignMenusDTO(AssignMenusRequest request);

}
