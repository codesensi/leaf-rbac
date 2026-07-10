package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 获取当前用户信息响应结果
 *
 * @author codesensi
 * @since 2024/1/21 15:39
 */
@Data
@Accessors(chain = true)
public class UserInfoOutDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 用户身份证号码
     */
    @Schema(description = "用户身份证号码")
    private String idNo;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 用户手机号码
     */
    @Schema(description = "用户手机号码")
    private String phone;

    /**
     * 用户性别:0-保密,1-男,2-女
     */
    @Schema(description = "用户性别:0-保密,1-男,2-女")
    private Integer gender;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    @Schema(description = "系统内置标识:0-自定义,1-内置")
    private Integer sysFlag;

    /**
     * 角色
     */
    @Schema(description = "角色")
    private List<String> roles;

    /**
     * 权限
     */
    @Schema(description = "权限")
    private List<String> permissions;

    /**
     * 菜单
     */
    @Schema(description = "菜单")
    private List<MenuOutDTO> menus;
}
