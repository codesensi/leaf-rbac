package cn.codesensi.leaf.rbac.api.controller.system;

import cn.codesensi.leaf.rbac.common.enums.OperateType;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.framework.annotation.LogOperate;
import cn.codesensi.leaf.rbac.system.entity.SysUser;
import cn.codesensi.leaf.rbac.system.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysUserTableDef.SYS_USER;

/**
 * 用户信息表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Tag(name = "用户管理", description = "用户信息相关接口")
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 保存用户信息表。
     *
     * @param sysUser 用户信息表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @LogOperate(module = "用户管理", type = OperateType.INSERT, desc = "保存用户", ignoreFields = {"password"})
    @Operation(summary = "保存用户", description = "保存用户信息表")
    @PostMapping("/save")
    public boolean save(@RequestBody SysUser sysUser) {
        return sysUserService.save(sysUser);
    }

    /**
     * 根据主键删除用户信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Operation(summary = "删除用户", description = "根据主键删除用户信息")
    @DeleteMapping("/delete/{id}")
    public boolean delete(@Parameter(description = "用户主键ID", required = true) @PathVariable Long id) {
        return sysUserService.removeById(id);
    }

    /**
     * 根据主键更新用户信息表。
     *
     * @param sysUser 用户信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Operation(summary = "更新用户", description = "根据主键更新用户信息")
    @PutMapping("/update")
    public boolean update(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }

    /**
     * 查询所有用户信息表。
     *
     * @return 所有数据
     */
    @Operation(summary = "查询用户列表", description = "查询所有用户信息")
    @GetMapping("/list")
    public List<SysUser> list() {
        return sysUserService.list();
    }

    /**
     * 根据主键获取用户信息表。
     *
     * @param id 用户信息表主键
     * @return 用户信息表详情
     */
    @Operation(summary = "获取用户详情", description = "根据主键获取用户信息详情")
    @GetMapping("/detail/{id}")
    public SysUser detail(@Parameter(description = "用户主键ID", required = true) @PathVariable Long id) {
        return sysUserService.getById(id);
    }

    /**
     * 分页查询用户信息表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @return 分页对象
     */
    @Operation(summary = "分页查询用户", description = "分页查询用户信息")
    @GetMapping("page")
    public Page<SysUser> page(@Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNumber,
                              @Parameter(description = "每页数据数量") @RequestParam(defaultValue = "10") Integer pageSize,
                              @ParameterObject SysUser sysUser) {
        Page<SysUser> page = new Page<>(pageNumber, pageSize);
        return sysUserService.queryChain()
                .select(SYS_USER.ALL_COLUMNS)
                .from(SYS_USER)
                // TODO 查询条件
                // .where(SYS_USER.ID.eq(sysUser.getId()))
                .page(page);
    }

}
