package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.api.converter.RegionConverter;
import cn.codesensi.leaf.rbac.api.response.RegionResponse;
import cn.codesensi.leaf.rbac.common.enums.OperateType;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.framework.annotation.LogOperate;
import cn.codesensi.leaf.rbac.system.dto.RegionDTO;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行政区划配置表 控制层。
 *
 * @author codesensi
 * @since 2026-07-10
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@Tag(name = "行政区划配置", description = "行政区划配置相关接口")
@RequestMapping("/conf/region")
public class ConfRegionController {

    private final ConfRegionService confRegionService;
    private final RegionConverter regionConverter;

    /**
     * 从民政部导入行政区划
     *
     * @return 导入数量
     */
    @LogOperate(module = "新政区划配置", type = OperateType.INSERT, desc = "从民政部导入行政区划")
    @Operation(summary = "从民政部导入行政区划", description = "从民政部导入行政区划")
    @PostMapping("/importFromMCA")
    public Integer importFromMCA() {
        return confRegionService.importFromMCA();
    }

    /**
     * 根据行政区划代码查询子节点
     *
     * @param code 行政区划代码
     * @return 子节点列表
     */
    @Operation(summary = "根据行政区划代码查询子节点", description = "根据行政区划代码查询子节点")
    @GetMapping("/listChildrenByCode")
    public List<RegionResponse> listChildrenByCode(@Parameter(description = "行政区划代码") @RequestParam(defaultValue = "0") String code) {
        List<RegionDTO> regionDTOS = confRegionService.listChildrenByCode(code);
        return regionConverter.toResponseList(regionDTOS);
    }

}
