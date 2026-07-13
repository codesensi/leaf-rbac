package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.system.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行政区划配置表 实体类。
 *
 * @author codesensi
 * @since 2026-07-10
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("conf_region")
public class ConfRegion extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 行政区划ID
     */
    @Id
    @Schema(description = "行政区划ID")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 父级代码
     */
    @Schema(description = "父级代码")
    private String pcode;

    /**
     * 行政区划代码
     */
    @Schema(description = "行政区划代码")
    private String code;

    /**
     * 行政区划名称
     */
    @Schema(description = "行政区划名称")
    private String name;

    /**
     * 层级:1-省;2-市;3-县（区）
     */
    @Schema(description = "层级")
    private Integer level;

    /**
     * 物化路径: /110000/110100/110101/
     */
    @Schema(description = "物化路径")
    private String fullPath;

}
