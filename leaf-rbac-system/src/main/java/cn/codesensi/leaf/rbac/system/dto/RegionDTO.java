package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行政区划缓存数据 DTO
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Data
@Accessors(chain = true)
public class RegionDTO implements Serializable {

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
