package cn.codesensi.leaf.rbac.api.controller.log;

import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.entity.LogLogin;
import cn.codesensi.leaf.rbac.system.service.LogLoginService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import static cn.codesensi.leaf.rbac.system.entity.table.LogLoginTableDef.LOG_LOGIN;

/**
 * 登录日志表 控制层。
 *
 * @author codesensi
 * @since 2026-07-06
 */
@Tag(name = "登录日志管理", description = "登录日志相关接口")
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/log/login")
public class LogLoginController {

    private final LogLoginService logLoginService;

    /**
     * 根据主键获取登录日志表。
     *
     * @param id 登录日志表主键
     * @return 登录日志表详情
     */
    @Operation(summary = "获取登录日志详情", description = "根据主键获取登录日志详情")
    // @GetMapping("/detail/{id}")
    public LogLogin getInfo(@Parameter(description = "日志主键ID", required = true) @PathVariable Long id) {
        return logLoginService.getById(id);
    }

    /**
     * 分页查询登录日志表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param logLogin   操作日志表
     * @return 分页对象
     */
    @Operation(summary = "分页查询登录日志", description = "分页查询登录日志")
    // @GetMapping("/page")
    public Page<LogLogin> page(@Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNumber,
                               @Parameter(description = "每页数据数量") @RequestParam(defaultValue = "10") Integer pageSize,
                               @ParameterObject LogLogin logLogin) {
        Page<LogLogin> page = new Page<>(pageNumber, pageSize);
        return logLoginService.queryChain()
                .select(LOG_LOGIN.ALL_COLUMNS)
                .from(LOG_LOGIN)
                // TODO 查询条件
                // .where(LOG_LOGIN.ID.eq(logLogin.getId()))
                .page(page);
    }

}
