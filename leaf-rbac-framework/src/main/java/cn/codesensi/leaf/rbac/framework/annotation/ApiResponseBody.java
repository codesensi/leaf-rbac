package cn.codesensi.leaf.rbac.framework.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * 统一 API 响应体包装注解。
 * <p>
 * 标注在 Controller 类或方法上，配合 {@link cn.codesensi.leaf.rbac.framework.advice.ApiResponseBodyAdvice}
 * 使用，用于自动将接口返回值包装为统一响应对象 {@link cn.codesensi.leaf.rbac.common.core.Result}
 * <p>
 * <b>使用方式：</b>
 * <ul>
 *   <li><b>类级别</b> — 标注在 Controller 类上，对该类下所有接口方法生效；
 *   <li><b>方法级别</b> — 标注在具体方法上，仅对该方法生效，优先级高于类级别。
 * </ul>
 * <p>
 * <b>设计意图：</b><br>
 * Controller 层接口只需返回业务数据本身（如 {@code User}、{@code List&lt;Role&gt;}），
 * 由 {@code ApiResponseBodyAdvice} 自动封装为 {@code Result<T>} 格式，
 * 避免在每个接口方法中重复编写 {@code return Result.success(data)}。
 *
 * @author codesensi
 * @see cn.codesensi.leaf.rbac.framework.advice.ApiResponseBodyAdvice
 * @see cn.codesensi.leaf.rbac.common.core.Result
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ApiResponseBody {
}
