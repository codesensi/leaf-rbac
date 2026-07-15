package cn.codesensi.leaf.rbac.api.mapper;

import cn.codesensi.leaf.rbac.api.request.LoginAccountRequest;
import cn.codesensi.leaf.rbac.api.response.LoginResponse;
import cn.codesensi.leaf.rbac.system.dto.LoginAccountDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;
import org.mapstruct.Mapper;

/**
 * 登录相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface LoginMapper {

    /**
     * LoginAccountRequest → LoginAccountDTO
     */
    LoginAccountDTO toDTO(LoginAccountRequest request);

    /**
     * LoginResultDTO → LoginResponse
     */
    LoginResponse toResponse(LoginResultDTO loginResultDTO);

}
