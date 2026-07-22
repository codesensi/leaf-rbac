package cn.codesensi.leaf.rbac.api.converter;

import cn.codesensi.leaf.rbac.api.response.DictResponse;
import cn.codesensi.leaf.rbac.system.dto.DictDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典对象转换器（DTO → Response）
 *
 * @author codesensi
 * @since 2026-07-22
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    /**
     * DictDTO → DictResponse
     */
    DictResponse toResponse(DictDTO dictDTO);

    /**
     * List<DictDTO> → List<DictResponse>
     */
    List<DictResponse> toResponseList(List<DictDTO> dictDTOList);

}
