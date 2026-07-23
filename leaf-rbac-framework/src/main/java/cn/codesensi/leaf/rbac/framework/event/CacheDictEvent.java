package cn.codesensi.leaf.rbac.framework.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 字典缓存预热事件
 * <p>
 * 字典数据导入完成后发布此事件，用于触发字典缓存的异步预热，
 * 缓存的异步预热，避免首次查询时的缓存穿透。
 *
 * @author codesensi
 * @since 2026-07-13
 */
@Getter
@Setter
public class CacheDictEvent extends ApplicationEvent {

    /**
     * 事件来源
     */
    private String cacheSource;

    @Builder
    public CacheDictEvent(Object source, String cacheSource) {
        super(source);
        this.cacheSource = cacheSource;
    }
}
