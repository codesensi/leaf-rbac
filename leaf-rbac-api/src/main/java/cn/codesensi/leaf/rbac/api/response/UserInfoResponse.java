package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 获取当前用户信息响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@Schema(description = "获取当前用户信息响应结果")
public class UserInfoResponse implements Serializable {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /**
     * 头像
     */
    @Schema(description = "头像", example = "https://xxx.com/avatar.png")
    private String avatar;

    /**
     * 用户身份证号码
     */
    @Schema(description = "用户身份证号码", example = "110101199001011234")
    private String idNo;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "admin@example.com")
    private String email;

    /**
     * 用户手机号码
     */
    @Schema(description = "用户手机号码", example = "13800138000")
    private String phone;

    /**
     * 用户性别:0-保密,1-男,2-女
     */
    @Schema(description = "用户性别:0-保密,1-男,2-女", example = "1")
    private Integer gender;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    @Schema(description = "系统内置标识:0-自定义,1-内置", example = "1")
    private Integer sysFlag;

    /**
     * 角色
     */
    @Schema(description = "角色", example = "[\"admin\",\"user\"]")
    private List<String> roles;

    /**
     * 权限
     */
    @Schema(description = "权限", example = "[\"system:user:list\",\"system:user:add\"]")
    private List<String> permissions;

    /**
     * 菜单
     */
    @Schema(description = "菜单")
    private List<MenuResponse> menus;
}
