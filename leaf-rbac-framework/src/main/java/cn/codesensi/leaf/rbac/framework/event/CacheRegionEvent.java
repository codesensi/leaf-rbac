package cn.codesensi.leaf.rbac.framework.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 行政区划缓存预热事件
 * <p>
 * 行政区划数据导入完成后发布此事件，用于触发三级行政区划（省/市/县）
 * 缓存的异步预热，避免首次查询时的缓存穿透。
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Getter
@Setter
public class CacheRegionEvent extends ApplicationEvent {

    /**
     * 事件来源
     */
    private String cacheSource;

    @Builder
    public CacheRegionEvent(Object source, String cacheSource) {
        super(source);
        this.cacheSource = cacheSource;
    }
}
