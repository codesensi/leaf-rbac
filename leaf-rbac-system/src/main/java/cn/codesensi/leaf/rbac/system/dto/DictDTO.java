package cn.codesensi.leaf.rbac.system.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 字典类型 DTO
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Data
public class DictDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    private String type;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 类型状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 类型备注
     */
    private String remark;

    /**
     * 字典数据列表
     */
    private List<DictDataDTO> dataList;

}
