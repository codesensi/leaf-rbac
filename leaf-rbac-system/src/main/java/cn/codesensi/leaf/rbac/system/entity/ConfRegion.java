package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.common.core.BaseEntity;
import cn.codesensi.leaf.rbac.framework.listener.MybatisFlexListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
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
@Table(value = "conf_region", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class ConfRegion extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 行政区划ID
     */
    @Id
    private Long id;

    /**
     * 父级代码
     */
    private String pcode;

    /**
     * 行政区划代码
     */
    private String code;

    /**
     * 行政区划名称
     */
    private String name;

    /**
     * 层级:1-省;2-市;3-县（区）
     */
    private Integer level;

    /**
     * 物化路径: /110000/110100/110101/
     */
    private String fullPath;

}
