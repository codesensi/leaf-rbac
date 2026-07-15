package cn.codesensi.leaf.rbac.api.controller.system;

import cn.codesensi.leaf.rbac.api.mapper.RoleMapper;
import cn.codesensi.leaf.rbac.api.request.AssignMenusRequest;
import cn.codesensi.leaf.rbac.api.request.RoleSaveRequest;
import cn.codesensi.leaf.rbac.common.enums.OperateType;
import cn.codesensi.leaf.rbac.framework.annotation.ApiResponseBody;
import cn.codesensi.leaf.rbac.framework.annotation.LogOperate;
import cn.codesensi.leaf.rbac.system.dto.AssignMenusDTO;
import cn.codesensi.leaf.rbac.system.dto.RoleSaveDTO;
import cn.codesensi.leaf.rbac.system.entity.SysRole;
import cn.codesensi.leaf.rbac.system.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.SysRoleTableDef.SYS_ROLE;

/**
 * 角色信息表 控制层。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Tag(name = "角色管理", description = "角色信息相关接口")
@ApiResponseBody
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final RoleMapper roleMapper;

    /**
     * 根据主键删除角色信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Operation(summary = "删除角色", description = "根据主键删除角色信息")
    @DeleteMapping("/delete/{id}")
    public boolean delete(@Parameter(description = "角色主键ID", required = true) @PathVariable Long id) {
        return sysRoleService.removeById(id);
    }

    /**
     * 根据主键更新角色信息表。
     *
     * @param sysRole 角色信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Operation(summary = "更新角色", description = "根据主键更新角色信息")
    @PutMapping("/update")
    public boolean update(@RequestBody SysRole sysRole) {
        return sysRoleService.updateById(sysRole);
    }

    /**
     * 查询所有角色信息表。
     *
     * @return 所有数据
     */
    @Operation(summary = "查询角色列表", description = "查询所有角色信息")
    @GetMapping("/list")
    public List<SysRole> list() {
        return sysRoleService.list();
    }

    /**
     * 根据主键获取角色信息表。
     *
     * @param id 角色信息表主键
     * @return 角色信息表详情
     */
    @Operation(summary = "获取角色详情", description = "根据主键获取角色信息详情")
    @GetMapping("/detail/{id}")
    public SysRole detail(@Parameter(description = "角色主键ID", required = true) @PathVariable Long id) {
        return sysRoleService.getById(id);
    }

    /**
     * 分页查询角色信息表。
     *
     * @param pageNumber 当前页码
     * @param pageSize   每页数据数量
     * @param sysRole    角色信息表
     * @return 分页对象
     */
    @Operation(summary = "分页查询角色", description = "分页查询角色信息")
    @GetMapping("/page")
    public Page<SysRole> page(@Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNumber,
                              @Parameter(description = "每页数据数量") @RequestParam(defaultValue = "10") Integer pageSize,
                              @ParameterObject SysRole sysRole) {
        Page<SysRole> page = new Page<>(pageNumber, pageSize);
        return sysRoleService.queryChain()
                .select(SYS_ROLE.ALL_COLUMNS)
                .from(SYS_ROLE)
                // TODO 查询条件
                // .where(SYS_ROLE.ID.eq(sysRole.getId()))
                .page(page);
    }

    /**
     * 保存角色信息
     *
     * @param request 角色信息
     */
    @LogOperate(module = "角色管理", type = OperateType.INSERT, desc = "保存角色")
    @Operation(summary = "保存角色", description = "保存角色信息")
    @PostMapping("/saveRole")
    public void saveRole(@Valid @RequestBody RoleSaveRequest request) {
        RoleSaveDTO roleSaveDTO = roleMapper.toSaveDTO(request);
        sysRoleService.saveRole(roleSaveDTO);
    }

    /**
     * 分配角色菜单权限
     *
     * @param request 角色菜单权限信息
     */
    @LogOperate(module = "角色管理", desc = "分配角色菜单权限")
    @Operation(summary = "分配角色菜单权限", description = "为角色分配菜单权限")
    @PutMapping("/assignMenus")
    public void assignMenus(@Valid @RequestBody AssignMenusRequest request) {
        AssignMenusDTO assignMenusDTO = roleMapper.toAssignMenusDTO(request);
        sysRoleService.assignMenus(assignMenusDTO);
    }

}
