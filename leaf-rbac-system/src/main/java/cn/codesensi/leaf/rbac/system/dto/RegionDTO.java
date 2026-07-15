package cn.codesensi.leaf.rbac.system.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行政区划缓存数据 DTO
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Data
public class RegionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 行政区划代码
     */
    private String code;

    /**
     * 行政区划名称
     */
    private String name;

}
