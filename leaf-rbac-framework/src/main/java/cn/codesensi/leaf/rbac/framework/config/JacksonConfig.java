package cn.codesensi.leaf.rbac.framework.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局序列化配置 —— 统一日期时间格式。
 * <p>
 * {@link LocalDateTime} 默认序列化为 ISO-8601 格式（如 {@code 2026-07-07T21:07:32}），
 * 通过本配置将其格式化为 {@code yyyy-MM-dd HH:mm:ss}，与 {@code spring.jackson.date-format}
 * 配置保持一致（后者仅对 {@link java.util.Date} 生效）。
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
public class JacksonConfig {

    /**
     * 日期时间格式器：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 自定义 Jackson ObjectMapper，对 {@link LocalDateTime} 类型注册全局序列化/反序列化格式。
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
    }

}
