package cn.codesensi.leaf.rbac.system.base;

import com.mybatisflex.annotation.Column;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
@Accessors(chain = true)
@Schema(description = "基础实体类")
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private Long updater;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @Column(onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识:0-未删除,1-已删除
     */
    @Schema(description = "逻辑删除标识:0-未删除,1-已删除")
    private Integer delFlag;

}
