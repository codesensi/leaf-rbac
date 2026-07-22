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
 * 字典数据配置表 实体类。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "conf_dict_data", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class ConfDictData extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典数据ID
     */
    @Id
    private Long id;

    /**
     * 字典类型
     */
    private String type;

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
     * 备注
     */
    private String remark;

}
