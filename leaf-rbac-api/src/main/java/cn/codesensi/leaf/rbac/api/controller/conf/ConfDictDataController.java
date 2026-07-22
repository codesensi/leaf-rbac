package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.system.entity.ConfDictData;
import cn.codesensi.leaf.rbac.system.service.ConfDictDataService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据配置表 控制层。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@RestController
@RequestMapping("/confDictData")
public class ConfDictDataController {

    @Autowired
    private ConfDictDataService confDictDataService;

    /**
     * 保存字典数据配置表。
     *
     * @param confDictData 字典数据配置表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    // @PostMapping("save")
    public boolean save(@RequestBody ConfDictData confDictData) {
        return confDictDataService.save(confDictData);
    }

    /**
     * 根据主键删除字典数据配置表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    // @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return confDictDataService.removeById(id);
    }

    /**
     * 根据主键更新字典数据配置表。
     *
     * @param confDictData 字典数据配置表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    // @PutMapping("update")
    public boolean update(@RequestBody ConfDictData confDictData) {
        return confDictDataService.updateById(confDictData);
    }

    /**
     * 查询所有字典数据配置表。
     *
     * @return 所有数据
     */
    // @GetMapping("list")
    public List<ConfDictData> list() {
        return confDictDataService.list();
    }

    /**
     * 根据主键获取字典数据配置表。
     *
     * @param id 字典数据配置表主键
     * @return 字典数据配置表详情
     */
    // @GetMapping("getInfo/{id}")
    public ConfDictData getInfo(@PathVariable Long id) {
        return confDictDataService.getById(id);
    }

    /**
     * 分页查询字典数据配置表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    // @GetMapping("page")
    public Page<ConfDictData> page(Page<ConfDictData> page) {
        return confDictDataService.page(page);
    }

}
