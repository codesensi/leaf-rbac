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
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfDictDataTableDef.CONF_DICT_DATA;
import static cn.codesensi.leaf.rbac.system.entity.table.ConfDictTypeTableDef.CONF_DICT_TYPE;

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
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    @Cacheable(value = CacheConst.DICT, key = "#dictType", condition = "T(org.springframework.util.StringUtils).hasText(#dictType)")
    @Override
    public List<DictDTO> listDataByType(String dictType) {
        // 1. 查询字典类型列表
        QueryChain<ConfDictType> typeQuery = confDictTypeService.queryChain()
                .select(CONF_DICT_TYPE.ALL_COLUMNS);
        if (StrUtil.isNotBlank(dictType)) {
            typeQuery.where(CONF_DICT_TYPE.TYPE.eq(dictType));
        }
        List<String> roleList = StpUtil.getRoleList();
        // 非管理员角色只能获取启用的字典类型
        if (!roleList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            typeQuery.and(CONF_DICT_TYPE.STATUS.eq(EnableEnum.ENABLE.getCode()));
        }
        List<ConfDictType> typeList = typeQuery.list();

        if (CollUtil.isEmpty(typeList)) {
            return List.of();
        }

        // 2. 批量查询所有字典数据（避免 N+1）
        List<String> typeValues = typeList.stream()
                .map(ConfDictType::getType)
                .distinct()
                .toList();
        QueryChain<ConfDictData> dataQuery = QueryChain.of(confDictDataMapper)
                .select(CONF_DICT_DATA.ALL_COLUMNS)
                .where(CONF_DICT_DATA.TYPE.in(typeValues));
        if (StrUtil.isNotBlank(dictType)) {
            dataQuery.where(CONF_DICT_DATA.TYPE.eq(dictType));
        }

        // 非管理员角色只能获取启用的字典数据
        if (!roleList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            dataQuery.and(CONF_DICT_DATA.STATUS.eq(EnableEnum.ENABLE.getCode()));
        }
        List<ConfDictData> dataList = dataQuery
                .orderBy(CONF_DICT_DATA.SORT, true)
                .list();

        // 3. 按 type 分组并装配
        Map<String, List<ConfDictData>> dataMap = dataList.stream()
                .collect(Collectors.groupingBy(ConfDictData::getType));
        typeList.forEach(type -> type.setDataList(dataMap.getOrDefault(type.getType(), List.of())));

        return confDictConverter.toDictDTOList(typeList);
    }

    /**
     * 根据字典类型批量获取字典数据
     *
     * @param dictTypeList 字典类型列表
     * @return 字典数据列表
     */
    @Override
    public List<DictDTO> listDataByTypeList(List<String> dictTypeList) {
        List<DictDTO> dictDTOList = new ArrayList<>();
        // 如果字典类型列表为空，则返回所有字典数据
        if (CollUtil.isEmpty(dictTypeList)) {
            List<DictDTO> dictDTO = self.listDataByType(null);
            dictDTOList.addAll(dictDTO);
            return dictDTOList;
        }
        for (String dictType : dictTypeList) {
            List<DictDTO> dictDTO = self.listDataByType(dictType);
            dictDTOList.addAll(dictDTO);
        }
        return dictDTOList;
    }
}
