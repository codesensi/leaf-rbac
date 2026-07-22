package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据响应结果
 *
 * @author codesensi
 * @since 2026-07-13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Schema(description = "字典数据响应结果")
public class DictDataResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @Schema(description = "字典编码")
    private String code;

    /**
     * 字典键值
     */
    @Schema(description = "字典键值")
    private String value;

    /**
     * 字典状态:0-启用,1-禁用
     */
    @Schema(description = "字典状态:0-启用,1-禁用")
    private Integer status;

    /**
     * 字典排序:数字越小越靠前
     */
    @Schema(description = "字典排序:数字越小越靠前")
    private Integer sort;

    /**
     * 是否默认:0-否,1-是
     */
    @Schema(description = "是否默认:0-否,1-是")
    private Integer isDefault;

    /**
     * 字典备注
     */
    @Schema(description = "备注")
    private String remark;

}
