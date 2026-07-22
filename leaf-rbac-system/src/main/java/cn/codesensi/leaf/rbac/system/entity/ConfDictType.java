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
import java.util.List;

/**
 * 字典类型配置表 实体类。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "conf_dict_type", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class ConfDictType extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @Id
    private Long id;

    /**
     * 字典类型
     */
    private String type;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 字典数据列表
     * 非数据库字段
     */
    private List<ConfDictData> dataList;

}
