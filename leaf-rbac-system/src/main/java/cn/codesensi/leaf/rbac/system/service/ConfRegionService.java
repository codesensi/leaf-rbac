package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import com.mybatisflex.core.service.IService;

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
}
