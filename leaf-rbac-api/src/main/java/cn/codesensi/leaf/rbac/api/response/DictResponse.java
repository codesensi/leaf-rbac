package cn.codesensi.leaf.rbac.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 字典响应结果
 *
 * @author codesensi
 * @since 2026-07-13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Schema(description = "字典响应结果")
public class DictResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String type;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 类型状态:0-启用,1-禁用
     */
    @Schema(description = "字典类型状态:0-启用,1-禁用")
    private Integer status;

    /**
     * 类型备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 字典数据列表
     */
    @Schema(description = "字典数据列表")
    private List<DictDataResponse> dataList;

}
