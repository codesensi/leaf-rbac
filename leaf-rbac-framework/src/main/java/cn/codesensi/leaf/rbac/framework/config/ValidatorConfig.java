package cn.codesensi.leaf.rbac.framework.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

/**
 * Hibernate Validator 参数校验配置。
 * <p>
 * 自定义 {@link Validator} Bean，替换 Spring Boot 默认的校验器，
 * 主要定制以下行为：
 * <ul>
 *   <li><b>快速失败模式</b>（{@code failFast(true)}）— 校验参数时一旦发现第一个非法字段立即返回，
 *       不再继续校验剩余字段，避免一条请求返回多条校验异常信息，提升接口响应效率；</li>
 *   <li><b>Spring 依赖注入支持</b>（{@link SpringConstraintValidatorFactory}）— 使自定义的
 *       {@link jakarta.validation.ConstraintValidator} 实现类中可以正常注入 Spring Bean，
 *       解决默认校验器工厂无法感知 Spring 容器的问题。</li>
 * </ul>
 * <p>
 * 对应 Maven 依赖：{@code org.springframework.boot:spring-boot-starter-validation}（内嵌 Hibernate Validator）。
 *
 * @author codesensi
 * @since 1.0
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator(AutowireCapableBeanFactory springFactory) {
        try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(true)
                // 解决 SpringBoot 依赖注入问题
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(springFactory))
                .buildValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }
}
