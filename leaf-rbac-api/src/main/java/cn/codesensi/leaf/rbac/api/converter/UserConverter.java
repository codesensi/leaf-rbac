package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.request.AssignRolesRequest;
import cn.codesensi.leaf.rbac.api.request.UserSaveRequest;
import cn.codesensi.leaf.rbac.api.response.MenuResponse;
import cn.codesensi.leaf.rbac.api.response.UserInfoResponse;
import cn.codesensi.leaf.rbac.system.dto.AssignRolesDTO;
import cn.codesensi.leaf.rbac.system.dto.MenuDTO;
import cn.codesensi.leaf.rbac.system.dto.UserInfoDTO;
import cn.codesensi.leaf.rbac.system.dto.UserSaveDTO;
import org.mapstruct.Mapper;

/**
 * 用户相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserSaveRequest → UserSaveDTO
     */
    UserSaveDTO toSaveDTO(UserSaveRequest request);

    /**
     * UserInfoDTO → UserInfoResponse
     */
    UserInfoResponse toInfoResponse(UserInfoDTO userInfoDTO);

    /**
     * MenuDTO → MenuResponse
     */
    MenuResponse mapMenuResponse(MenuDTO menuDTO);

    /**
     * AssignRolesRequest → AssignRolesDTO
     */
    AssignRolesDTO toAssignRolesDTO(AssignRolesRequest request);

}
