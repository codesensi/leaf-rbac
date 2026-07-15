package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.request.CaptchaRequest;
import cn.codesensi.leaf.rbac.api.response.CaptchaResponse;
import cn.codesensi.leaf.rbac.system.dto.CaptchaDTO;
import cn.codesensi.leaf.rbac.system.dto.CaptchaResultDTO;
import org.mapstruct.Mapper;

/**
 * 验证码相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface CaptchaConverter {

    /**
     * CaptchaRequest → CaptchaDTO
     */
    CaptchaDTO toDTO(CaptchaRequest request);

    /**
     * CaptchaResultDTO → CaptchaResponse
     */
    CaptchaResponse toResponse(CaptchaResultDTO captchaResultDTO);

}
