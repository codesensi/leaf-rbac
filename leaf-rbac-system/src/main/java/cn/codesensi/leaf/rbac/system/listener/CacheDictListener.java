package cn.codesensi.leaf.rbac.system.listener;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.codesensi.leaf.rbac.common.constants.RbacConst;
import cn.codesensi.leaf.rbac.framework.cache.CacheEvictService;
import cn.codesensi.leaf.rbac.framework.event.CacheDictEvent;
import cn.codesensi.leaf.rbac.system.entity.ConfDictType;
import cn.codesensi.leaf.rbac.system.service.ConfDictDataService;
import cn.codesensi.leaf.rbac.system.service.ConfDictTypeService;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfDictTypeTableDef.CONF_DICT_TYPE;

/**
 * 字典缓存预热监听器
 * <p>
 * 在字典数据导入完成后异步预热缓存，
 * 避免数据更新后首次查询的缓存穿透。
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CacheDictListener {

    private final ConfDictDataService confDictDataService;
    private final ConfDictTypeService confDictTypeService;
    private final CacheEvictService cacheEvictService;

    /**
     * @param event 缓存预热事件
     */
    @Async
    @EventListener
    public void cacheDict(CacheDictEvent event) {
        log.info("[cacheDict][收到 CacheDictEvent 事件]，事件来源：{}", event.getCacheSource());
        // 1.清除缓存
        cacheEvictService.clearDictCache();
        log.info("[cacheDict]清除缓存完成");
        // 2.重新加载缓存
        log.info("[cacheDict]开始预热缓存");
        List<ConfDictType> dictTypeList = confDictTypeService.queryChain()
                .select(CONF_DICT_TYPE.ALL_COLUMNS)
                .list();
        List<String> typeList = dictTypeList.stream()
                .map(ConfDictType::getType)
                .toList();
        // 模拟超级管理员登录：查询全量字典数据
        SaTokenContextMockUtil.setMockContext(() -> {
            StpUtil.login(RbacConst.USER_ADMIN_ID, CacheConst.EXPIRE_TIME_1_MINUTE);
            confDictDataService.listDictByTypeList(typeList);
        });
        log.info("[cacheDict]缓存预热完成");
    }
}
