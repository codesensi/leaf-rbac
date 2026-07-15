package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行政区划缓存数据 DTO
 *
 * @author codesensi
 * @since 2026-07-13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Schema(description = "行政区划缓存数据")
public class RegionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 行政区划代码
     */
    @Schema(description = "行政区划代码", example = "110000")
    private String code;

    /**
     * 行政区划名称
     */
    @Schema(description = "行政区划名称", example = "北京市")
    private String name;

}
