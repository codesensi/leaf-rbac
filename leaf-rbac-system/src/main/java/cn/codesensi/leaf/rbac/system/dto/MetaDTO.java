package cn.codesensi.leaf.rbac.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 路由元信息
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@Schema(description = "路由元信息")
public class MetaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String title;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 是否显示该菜单
     */
    @Schema(description = "是否显示该菜单")
    private Boolean showLink;

    /**
     * 菜单排序
     * 值越高排的越后
     */
    @Schema(description = "菜单排序")
    private Integer sort;

    /**
     * 是否显示父级菜单
     */
    @Schema(description = "是否显示父级菜单")
    private Boolean showParent;

    /**
     * 内嵌的iframe链接地址
     */
    @Schema(description = "内嵌的iframe链接地址")
    private String frameSrc;

}
