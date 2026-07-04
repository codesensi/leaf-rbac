package cn.codesensi.leaf.rbac.bootstrap.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

import java.io.File;

/**
 * 自定义 Spring 条件判断：当锁文件不存在时，返回 {@code true} 以启用初始化配置。
 * <p>
 * 用于 {@link org.springframework.context.annotation.Conditional @Conditional} 注解，
 * 控制 {@link cn.codesensi.leaf.rbac.bootstrap.initializer.DatabaseInitializer} 等 Bean 是否创建。
 * 锁文件路径通过配置项 {@code app.lock-file} 指定，默认为 {@code ./data/app.lock}。
 *
 * @see org.springframework.context.annotation.Condition
 * @see org.springframework.context.annotation.Conditional
 */
@Slf4j
public class LockFileMissingCondition implements Condition {

    /**
     * 判断锁文件是否不存在。
     * <p>
     * 从 {@link Environment} 中读取锁文件路径，若该路径对应的文件在文件系统中不存在，
     * 则条件匹配（返回 {@code true}），触发相关 Bean 的初始化流程。
     *
     * @param context  当前应用上下文，用于获取 Environment 等运行时信息
     * @param metadata 标注了 {@code @Conditional} 的目标类/方法的注解元数据
     * @return 锁文件不存在时返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String lockFilePath = environment.getProperty("app.lock-file", "./data/app.lock");
        File lockFile = new File(lockFilePath);
        boolean exists = lockFile.exists();
        return !exists;
    }
}
