package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.api.converter.DictConverter;
import cn.codesensi.leaf.rbac.api.response.DictResponse;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.dto.DictDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfDictData;
import cn.codesensi.leaf.rbac.system.service.ConfDictDataService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据配置表 控制层。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@ApiResponseBody
@RequiredArgsConstructor
@Tag(name = "字典数据配置", description = "字典数据配置相关接口")
@RestController
@RequestMapping("/conf/dict/data")
public class ConfDictDataController {

    private final ConfDictDataService confDictDataService;
    private final DictConverter dictConverter;

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

    /**
     * 根据字典类型批量获取字典数据
     *
     * @param dictTypeList 字典类型列表
     * @return 字典数据列表
     */
    @Operation(summary = "根据字典类型批量获取字典数据", description = "根据字典类型批量获取字典数据")
    @GetMapping("/listDictByTypeList")
    public List<DictResponse> listDictByTypeList(@Parameter(description = "字典类型列表") @RequestParam(required = false) List<String> dictTypeList) {
        List<DictDTO> dictDTOList = confDictDataService.listDictByTypeList(dictTypeList);
        return dictConverter.toResponseList(dictDTOList);
    }

}
