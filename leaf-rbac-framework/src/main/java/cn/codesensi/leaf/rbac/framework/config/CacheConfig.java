package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.properties.AppCacheProperties;
import cn.codesensi.leaf.rbac.common.util.CacheUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 缓存配置。
 * <p>
 * <b>序列化策略</b>（参考 sa-token {@code SaJsonTemplateForJackson} 的类型分派思路）：
 * <ul>
 *   <li>对象 / {@link java.util.Map}：委托 {@link GenericJackson2JsonRedisSerializer}
 *       默认配置，利用 {@code As.PROPERTY} 在 JSON 对象上嵌入 {@code @class}</li>
 *   <li>{@link Collection}（{@link List} / {@link Set}）：自定义 cleanMapper 以
 *       {@code ["java.util.List",[...]]} 二维数组格式存储，规避 {@code As.PROPERTY}
 *       无法在 JSON 数组上嵌入类型信息的问题</li>
 * </ul>
 * <p>
 * <b>TTL 策略</b>：通过装饰 {@link RedisCacheWriter} 的 {@code put} 方法，
 * 在每次写入时用 {@code baseTtl + random(maxExtra)} 生成随机过期时间，
 * 避免缓存雪崩。
 *
 * @author codesensi
 * @see CacheValueSerializer
 */
