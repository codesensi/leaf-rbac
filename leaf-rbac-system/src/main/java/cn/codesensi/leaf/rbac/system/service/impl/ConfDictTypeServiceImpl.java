package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.common.enums.EnableEnum;
import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import cn.codesensi.leaf.rbac.system.mapper.ConfDictTypeMapper;
import cn.codesensi.leaf.rbac.system.service.ConfDictTypeService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfDictTypeTableDef.CONF_DICT_TYPE;

/**
 * 字典类型配置表 服务层实现。
 *
 * @author codesensi
 * @since 2026-07-22
 */
@RequiredArgsConstructor
@Service
public class ConfDictTypeServiceImpl extends ServiceImpl<ConfDictTypeMapper, ConfDictType> implements ConfDictTypeService {

    private final ConfDictTypeMapper confDictTypeMapper;

    /**
     * 根据字典类型获取字典类型配置
     *
     * @param dictType 字典类型
     * @return 字典类型配置
     */
    @Override
    public List<ConfDictType> listTypeByType(String dictType) {
        QueryChain<ConfDictType> typeQuery = QueryChain.of(confDictTypeMapper)
                .select(CONF_DICT_TYPE.ALL_COLUMNS);
        if (StrUtil.isNotBlank(dictType)) {
            typeQuery.where(CONF_DICT_TYPE.TYPE.eq(dictType));
        }
        List<String> roleList = StpUtil.getRoleList();
        // 非管理员角色只能获取启用的字典类型
        if (!roleList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            typeQuery.and(CONF_DICT_TYPE.STATUS.eq(EnableEnum.ENABLE.getCode()));
        }
        return typeQuery.list();
    }

    /**
     * 根据字典类型获取字典类型配置
     * 字典类型为空时返回所有字典类型配置
     *
     * @param dictTypeList 字典类型列表
     * @return 字典类型配置
     */
    @Override
    public List<ConfDictType> listTypeByTypeList(List<String> dictTypeList) {
        QueryChain<ConfDictType> typeQuery = QueryChain.of(confDictTypeMapper)
                .select(CONF_DICT_TYPE.ALL_COLUMNS);
        if (CollUtil.isNotEmpty(dictTypeList)) {
            typeQuery.where(CONF_DICT_TYPE.TYPE.in(dictTypeList));
        }
        List<String> roleList = StpUtil.getRoleList();
        // 非管理员角色只能获取启用的字典类型
        if (!roleList.contains(RbacConst.ROLE_ADMIN_CODE)) {
            typeQuery.and(CONF_DICT_TYPE.STATUS.eq(EnableEnum.ENABLE.getCode()));
        }
        return typeQuery.list();
    }
}
