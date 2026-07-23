package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 字典类型配置表 服务层。
 *
 * @author codesensi
 * @since 2026-07-22
 */
public interface ConfDictTypeService extends IService<ConfDictType> {

    /**
     * 根据字典类型获取字典类型配置
     * 字典类型为空时返回所有字典类型配置
     *
     * @param dictType 字典类型
     * @return 字典类型配置
     */
    List<ConfDictType> listTypeByType(String dictType);

    /**
     * 根据字典类型获取字典类型配置
     * 字典类型为空时返回所有字典类型配置
     *
     * @param dictTypeList 字典类型列表
     * @return 字典类型配置
     */
    List<ConfDictType> listTypeByTypeList(List<String> dictTypeList);

}
