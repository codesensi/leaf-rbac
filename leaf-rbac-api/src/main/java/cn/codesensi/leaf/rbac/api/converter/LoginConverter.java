package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.request.LoginRequest;
import cn.codesensi.leaf.rbac.api.response.LoginResponse;
import cn.codesensi.leaf.rbac.system.dto.LoginDTO;
import cn.codesensi.leaf.rbac.system.dto.LoginResultDTO;
import org.mapstruct.Mapper;

/**
 * 登录相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface LoginConverter {

    /**
     * LoginAccountRequest → LoginAccountDTO
     */
    LoginDTO toDTO(LoginRequest request);

    /**
     * LoginResultDTO → LoginResponse
     */
    LoginResponse toResponse(LoginResultDTO loginResultDTO);

}
