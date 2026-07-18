package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import com.giffing.bucket4j.spring.boot.starter.config.cache.AbstractCacheResolverTemplate;
import com.giffing.bucket4j.spring.boot.starter.config.cache.SyncCacheResolver;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.AbstractProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Bucket4j 限流基础设施配置。
 * <p>
 * <b>背景：</b><br>
 * {@code bucket4j-spring-boot-starter} 内置的
 * {@code LettuceBucket4jConfiguration} 依赖两个条件：
 * <ol>
 *   <li>{@code @ConditionalOnAsynchronousPropertyCondition} — 要求存在
 *       WEBFLUX/GATEWAY 过滤器，但本项目是 Servlet 架构，条件不满足；</li>
 *   <li>{@code @ConditionalOnBean(RedisClient.class)} — 要求存在原生
 *       {@link io.lettuce.core.RedisClient} Bean，但 Spring Boot Data Redis
 *       只暴露 {@code LettuceConnectionFactory}，条件不满足。</li>
 * </ol>
 * 两个前置条件均不成立，导致 starter 无法自动创建
 * {@code AsyncCacheResolver} 或 {@code SyncCacheResolver}，
 * 最终启动时抛出 {@code NoCacheConfiguredException}。
 * </p>
 * <p>
 * <b>解决：</b><br>
 * 此处手动提供两个 Bean 来填补 starter 的缺口：
 * <ul>
 *   <li>{@link #redisClient(RedisProperties)} — 基于已有的
 *       {@code spring.data.redis} 配置创建原生 {@code RedisClient}，
 *       供 Bucket4j Lettuce 集成使用；</li>
 *   <li>{@link #lettuceSyncCacheResolver(RedisClient)} — 实现
 *       {@code SyncCacheResolver}，以同步模式（适配 Servlet Filter）
 *       操作 Redis 中的令牌桶。</li>
 * </ul>
 * </p>
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
public class Bucket4jLettuceConfig {

    /**
     * 为 Bucket4j 创建专用的 {@link RedisClient} Bean。
     * <p>
     * Spring Boot Data Redis 的 {@code RedisAutoConfiguration} 只会自动装配
     * {@code LettuceConnectionFactory} 和 {@code RedisTemplate}，
     * 不会暴露底层 {@code io.lettuce.core.RedisClient}。
     * <br>
     * Bucket4j 的 Lettuce 集成需要 {@code RedisClient}
     * 来创建 {@code LettuceBasedProxyManager}（通过
     * {@link Bucket4jLettuce#casBasedBuilder(RedisClient)}），此处基于已有的
     * {@code spring.data.redis} 配置手动创建原生 Lettuce 客户端。
     * </p>
     * <p>
     * <b>连接隔离：</b><br>
     * 该 {@code RedisClient} 仅供 Bucket4j 限流使用（少量 CAS Lua 脚本操作），
     * 与 Spring Data Redis 管理的 {@code LettuceConnectionFactory} 连接池
     * 相互独立，互不影响。
     * </p>
     *
     * @param redisProperties Spring Boot Redis 配置属性（host、port、password、database）
     * @return 原生 Lettuce Redis 客户端
     */
    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(RedisProperties redisProperties) {
        RedisURI.Builder builder = RedisURI.Builder
                .redis(redisProperties.getHost(), redisProperties.getPort())
                .withDatabase(redisProperties.getDatabase())
                .withPassword(redisProperties.getPassword().toCharArray())
                .withTimeout(redisProperties.getTimeout());
        return RedisClient.create(builder.build());
    }

    /**
     * 基于 Lettuce 的同步 {@code SyncCacheResolver} 实现。
     * <p>
     * 逻辑与 starter 内置的 {@code LettuceCacheResolver} 一致——
     * 通过 {@code LettuceBasedProxyManager} 在 Redis 中执行 CAS Lua 脚本，
     * 实现分布式令牌桶的原子操作。
     * <br>
     * 唯一区别：{@code #isAsync()} 返回 {@code false}，
     * 使 Bucket4j 走同步调用路径，适配本项目的 Servlet Filter 模式。
     * </p>
     *
     * @param redisClient 原生 Lettuce Redis 客户端
     * @return 同步模式的 Lettuce 缓存解析器
     */
    @Bean
    @ConditionalOnBean(RedisClient.class)
    @ConditionalOnMissingBean(SyncCacheResolver.class)
    public SyncCacheResolver lettuceSyncCacheResolver(RedisClient redisClient) {
        return new SyncLettuceCacheResolver(redisClient);
    }

    /**
     * 同步 Lettuce 缓存解析器。
     * <p>
     * 继承 {@link AbstractCacheResolverTemplate} 复用 starter 的桶构建逻辑
     * （令牌估算、消耗、指标监听等），仅需实现三个抽象方法即可对接 Bucket4j 的 Lettuce 后端。
     * </p>
     */
    static class SyncLettuceCacheResolver extends AbstractCacheResolverTemplate<byte[]> implements SyncCacheResolver {

        /**
         * 限流缓存 key 子前缀，与项目中其他 Redis key 格式统一。
         * 完整格式：{@code leaf-rbac:dev:rate-limit:192.168.1.100}
         */
        private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:";

        private final RedisClient redisClient;

        SyncLettuceCacheResolver(RedisClient redisClient) {
            this.redisClient = redisClient;
        }

        /**
         * 同步模式 —— 适配 Servlet Filter。
         */
        @Override
        public boolean isAsync() {
            return false;
        }

        /**
         * 将限流 key 转为 Lettuce 原生字节数组编码，并统一加上项目缓存前缀。
         * <p>
         * 例如 SpEL {@code cache-key: "getRemoteAddr()"} 计算出 {@code 192.168.1.100}，
         * 实际写入 Redis 的 key 为 {@code leaf-rbac:dev:rate-limit:192.168.1.100}。
         * </p>
         */
        @Override
        public byte[] castStringToCacheKey(String key) {
            String prefixedKey = CacheUtil.getBasePrefix() + RATE_LIMIT_KEY_PREFIX + key;
            return prefixedKey.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * 创建基于 Lettuce Redis 的令牌桶代理管理器。
         * <p>
         * 使用 {@link Bucket4jLettuce#casBasedBuilder(RedisClient)} 新 API
         * 替代已弃用的 {@code LettuceBasedProxyManager.builderFor()}。
         * 底层通过 Redis CAS Lua 脚本实现多实例无竞态的令牌消费。
         * </p>
         *
         * @param cacheName 缓存名称（对应 YAML 中的 {@code cache-name}）
         * @return Lettuce 代理管理器，每次桶操作通过 Redis Lua 脚本原子执行
         */
        @Override
        public AbstractProxyManager<byte[]> getProxyManager(String cacheName) {
            return Bucket4jLettuce.casBasedBuilder(redisClient)
                    // Bucket4j 默认：桶装满后 redis 缓存永不过期
                    // .expirationAfterWrite(ExpirationAfterWriteStrategy.none())
                    // Redis 中令牌桶 key 的 TTL 淘汰策略：「桶装满之后，再多存活 30 秒再过期」
                    .expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(30)))
                    .build();
        }
    }
}
