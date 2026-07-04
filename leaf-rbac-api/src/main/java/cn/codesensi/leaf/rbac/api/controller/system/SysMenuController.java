package cn.codesensi.leaf.rbac.api.controller.system;

import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.system.entity.SysMenu;
import cn.codesensi.leaf.rbac.system.service.SysMenuService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysMenuTableDef.SYS_MENU;

/**
 * 路由菜单表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Tag(name = "菜单管理", description = "路由菜单相关接口")
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 保存路由菜单表。
     *
     * @param sysMenu 路由菜单表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @Operation(summary = "保存菜单", description = "保存路由菜单表")
    @PostMapping("/save")
    public boolean save(@RequestBody SysMenu sysMenu) {
        return sysMenuService.save(sysMenu);
    }

    /**
     * 根据主键删除路由菜单表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Operation(summary = "删除菜单", description = "根据主键删除路由菜单")
    @DeleteMapping("/delete/{id}")
    public boolean delete(@Parameter(description = "菜单主键ID", required = true) @PathVariable Long id) {
        return sysMenuService.removeById(id);
    }

    /**
     * 根据主键更新路由菜单表。
     *
     * @param sysMenu 路由菜单表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Operation(summary = "更新菜单", description = "根据主键更新路由菜单")
    @PutMapping("/update")
    public boolean update(@RequestBody SysMenu sysMenu) {
        return sysMenuService.updateById(sysMenu);
    }

    /**
     * 查询所有路由菜单表。
     *
     * @return 所有数据
     */
    @Operation(summary = "查询菜单列表", description = "查询所有路由菜单")
    @GetMapping("/list")
    public List<SysMenu> list() {
        return sysMenuService.list();
    }

    /**
     * 根据主键获取路由菜单表。
     *
     * @param id 路由菜单表主键
     * @return 路由菜单表详情
     */
    @Operation(summary = "获取菜单详情", description = "根据主键获取路由菜单详情")
    @GetMapping("/detail/{id}")
    public SysMenu detail(@Parameter(description = "菜单主键ID", required = true) @PathVariable Long id) {
        return sysMenuService.getById(id);
    }

    /**
     * 分页查询路由菜单表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param sysMenu    路由菜单表
     * @return 分页对象
     */
    @Operation(summary = "分页查询菜单", description = "分页查询路由菜单")
    @GetMapping("page")
    public Page<SysMenu> page(@Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNumber,
                              @Parameter(description = "每页数据数量") @RequestParam(defaultValue = "10") Integer pageSize,
                              @ParameterObject SysMenu sysMenu) {
        Page<SysMenu> page = new Page<>(pageNumber, pageSize);
        return sysMenuService.queryChain()
                .select(SYS_MENU.ALL_COLUMNS)
                .from(SYS_MENU)
                // TODO 查询条件
                // .where(SYS_MENU.ID.eq(sysMenu.getId()))
                .page(page);
    }

}
