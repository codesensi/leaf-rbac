package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.response.RegionResponse;
import cn.codesensi.leaf.rbac.system.dto.RegionDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 行政区划相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface RegionConverter {

    /**
     * RegionDTO → RegionResponse
     */
    RegionResponse toResponse(RegionDTO regionDTO);

    /**
     * List<RegionDTO> → List<RegionResponse>
     */
    List<RegionResponse> toResponseList(List<RegionDTO> regionDTOS);

}
