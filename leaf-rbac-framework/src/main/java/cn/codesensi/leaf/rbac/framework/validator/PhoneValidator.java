package cn.codesensi.leaf.rbac.framework.validator;

import cn.codesensi.leaf.rbac.framework.annotation.Phone;
import cn.hutool.core.lang.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号校验器
 *
 * @author codesensi
 * @since 1.0
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

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
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 不校验必填属性
        if (value == null || value.isBlank()) {
            return true;
        }
        return Validator.isMobile(value);
    }
}
