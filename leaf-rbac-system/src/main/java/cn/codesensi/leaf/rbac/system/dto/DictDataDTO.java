package cn.codesensi.leaf.rbac.system.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据 DTO
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Data
public class DictDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    private String code;

    /**
     * 字典键值
     */
    private String value;

    /**
     * 字典状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 字典排序:数字越小越靠前
     */
    private Integer sort;

    /**
     * 是否默认:0-否,1-是
     */
    private Integer isDefault;

    /**
     * 字典备注
     */
    private String remark;

}
