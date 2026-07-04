package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池配置实体类
 */
@Data
@Component
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize;

    /**
     * 缓冲队列大小
     */
    private Integer queueCapacity;

    /**
     * 线程的最大空闲秒数
     */
    private Integer keepAliveSeconds;

    /**
     * 是否允许核心线程超时
     */
    private Boolean allowCoreThreadTimeout;

    /**
     * 是否等待剩余任务完成后才关闭应用
     */
    private Boolean waitForTasksToCompleteOnShutdown;

    /**
     * 等待剩余任务完成的最大秒数
     */
    private Integer awaitTerminationSeconds;

    /**
     * 拒绝策略
     */
    private String rejectedExecutionHandler;
}
