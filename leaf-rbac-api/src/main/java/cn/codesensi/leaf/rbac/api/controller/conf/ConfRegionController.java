package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行政区划配置表 控制层。
 *
 * @author codesensi
 * @since 2026-07-10
 */
@RestController
@RequestMapping("/conf/region")
public class ConfRegionController {

    @Autowired
    private ConfRegionService confRegionService;

    /**
     * 保存行政区划配置表。
     *
     * @param confRegion 行政区划配置表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody ConfRegion confRegion) {
        return confRegionService.save(confRegion);
    }

    /**
     * 根据主键删除行政区划配置表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return confRegionService.removeById(id);
    }

    /**
     * 根据主键更新行政区划配置表。
     *
     * @param confRegion 行政区划配置表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody ConfRegion confRegion) {
        return confRegionService.updateById(confRegion);
    }

    /**
     * 查询所有行政区划配置表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<ConfRegion> list() {
        return confRegionService.list();
    }

    /**
     * 根据主键获取行政区划配置表。
     *
     * @param id 行政区划配置表主键
     * @return 行政区划配置表详情
     */
    @GetMapping("getInfo/{id}")
    public ConfRegion getInfo(@PathVariable Long id) {
        return confRegionService.getById(id);
    }

    /**
     * 分页查询行政区划配置表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<ConfRegion> page(Page<ConfRegion> page) {
        return confRegionService.page(page);
    }

    /**
     * 从民政部导入行政区划
     */
    @PostMapping("/importFromMCA")
    public void importFromMCA() {
        confRegionService.importFromMCA();
    }

}