@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    private final AppCacheProperties appCacheProperties;

    /**
     * 构建缓存管理器。
     * <p>
     * 使用非锁定的 {@link RedisCacheWriter}，并通过装饰模式在每次写入时
     * 注入随机 TTL，同时自定义缓存 key 前缀和值序列化策略。
     *
     * @param lettuceConnectionFactory Redis 连接工厂
     * @return 全局唯一的 {@link CacheManager} 实例
     */
    @Primary
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {

        // 底层 Redis 读写
        RedisCacheWriter defaultWriter = RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);

        // 装饰 Writer：在写入时用随机 TTL 替换固定 TTL，防止缓存雪崩
        RedisCacheWriter randomTtlWriter = new RedisCacheWriter() {

            /**
             * 写入缓存键值对，自动生成随机 TTL 以分散过期时间。
             *
             * @param name  缓存名称
             * @param key   已序列化的缓存键（二进制）
             * @param value 已序列化的缓存值（二进制）
             * @param ttl   原始 TTL，将被忽略，替换为随机值
             */
            @Override
            public void put(@NonNull String name, @NonNull byte[] key, @NonNull byte[] value, Duration ttl) {
                long baseTtl = appCacheProperties.getBaseTtl();
                long maxExtra = appCacheProperties.getMaxExtra();
                Duration dynamicTtl = Duration.ofSeconds(baseTtl + ThreadLocalRandom.current().nextLong(maxExtra));
                defaultWriter.put(name, key, value, dynamicTtl);
            }

            // ======================== 以下全部委托给 defaultWriter ========================

            /**
             * 获取缓存统计信息快照。
             *
             * @param cacheName 缓存名称
             * @return 统计信息，如无统计则计数全为 0
             */
            @Override
            public CacheStatistics getCacheStatistics(String cacheName) {
                return defaultWriter.getCacheStatistics(cacheName);
            }

            /**
             * 根据 key 从 Redis 读取二进制缓存值。
             *
             * @param name 缓存名称
             * @param key  二进制缓存键
             * @return 二进制缓存值，key 不存在时返回 {@code null}
             */
            @Nullable
            @Override
            public byte[] get(String name, byte[] key) {
                return defaultWriter.get(name, key);
            }

            /**
             * 异步读取并附带 TTL 续期。
             * <p>
             * 该操作非阻塞，依赖底层 Redis 驱动对异步操作的支持。
             *
             * @param name             缓存名称
             * @param key              二进制缓存键
             * @param ttl              读取时设置的 TTL 过期时间，可为 {@code null}
             * @return 异步结果，包含读取到的二进制值或 {@code null}
             * @since 3.2
             */
            @Override
            public CompletableFuture<byte[]> retrieve(String name, byte[] key, @Nullable Duration ttl) {
                return defaultWriter.retrieve(name, key, ttl);
            }

            /**
             * 异步写入键值对并设置过期时间。
             * <p>
             * 该操作非阻塞。
             *
             * @param name  缓存名称
             * @param key   二进制缓存键
             * @param value 二进制缓存值
             * @param ttl   可选的过期时间，可为 {@code null}
             * @return 异步占位，写入完成后 void
             * @since 3.2
             */
            @Override
            public CompletableFuture<Void> store(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
                return defaultWriter.store(name, key, value, ttl);
            }

            /**
             * 仅在 key 不存在时写入（原子操作）。
             *
             * @param name  缓存名称
             * @param key   二进制缓存键
             * @param value 二进制缓存值
             * @param ttl   可选的过期时间，可为 {@code null}
             * @return key 已存在时返回旧值，写入成功时返回 {@code null}
             */
            @Nullable
            @Override
            public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
                return defaultWriter.putIfAbsent(name, key, value, ttl);
            }

            /**
             * 从 Redis 中删除指定 key。
             *
             * @param name 缓存名称
             * @param key  二进制缓存键
             */
            @Override
            public void remove(String name, byte[] key) {
                defaultWriter.remove(name, key);
            }

            /**
             * 按模式清理匹配的 Redis key。
             *
             * @param name    缓存名称
             * @param pattern 匹配模式（二进制）
             */
            @Override
            public void clean(String name, byte[] pattern) {
                defaultWriter.clean(name, pattern);
            }

            /**
             * 重置缓存统计计数。
             *
             * @param name 缓存名称
             * @since 2.4
             */
            @Override
            public void clearStatistics(String name) {
                defaultWriter.clearStatistics(name);
            }

            /**
             * 附加统计收集器后返回新的 Writer 实例。
             *
             * @param cacheStatisticsCollector 统计收集器
             * @return 附加了统计功能的新 Writer
             */
            @Override
            public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
                return defaultWriter.withStatisticsCollector(cacheStatisticsCollector);
            }
        };

        // 缓存 key 前缀：basePrefix + cacheName + ":"
        CacheKeyPrefix cacheKeyPrefix = cacheName -> CacheUtil.getBasePrefix().concat(cacheName).concat(":");
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheKeyPrefix)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new CacheValueSerializer()));

        return RedisCacheManager.builder(randomTtlWriter)
                .cacheDefaults(defaultConfig)
                .build();
    }

    /**
     * 分类型的缓存值序列化器。
     * <p>
     * <b>类型分派规则：</b>
     * <table>
     *   <caption>序列化格式</caption>
     *   <tr><th>Java 类型</th><th>序列化器</th><th>输出格式</th></tr>
     *   <tr><td>对象</td><td>{@link GenericJackson2JsonRedisSerializer}</td>
     *       <td>{@code {"@class":"...","field":"val"}}</td></tr>
     *   <tr><td>{@link java.util.Map}</td><td>{@code GenericJackson2JsonRedisSerializer}</td>
     *       <td>{@code {"@class":"...","key":"val"}}</td></tr>
     *   <tr><td>{@link List} / {@link Set}</td><td>cleanMapper</td>
     *       <td>{@code ["java.util.List",["item"]]}</td></tr>
     * </table>
     * <p>
     * <b>为什么集合类型需要单独处理：</b>
     * {@code GenericJackson2JsonRedisSerializer} 内部使用 Jackson
     * {@code DefaultTyping.NON_FINAL + As.PROPERTY}，对于 JSON 对象能正常嵌入
     * {@code @class} 属性，但 JSON 数组无法添加属性，导致类型信息丢失，
     * 反序列化时无法正确还原类型。
     */
    static class CacheValueSerializer implements RedisSerializer<Object> {

        /**
         * 对象 / Map 类型：利用 As.PROPERTY 在 JSON 对象上嵌入 @class
         */
        private final GenericJackson2JsonRedisSerializer objectSerializer = new GenericJackson2JsonRedisSerializer();

        /**
         * 集合类型：纯 ObjectMapper，手工构造 {@code ["类型名", 值]} 二维数组
         */
        private final ObjectMapper cleanMapper = new ObjectMapper();

        /**
         * 序列化：按运行时类型分派到不同序列化策略。
         * <p>
         * {@code null} 值序列化为空字节数组，与 Spring 的
         * {@link org.springframework.cache.support.NullValue} 机制协作。
         *
         * @param value 待序列化的缓存值
         * @return JSON 字节数组
         * @throws SerializationException 序列化失败时抛出
         */
        @Override
        public byte[] serialize(Object value) throws SerializationException {
            if (value == null) {
                return new byte[0];
            }
            if (value instanceof Collection) {
                return serializeAsWrapperArray(value);
            }
            return objectSerializer.serialize(value);
        }

        /**
         * 将集合类型序列化为 {@code ["类名",[...]]} 二维数组。
         * <p>
         * 第一元素为归一化后的类型全限定名，第二元素为集合自身的 JSON 表示。
         *
         * @param value 集合实例
         * @return 二维数组格式的 JSON 字节
         */
        private byte[] serializeAsWrapperArray(Object value) {
            try {
                List<Object> wrapper = new ArrayList<>();
                wrapper.add(normalizeCollectionType(value));
                wrapper.add(value);
                return cleanMapper.writeValueAsBytes(wrapper);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Failed to serialize " + value.getClass(), e);
            }
        }

        /**
         * 反序列化：根据 JSON 首节点类型分派。
         * <p>
         * JSON 数组（{@code [} 开头）→ 二维数组格式，提取类型名 + 数据还原；
         * JSON 对象（{@code {} 开头）→ 委托 {@link GenericJackson2JsonRedisSerializer}
         * 通过 {@code @class} 属性还原类型。
         *
         * @param bytes JSON 字节数组
         * @return 反序列化后的 Java 对象
         * @throws SerializationException 反序列化失败时抛出
         */
        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            if (ObjUtil.isEmpty(bytes)) {
                return null;
            }
            JsonNode node;
            try {
                node = cleanMapper.readTree(bytes);
            } catch (IOException e) {
                throw new SerializationException("Failed to deserialize", e);
            }
            if (node.isArray()) {
                return deserializeWrapperArray(bytes);
            }
            return objectSerializer.deserialize(bytes);
        }

        /**
         * 解析二维数组格式 {@code ["类名", 数据]}，还原为原始类型。
         * <p>
         * 流程：读取外层数组 → 提取第一元素作为类型名并反射获取类对象 →
         * 对第二元素（数据）执行 {@link ObjectMapper#convertValue} 转换为目标类型。
         *
         * @param bytes 二维数组格式的 JSON 字节
         * @return 还原后的 Java 集合实例
         */
        private Object deserializeWrapperArray(byte[] bytes) {
            try {
                List<Object> wrapper = cleanMapper.readValue(bytes,
                        cleanMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                return cleanMapper.convertValue(wrapper.get(1), Class.forName((String) wrapper.get(0)));
            } catch (Exception e) {
                throw new SerializationException("Failed to deserialize wrapper array", e);
            }
        }

        /**
         * 将集合运行时类型归一化为顶层接口。
         * <p>
         * 存储接口名而非具体实现类，避免 {@code ImmutableCollections$ListN}、
         * {@code java.util.Arrays$ArrayList} 等不可实例化的私有类型写入后
         * 反序列化失败。
         *
         * @param value 集合实例
         * @return 归一化后的顶层接口全限定名
         */
        private String normalizeCollectionType(Object value) {
            return switch (value) {
                case List<?> ignored -> List.class.getName();
                case Set<?> ignored -> Set.class.getName();
                default -> value.getClass().getName();
            };
        }
    }
}
