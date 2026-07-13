package cn.codesensi.leaf.rbac.system.mapper;

import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;

/**
 * 行政区划配置表 映射层。
 *
 * @author codesensi
 * @since 2026-07-10
 */
public interface ConfRegionMapper extends BaseMapper<ConfRegion> {

    /**
     * 物理删除全部数据（绕过全局逻辑删除）
     */
    @Delete("DELETE FROM conf_region")
    int deleteAll();

}
