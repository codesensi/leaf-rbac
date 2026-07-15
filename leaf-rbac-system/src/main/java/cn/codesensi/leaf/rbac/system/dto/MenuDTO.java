package cn.codesensi.leaf.rbac.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 路由菜单
 */
@Data
@Accessors(chain = true)
@Schema(description = "菜单信息")
public class MenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路由菜单ID
     */
    @Schema(description = "路由菜单ID", example = "1")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 父级路由菜单ID
     */
    @Schema(description = "父级路由菜单ID", example = "0")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long pid;

    /**
     * 路由名称(外链地址)
     */
    @Schema(description = "路由名称", example = "system")
    private String name;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径", example = "/system")
    private String path;

    /**
     * 路由参数
     */
    @Schema(description = "路由参数", example = "id=1")
    private String param;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "系统管理")
    private String title;

    /**
     * 菜单类型:1-目录,2-菜单,3-按钮
     */
    @Schema(description = "菜单类型:1-目录,2-菜单,3-按钮", example = "1")
    private Integer type;

    /**
     * 菜单排序
     */
    @Schema(description = "菜单排序", example = "1")
    private Integer sort;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标", example = "system")
    private String icon;

    /**
     * 权限编码
     */
    @Schema(description = "权限编码", example = "system:user:list")
    private String perms;

    /**
     * 是否外链:0-否,1-是
     */
    @Schema(description = "是否外链:0-否,1-是", example = "0")
    private Integer isLink;

    /**
     * 是否内嵌iframe:0-否,1-是
     */
    @Schema(description = "是否内嵌iframe:0-否,1-是", example = "0")
    private Integer isFrame;

    /**
     * 内嵌iframe地址
     */
    @Schema(description = "内嵌iframe地址", example = "https://www.baidu.com")
    private String frameSrc;

    /**
     * 是否显示:0-否,1-是
     */
    @Schema(description = "是否显示:0-否,1-是", example = "1")
    private Integer isShow;

    /**
     * 是否显示父级菜单:0-否,1-是
     */
    @Schema(description = "是否显示父级菜单:0-否,1-是", example = "0")
    private Integer isShowParent;

    /**
     * 菜单状态:0-启用,1-禁用
     */
    @Schema(description = "菜单状态:0-启用,1-禁用", example = "0")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统管理菜单")
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    @Schema(description = "系统内置标识:0-自定义,1-内置", example = "1")
    private Integer sysFlag;

}
