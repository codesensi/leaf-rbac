package cn.codesensi.leaf.rbac.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 路由菜单
 * 配置@JsonInclude(Include.NON_NULL)的注解，解决传null值给Vue动态路由渲染时出错
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@Schema(description = "路由菜单树")
public class RouteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    private String path;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String name;

    /**
     * 路由名称
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由元信息
     */
    @Schema(description = "路由元信息")
    private MetaDTO meta;

    /**
     * 子路由
     */
    @Schema(description = "子路由")
    private List<RouteDTO> children;
}
