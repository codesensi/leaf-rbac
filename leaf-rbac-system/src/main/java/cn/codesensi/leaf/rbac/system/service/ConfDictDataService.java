package cn.codesensi.leaf.rbac.system.service;

import cn.codesensi.leaf.rbac.system.dto.DictDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfDictData;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 字典数据配置表 服务层。
 *
 * @author codesensi
 * @since 2026-07-22
 */
public interface ConfDictDataService extends IService<ConfDictData> {

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    List<DictDTO> listDataByType(String dictType);

    /**
     * 根据字典类型批量获取字典数据
     *
     * @param dictTypeList 字典类型列表
     * @return 字典数据列表
     */
    List<DictDTO> listDataByTypeList(List<String> dictTypeList);

}
