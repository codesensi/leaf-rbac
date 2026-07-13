package cn.codesensi.leaf.rbac.system.listener;

import cn.codesensi.leaf.rbac.framework.event.CacheRegionEvent;
import cn.codesensi.leaf.rbac.system.entity.ConfRegion;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.codesensi.leaf.rbac.system.entity.table.ConfRegionTableDef.CONF_REGION;

/**
 * 行政区划缓存预热监听器
 * <p>
 * 在行政区划数据导入完成后异步预热省、市、县三级缓存，
 * 避免数据更新后首次查询的缓存穿透。
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CacheRegionListener {

    private final ConfRegionService confRegionService;

    /**
     * 逐级遍历省（level=1）、市（level=2）、县（level=3），
     * 通过调用标注了 {@code @Cacheable} 的 {@code listChildrenByCode}
     * 方法写入 Redis 缓存。
     *
     * @param event 缓存预热事件
     */
    @Async
    @EventListener
    public void cacheRegion(CacheRegionEvent event) {
        log.info("[cacheRegion][收到 CacheRegionEvent 事件]，导入数据总量：{}", event.getCacheSize());
        for (int i = 1; i <= 3; i++) {
            List<ConfRegion> confRegions = confRegionService.queryChain()
                    .select(CONF_REGION.PCODE)
                    .where(CONF_REGION.LEVEL.eq(i))
                    .list();
            for (ConfRegion confRegion : confRegions) {
                confRegionService.listChildrenByCode(confRegion.getPcode());
            }
        }
    }

}
