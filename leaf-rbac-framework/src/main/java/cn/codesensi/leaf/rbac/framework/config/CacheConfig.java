package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.properties.AppCacheProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    private final AppCacheProperties appCacheProperties;

    /**
     * 缓存管理器
     *
     * @param lettuceConnectionFactory Redis 连接工厂
     * @return 缓存管理器
     */
    @Primary
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {

        // 1. 获取默认的 Writer（负责底层 Redis 读写）
        RedisCacheWriter defaultWriter = RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);

        // 2. 装饰 Writer：重写 put 方法，在写入时动态生成随机 TTL
        RedisCacheWriter randomTtlWriter = new RedisCacheWriter() {

            /**
             * 写入缓存时，动态计算 TTL
             * @param name cache name must not be {@literal null}.
             * @param key key for the cache entry. Must not be {@literal null}.
             * @param value value stored for the key. Must not be {@literal null}.
             * @param ttl optional expiration time. Can be {@literal null}.
             */
            @Override
            public void put(@NonNull String name, @NonNull byte[] key, @NonNull byte[] value, Duration ttl) {
                // 忽略传入的 ttl（它是默认配置里的固定值），改为动态随机值
                Long baseTtl = appCacheProperties.getBaseTtl();
                Long maxExtra = appCacheProperties.getMaxExtra();
                Duration dynamicTtl = Duration.ofSeconds(baseTtl + ThreadLocalRandom.current().nextLong(maxExtra));
                // 调用父级默认 writer 执行真正的写入
                defaultWriter.put(name, key, value, dynamicTtl);
            }

            // ---------- 以下方法全部委托给 defaultWriter 即可 ----------

            /**
             * Obtain snapshot of the captured statistics. May return a statistics object whose counters are zero if there are no
             * statistics for {@code cacheName}.
             *
             * @param cacheName must not be {@literal null}.
             * @return never {@literal null}.
             */
            @Override
            public CacheStatistics getCacheStatistics(String cacheName) {
                return defaultWriter.getCacheStatistics(cacheName);
            }

            /**
             * Get the binary value representation from Redis stored for the given key.
             *
             * @param name must not be {@literal null}.
             * @param key  must not be {@literal null}.
             * @return {@literal null} if key does not exist.
             * @see #get(String, byte[], Duration)
             */
            @Nullable
            @Override
            public byte[] get(String name, byte[] key) {
                return defaultWriter.get(name, key);
            }

            /**
             * Get the binary value representation from Redis stored for the given key and set the given {@link Duration TTL
             * expiration} for the cache entry.
             *
             * @param name must not be {@literal null}.
             * @param key  must not be {@literal null}.
             * @param ttl  {@link Duration} specifying the {@literal expiration timeout} for the cache entry.
             * @return {@literal null} if key does not exist or has {@literal expired}.
             */
            @Nullable
            @Override
            public byte[] get(String name, byte[] key, @Nullable Duration ttl) {
                return defaultWriter.get(name, key, ttl);
            }

            /**
             * Get the binary value representation from Redis stored for the given key and set the given {@link Duration TTL
             * expiration} for the cache entry, obtaining the value from {@code valueLoader} if necessary.
             * <p>
             * If possible (and configured for locking), implementations should ensure that the loading operation is synchronized
             * so that the specified {@code valueLoader} is only called once in case of concurrent access on the same key.
             *
             * @param name              must not be {@literal null}.
             * @param key               must not be {@literal null}.
             * @param valueLoader       value loader that creates the value if the cache lookup has been not successful.
             * @param ttl               {@link Duration} specifying the {@literal expiration timeout} for the cache entry.
             * @param timeToIdleEnabled {@literal true} to enable Time to Idle when retrieving the value.
             * @since 3.4
             */
            @Override
            public byte[] get(String name, byte[] key, Supplier<byte[]> valueLoader, @Nullable Duration ttl, boolean timeToIdleEnabled) {
                return defaultWriter.get(name, key, valueLoader, ttl, timeToIdleEnabled);
            }

            /**
             * Determines whether the asynchronous {@link #retrieve(String, byte[])} and
             * {@link #retrieve(String, byte[], Duration)} cache operations are supported by the implementation.
             * <p>
             * The main factor for whether the {@literal retrieve} operation can be supported will primarily be determined by the
             * Redis driver in use at runtime.
             * <p>
             * Returns {@literal false} by default. This will have an effect of {@link RedisCache#retrieve(Object)} and
             * {@link RedisCache#retrieve(Object, Supplier)} throwing an {@link UnsupportedOperationException}.
             *
             * @return {@literal true} if asynchronous {@literal retrieve} operations are supported by the implementation.
             * @since 3.2
             */
            @Override
            public boolean supportsAsyncRetrieve() {
                return defaultWriter.supportsAsyncRetrieve();
            }

            /**
             * Asynchronously retrieves the {@link CompletableFuture value} to which the {@link RedisCache} maps the given
             * {@code byte[] key}.
             * <p>
             * This operation is non-blocking.
             *
             * @param name {@link String} with the name of the {@link RedisCache}.
             * @param key  {@code byte[] key} mapped to the {@link CompletableFuture value} in the {@link RedisCache}.
             * @return the {@link CompletableFuture value} to which the {@link RedisCache} maps the given {@code byte[] key}.
             * @see #retrieve(String, byte[], Duration)
             * @since 3.2
             */
            @Override
            public CompletableFuture<byte[]> retrieve(String name, byte[] key) {
                return defaultWriter.retrieve(name, key);
            }

            /**
             * Asynchronously retrieves the {@link CompletableFuture value} to which the {@link RedisCache} maps the given
             * {@code byte[] key} setting the {@link Duration TTL expiration} for the cache entry.
             * <p>
             * This operation is non-blocking.
             *
             * @param name {@link String} with the name of the {@link RedisCache}.
             * @param key  {@code byte[] key} mapped to the {@link CompletableFuture value} in the {@link RedisCache}.
             * @param ttl  {@link Duration} specifying the {@literal expiration timeout} for the cache entry.
             * @return the {@link CompletableFuture value} to which the {@link RedisCache} maps the given {@code byte[] key}.
             * @since 3.2
             */
            @Override
            public CompletableFuture<byte[]> retrieve(String name, byte[] key, @Nullable Duration ttl) {
                return defaultWriter.retrieve(name, key, ttl);
            }


            /**
             * Store the given key/value pair asynchronously to Redis and set the expiration time if defined.
             * <p>
             * This operation is non-blocking.
             *
             * @param name  cache name must not be {@literal null}.
             * @param key   key for the cache entry. Must not be {@literal null}.
             * @param value value stored for the key. Must not be {@literal null}.
             * @param ttl   optional expiration time. Can be {@literal null}.
             * @since 3.2
             */
            @Override
            public CompletableFuture<Void> store(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
                return defaultWriter.store(name, key, value, ttl);
            }

            /**
             * Write the given value to Redis if the key does not already exist.
             *
             * @param name  cache name must not be {@literal null}.
             * @param key   key for the cache entry. Must not be {@literal null}.
             * @param value value stored for the key. Must not be {@literal null}.
             * @param ttl   optional expiration time. Can be {@literal null}.
             * @return {@literal null} if the value has been written, the value stored for the key if it already exists.
             */
            @Nullable
            @Override
            public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
                return defaultWriter.putIfAbsent(name, key, value, ttl);
            }

            /**
             * Remove the given key from Redis.
             *
             * @param name cache name must not be {@literal null}.
             * @param key  key for the cache entry. Must not be {@literal null}.
             */
            @Override
            public void remove(String name, byte[] key) {
                defaultWriter.remove(name, key);
            }

            /**
             * Remove all keys following the given pattern.
             *
             * @param name    cache name must not be {@literal null}.
             * @param pattern pattern for the keys to remove. Must not be {@literal null}.
             */
            @Override
            public void clean(String name, byte[] pattern) {
                defaultWriter.clean(name, pattern);
            }

            /**
             * Reset all statistics counters and gauges for this cache.
             *
             * @param name
             * @since 2.4
             */
            @Override
            public void clearStatistics(String name) {
                defaultWriter.getCacheStatistics(name);
            }

            /**
             * Obtain a {@link RedisCacheWriter} using the given {@link CacheStatisticsCollector} to collect metrics.
             *
             * @param cacheStatisticsCollector must not be {@literal null}.
             * @return new instance of {@link RedisCacheWriter}.
             */
            @Override
            public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
                return defaultWriter.withStatisticsCollector(cacheStatisticsCollector);
            }
        };

        // 缓存前缀：格式为 basePrefix + cacheName + ":" + key（去掉 Spring 默认的 :: 分隔符）
        CacheKeyPrefix cacheKeyPrefix = cacheName -> CacheUtil.getBasePrefix().concat(cacheName).concat(":");
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheKeyPrefix)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(new ObjectMapper())));

        return RedisCacheManager.builder(randomTtlWriter)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
