package cn.codesensi.leaf.rbac.api.controller.log;

import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.entity.LogOperate;
import cn.codesensi.leaf.rbac.system.service.LogOperateService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import static cn.codesensi.leaf.rbac.system.entity.table.LogOperateTableDef.LOG_OPERATE;

/**
 * 操作日志表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Tag(name = "操作日志管理", description = "操作日志相关接口")
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/log/operate")
public class LogOperateController {

    private final LogOperateService logOperateService;

    /**
     * 根据主键获取操作日志表。
     *
     * @param id 操作日志表主键
     * @return 操作日志表详情
     */
    @Operation(summary = "获取操作日志详情", description = "根据主键获取操作日志详情")
    @GetMapping("/detail/{id}")
    public LogOperate detail(@Parameter(description = "日志主键ID", required = true) @PathVariable Long id) {
        return logOperateService.getById(id);
    }

    /**
     * 分页查询操作日志表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param logOperate 操作日志表
     * @return 分页对象
     */
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志")
    @GetMapping("/page")
    public Page<LogOperate> page(@Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNumber,
                                 @Parameter(description = "每页数据数量") @RequestParam(defaultValue = "10") Integer pageSize,
                                 @ParameterObject LogOperate logOperate) {
        Page<LogOperate> page = new Page<>(pageNumber, pageSize);
        return logOperateService.queryChain()
                .select(LOG_OPERATE.ALL_COLUMNS)
                .from(LOG_OPERATE)
                // TODO 查询条件
                // .where(LOG_OPERATE.ID.eq(logOperate.getId()))
                .page(page);
    }

}
