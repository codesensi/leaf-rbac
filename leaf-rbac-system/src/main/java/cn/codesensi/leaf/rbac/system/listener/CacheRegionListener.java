package cn.codesensi.leaf.rbac.system.listener;

import cn.codesensi.leaf.rbac.framework.cache.CacheEvictService;
import cn.codesensi.leaf.rbac.framework.event.CacheRegionEvent;
import cn.codesensi.leaf.rbac.system.service.ConfRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private final CacheEvictService cacheEvictService;

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
        log.info("[cacheRegion][收到 CacheRegionEvent 事件]，事件来源：{}", event.getCacheSource());
        // 1.清除缓存
        cacheEvictService.clearRegionPcodeCache();
        log.info("[cacheRegion]清除缓存完成");
        // 查询行政区划全部层级
        List<Integer> levels = confRegionService.queryChain()
                .select(CONF_REGION.LEVEL)
                .groupBy(CONF_REGION.LEVEL)
                .listAs(Integer.class);
        // 2.重新加载缓存：逐级并行预热省→市→县三级行政区划缓存
        log.info("[cacheRegion]开始预热缓存：逐级并发遍历省、市、区（县）三级行政区划");
        for (Integer level : levels) {
            // 收集当前层级去重后的所有父级编码
            List<String> pcodes = confRegionService.queryChain()
                    .select(CONF_REGION.PCODE)
                    .where(CONF_REGION.LEVEL.eq(level))
                    .listAs(String.class)
                    .stream()
                    .distinct()
                    .toList();

            // 并发调用 listChildrenByCode 写入缓存
            CompletableFuture.allOf(pcodes.stream()
                    .map(pcode -> CompletableFuture.runAsync(() -> confRegionService.listChildrenByCode(pcode)))
                    .toArray(CompletableFuture[]::new)
            ).join();
        }
        log.info("[cacheRegion]缓存预热完成");
    }
}
