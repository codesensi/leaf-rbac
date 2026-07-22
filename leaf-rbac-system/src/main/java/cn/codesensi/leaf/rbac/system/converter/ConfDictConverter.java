package cn.codesensi.leaf.rbac.system.converter;

import cn.codesensi.leaf.rbac.system.dto.DictDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典相关对象转换（Entity → DTO）
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface ConfDictConverter {

    /**
     * ConfDictType → DictDTO
     */
    DictDTO toDictDTO(ConfDictType confDictType);

    /**
     * List<ConfDictType> → List<DictDTO>
     */
    List<DictDTO> toDictDTOList(List<ConfDictType> confDictTypes);

}
