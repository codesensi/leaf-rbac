package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import cn.codesensi.leaf.rbac.system.service.ConfDictTypeService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型配置表 控制层。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@RestController
@RequestMapping("/confDictType")
public class ConfDictTypeController {

    @Autowired
    private ConfDictTypeService confDictTypeService;

    /**
     * 保存字典类型配置表。
     *
     * @param confDictType 字典类型配置表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    // @PostMapping("save")
    public boolean save(@RequestBody ConfDictType confDictType) {
        return confDictTypeService.save(confDictType);
    }

    /**
     * 根据主键删除字典类型配置表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    // @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return confDictTypeService.removeById(id);
    }

    /**
     * 根据主键更新字典类型配置表。
     *
     * @param confDictType 字典类型配置表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    // @PutMapping("update")
    public boolean update(@RequestBody ConfDictType confDictType) {
        return confDictTypeService.updateById(confDictType);
    }

    /**
     * 查询所有字典类型配置表。
     *
     * @return 所有数据
     */
    // @GetMapping("list")
    public List<ConfDictType> list() {
        return confDictTypeService.list();
    }

    /**
     * 根据主键获取字典类型配置表。
     *
     * @param id 字典类型配置表主键
     * @return 字典类型配置表详情
     */
    // @GetMapping("getInfo/{id}")
    public ConfDictType getInfo(@PathVariable Long id) {
        return confDictTypeService.getById(id);
    }

    /**
     * 分页查询字典类型配置表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    // @GetMapping("page")
    public Page<ConfDictType> page(Page<ConfDictType> page) {
        return confDictTypeService.page(page);
    }

}
