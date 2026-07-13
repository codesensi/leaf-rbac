package cn.codesensi.leaf.rbac.api.controller.conf;

import cn.codesensi.leaf.rbac.common.enums.OperateType;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.framework.annotation.LogOperate;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
