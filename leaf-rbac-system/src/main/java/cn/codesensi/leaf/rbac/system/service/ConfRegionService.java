package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.RegionDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 行政区划配置表 服务层。
 *
 * @author codesensi
 * @since 2026-07-10
 */
public interface ConfRegionService extends IService<ConfRegion> {

    /**
     * 从民政部导入行政区划
     */
    Integer importFromMCA();

    /**
     * 根据行政区划代码查询行政区划下属节点
     */
    List<RegionDTO> listChildrenByCode(String code);

}
