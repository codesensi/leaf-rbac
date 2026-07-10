package cn.codesensi.leaf.rbac.framework.cache;

import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 为 sa-token 所有 Redis 缓存 key 统一加上项目自定义前缀。
 * <p>
 * 扩展自 {@link SaTokenDaoForRedisTemplate}，仅覆写与 Redis key 直接交互的
 * 6 个 String 方法及搜索方法，所有 Object / Session 级别方法会通过
 * {@link cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString}
 * 的默认实现自动携带上自定义前缀。
 * <p>
 * <b>前缀示例：</b>{@code leaf-rbac:dev:} <br>
 * <b>最终 key 格式：</b>{@code leaf-rbac:dev:Authorization:login:token:xxx}
 *
 * @author codesensi
 */
@Component
@Primary
public class SaTokenCachePrefixRedisDao extends SaTokenDaoForRedisTemplate {

    /**
     * 懒加载缓存前缀，application name 和 profile 运行时不变化
     */
    private String customPrefix;

    /**
     * 获取自定义前缀。
     * 格式：{@code applicationName:activeProfile:}
     */
    private String getCustomPrefix() {
        if (customPrefix == null) {
            customPrefix = CacheUtil.getBasePrefix();
        }
        return customPrefix;
    }

    /**
     * 为 key 拼接自定义前缀。
     */
    private String prefixKey(String key) {
        return getCustomPrefix() + key;
    }

    // ======================== 覆写 String 级别方法（Object/Session 方法自动继承前缀） ========================

    @Override
    public String get(String key) {
        return super.get(prefixKey(key));
    }

    @Override
    public void set(String key, String value, long timeout) {
        super.set(prefixKey(key), value, timeout);
    }

    @Override
    public void update(String key, String value) {
        super.update(prefixKey(key), value);
    }

    @Override
    public void delete(String key) {
        super.delete(prefixKey(key));
    }

    @Override
    public long getTimeout(String key) {
        return super.getTimeout(prefixKey(key));
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        super.updateTimeout(prefixKey(key), timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        return super.searchData(prefixKey(prefix), keyword, start, size, sortType);
    }
}
