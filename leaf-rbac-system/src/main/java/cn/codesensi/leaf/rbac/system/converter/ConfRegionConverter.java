package cn.codesensi.leaf.rbac.system.converter;

import cn.codesensi.leaf.rbac.system.dto.RegionDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 行政区划相关对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface ConfRegionConverter {

    /**
     * ConfRegion → RegionDTO
     */
    RegionDTO toRegionDTO(ConfRegion confRegion);

    /**
     * List<ConfRegion> → List<RegionDTO>
     */
    List<RegionDTO> toRegionDTOList(List<ConfRegion> confRegions);

}
