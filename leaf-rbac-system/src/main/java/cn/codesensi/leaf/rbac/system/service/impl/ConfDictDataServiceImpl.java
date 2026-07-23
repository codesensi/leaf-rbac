package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.common.enums.EnableEnum;
import cn.codesensi.leaf.rbac.system.converter.ConfDictConverter;
import cn.codesensi.leaf.rbac.system.dto.DictDTO;
import cn.codesensi.leaf.rbac.system.entity.ConfDictData;
import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import cn.codesensi.leaf.rbac.system.mapper.ConfDictDataMapper;
import cn.codesensi.leaf.rbac.system.service.ConfDictDataService;
import cn.codesensi.leaf.rbac.system.service.ConfDictTypeService;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfDictDataTableDef.CONF_DICT_DATA;

/**
 * 字典数据配置表 服务层实现。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@RequiredArgsConstructor
@Service
public class ConfDictDataServiceImpl extends ServiceImpl<ConfDictDataMapper, ConfDictData> implements ConfDictDataService {

    private final ConfDictDataMapper confDictDataMapper;
    private final ConfDictTypeService confDictTypeService;
    private final ConfDictConverter confDictConverter;

    // 懒加载的Setter注入，解决自调用无法缓存和循环依赖的问题
    private ConfDictDataService self;

    @Autowired
    public void setSelf(@Lazy ConfDictDataService self) {
        this.self = self;
    }

    /**
     * 根据字典类型获取字典配置
     * 查询全量字典时由 listDictByTypeList 调用，确保 dictType 不为空
     *
     * @param dictType 字典类型
     * @return 字典配置
     */
    @Cacheable(value = CacheConst.DICT, key = "#dictType")
    @Override
    public List<DictDTO> listDictByType(String dictType) {
        // 1. 获取type数据
        List<ConfDictType> confDictTypeList = confDictTypeService.listTypeByType(dictType);
        List<String> typeList = confDictTypeList.stream()
                .map(ConfDictType::getType)
                .toList();
        // 2. 获取data数据
        List<ConfDictData> confDictDataList = listDataByTypeList(typeList);
        // 3. 组织返回数据：按 type 分组并装配数据
        Map<String, List<ConfDictData>> dataMap = confDictDataList.stream()
                .collect(Collectors.groupingBy(ConfDictData::getType));
        confDictTypeList.forEach(type -> type.setDataList(dataMap.getOrDefault(type.getType(), List.of())));
        return confDictConverter.toDictDTOList(confDictTypeList);
    }

    /**
     * 根据字典类型获取字典配置
     *
     * @param dictTypeList 字典类型列表
     * @return 字典配置
     */
    @Override
    public List<DictDTO> listDictByTypeList(List<String> dictTypeList) {
        List<ConfDictType> confDictTypeList = confDictTypeService.listTypeByTypeList(dictTypeList);
        return confDictTypeList.stream()
                .map(confDictType -> self.listDictByType(confDictType.getType()))
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * 根据字典类型批量获取字典数据
     *
     * @param dictTypeList 字典类型列表
     * @return 字典数据列表
     */
    @Override
    public List<ConfDictData> listDataByTypeList(List<String> dictTypeList) {
        QueryChain<ConfDictData> dataQuery = QueryChain.of(confDictDataMapper)
                .select(CONF_DICT_DATA.ALL_COLUMNS)
                .where(CONF_DICT_DATA.TYPE.in(dictTypeList));

        List<String> roleList = StpUtil.getRoleList();
        // 非管理员角色只能获取启用的字典类型
        if (!roleList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            dataQuery.and(CONF_DICT_DATA.STATUS.eq(EnableEnum.ENABLE.getCode()));
        }
        return dataQuery
                .orderBy(CONF_DICT_DATA.SORT, true)
                .list();
    }
}
