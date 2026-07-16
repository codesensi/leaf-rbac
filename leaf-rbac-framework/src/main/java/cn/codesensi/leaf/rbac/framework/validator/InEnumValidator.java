package cn.codesensi.leaf.rbac.framework.validator;

import cn.codesensi.leaf.rbac.common.enums.BaseEnum;
import cn.codesensi.leaf.rbac.framework.annotation.InEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 枚举值校验器
 *
 * @author codesensi
 * @since 1.0
 */
public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    private Set<String> validValues;

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(Object, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(InEnum constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        // 检查枚举是否实现了 BaseEnum 接口
        if (BaseEnum.class.isAssignableFrom(enumClass)) {
            // 直接获取枚举常量数组，转换为 BaseEnum 类型
            Enum<?>[] constants = enumClass.getEnumConstants();
            validValues = Arrays.stream(constants)
                    .map(e -> String.valueOf(((BaseEnum<?>) e).getCode()))
                    .collect(Collectors.toSet());
        } else {
            // 普通枚举使用常量名
            validValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 不校验必填属性
        if (value == null || value.toString().isBlank()) {
            return true;
        }
        return validValues.contains(value.toString());
    }
}
