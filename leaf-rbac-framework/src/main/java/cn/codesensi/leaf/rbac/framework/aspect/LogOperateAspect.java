package cn.codesensi.leaf.rbac.framework.aspect;

import cn.codesensi.leaf.rbac.common.enums.SuccessEnum;
import cn.codesensi.leaf.rbac.framework.annotation.LogOperate;
import cn.codesensi.leaf.rbac.framework.event.LogOperateEvent;
import cn.codesensi.leaf.rbac.framework.util.Ip2regionUtil;
import cn.codesensi.leaf.rbac.framework.util.IpUtil;
import cn.codesensi.leaf.rbac.framework.util.ServletUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 操作日志 AOP 切面。
 * <p>
 * 通过环绕通知拦截所有标注了 {@link LogOperate} 注解的 Controller 方法，
 * 自动采集请求信息（IP、地区、设备、浏览器等）、方法参数、响应结果及执行耗时，
 * 封装为 {@link LogOperateEvent} 事件发布，由监听器异步写入操作日志表。
 * <p>
 * 支持按配置记录或忽略指定字段，避免敏感信息落入日志。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class LogOperateAspect {

    /**
     * Spring 方法参数名解析器。
     * <p>
     * 用于从字节码 debug 信息或 {@code -parameters} 编译选项中获取方法形参的真实名称
     * （如 {@code id}、{@code user}），而非默认的 {@code arg0}、{@code arg1}。
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Jackson ObjectMapper，用于将响应结果树化为 {@link JsonNode}，
     * 以便递归剔除 {@code ignoreFields} 中指定的敏感字段。
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 参数序列化专用的 ObjectMapper —— 自动跳过值为 null 的字段，
     * 避免入参中大量 null 字段污染操作日志。
     */
    private static final ObjectMapper paramsObjectMapper = new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Spring 应用事件发布器，用于发布 {@link LogOperateEvent} 事件，
     * 由事件监听器异步处理日志持久化，不阻塞主流程。
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 构造方法，通过构造器注入 {@link ApplicationEventPublisher}。
     *
     * @param eventPublisher 应用事件发布器
     */
    public LogOperateAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 环绕通知 —— 拦截所有标注了 {@link LogOperate} 的方法，采集操作日志并发布事件。
     * <p>
     * 处理流程：
     * <ol>
     *   <li>填充基础信息：模块、类型、描述、方法全限定名；</li>
     *   <li>采集请求上下文：请求 URL、客户端 IP、地理区域、系统、设备、浏览器；</li>
     *   <li>记录方法参数（可选）：按配置过滤敏感字段后序列化为 JSON；</li>
     *   <li>执行目标方法 {@code proceed()}；</li>
     *   <li>记录响应结果（可选）：按配置剔除敏感字段后序列化为 JSON；</li>
     *   <li>捕获异常时记录失败状态和错误消息，继续向上抛出；</li>
     *   <li>{@code finally} 中计算耗时并发布 {@link LogOperateEvent} 事件。</li>
     * </ol>
     *
     * @param joinPoint  AOP 切入点
     * @param logOperate 方法上的 {@link LogOperate} 注解
     * @return 目标方法的返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("@annotation(logOperate)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperate logOperate) throws Throwable {
        long start = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        // 构建事件构建器（source 传入方法签名，满足 ApplicationEvent 的构造要求）
        LogOperateEvent.LogOperateEventBuilder builder = LogOperateEvent.builder()
                .source(signature.toLongString())
                .module(logOperate.module())
                .type(logOperate.type().getCode())
                .descr(logOperate.desc())
                .method(signature.toLongString());

        ServletRequestAttributes attributes = ServletUtil.getRequestAttributes();
        if (ObjUtil.isNotNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            StringBuffer requestUrl = request.getRequestURL();
            builder.url(requestUrl.toString());
        }

        String ipAddr = IpUtil.getIpAddr();
        builder.ip(ipAddr);
        builder.region(Ip2regionUtil.search(ipAddr));
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getUserAgent());
        builder.os(userAgent.getOperatingSystem().getName());
        builder.device(userAgent.getOperatingSystem().getDeviceType().getName());
        builder.browser(userAgent.getBrowser().getName());

        Set<String> ignoreSet = ObjUtil.isNotEmpty(logOperate.ignoreFields())
                ? new HashSet<>(Arrays.asList(logOperate.ignoreFields()))
                : Collections.emptySet();
        if (logOperate.recordParams()) {
            ArgumentContext argumentContext = argumentResolve(joinPoint);
            List<ArgumentDetail> argumentDetails = argumentContext.getDetails();
            // 将参数列表序列化为 JsonNode，对 value 中的字段进行脱敏
            ArrayNode paramsArray = objectMapper.createArrayNode();
            for (ArgumentDetail detail : argumentDetails) {
                ObjectNode paramNode = objectMapper.createObjectNode();
                paramNode.put("index", detail.getIndex());
                paramNode.put("name", detail.getName());
                // 对 value 做字段级脱敏（跳过 null 字段）
                if (detail.getValue() != null) {
                    JsonNode valueNode = paramsObjectMapper.valueToTree(detail.getValue());
                    JsonNode filteredValue = removeFields(valueNode, ignoreSet);
                    paramNode.set("value", filteredValue);
                }
                paramsArray.add(paramNode);
            }
            // 无入参时不记录请求参数
            if (!paramsArray.isEmpty()) {
                builder.params(paramsArray.toString());
            }
        }
        // 操作人
        if (StpUtil.isLogin()) {
            builder.userId(StpUtil.getLoginIdAsLong());
        }

        try {
            Object result = joinPoint.proceed();
            builder.status(SuccessEnum.SUCCESS.getCode());
            // 记录结果
            if (logOperate.recordResult()) {
                // 4. 将结果转换为 JsonNode 并剔除字段
                JsonNode rootNode = objectMapper.valueToTree(result);
                JsonNode filteredNode = removeFields(rootNode, ignoreSet);
                builder.result(filteredNode.toString());
            }
            return result;
        } catch (Throwable t) {
            builder.status(SuccessEnum.FAIL.getCode());
            builder.errorMsg(t.getMessage());
            throw t;
        } finally {
            builder.durationMs(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(builder.build());
        }
    }

    /**
     * 解析切入点方法的参数上下文。
     * <p>
     * 将 {@link ProceedingJoinPoint} 解析为结构化的 {@link ArgumentContext}，
     * 包含目标方法反射对象、所有参数的名称/值/注解列表、以及原始 joinPoint。
     *
     * @param joinPoint AOP 切入点
     * @return 参数上下文，包含方法、参数详情列表、切入点本身
     */
    private ArgumentContext argumentResolve(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] rawArgs = joinPoint.getArgs();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        List<ArgumentDetail> details = new ArrayList<>();
        for (int i = 0; i < rawArgs.length; i++) {
            details.add(new ArgumentDetail(
                    i,
                    paramNames != null ? paramNames[i] : "arg" + i,
                    rawArgs[i]
            ));
        }
        return new ArgumentContext(method, details, joinPoint);
    }

    /**
     * 递归剔除 JSON 树中的敏感字段。
     * <p>
     * 遍历 {@link JsonNode} 树结构，将对象节点中名称命中 {@code ignoreSet} 的字段移除。
     * 支持嵌套对象和数组结构的递归处理。
     *
     * @param node      待处理的 JSON 树节点
     * @param ignoreSet 需要剔除的字段名集合
     * @return 处理后的 JSON 树节点
     */
    private JsonNode removeFields(JsonNode node, Set<String> ignoreSet) {
        if (node == null || node.isNull()) {
            return node;
        }
        // 处理数组：递归处理每个元素
        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                arrayNode.set(i, removeFields(arrayNode.get(i), ignoreSet));
            }
            return arrayNode;
        }
        // 处理对象：移除命中 ignoreSet 的顶层字段
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            Set<String> keysToRemove = new HashSet<>();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (ignoreSet.contains(fieldName)) {
                    keysToRemove.add(fieldName);
                }
            }
            keysToRemove.forEach(objectNode::remove);
            return objectNode;
        }
        // 基础类型（字符串、数字、布尔等）无需处理，直接返回
        return node;
    }

    /**
     * 参数详情 —— 记录单个方法参数的索引、名称、值及参数注解。
     * <p>
     * 将 AOP 拦截到的原始 {@code Object[] args}、参数名数组、参数注解数组
     * 按索引对齐，封装为结构化对象，便于后续按需过滤和序列化。
     */
    @Data
    static class ArgumentDetail {
        /**
         * 参数在方法形参列表中的位置索引，从 0 开始
         */
        private int index;
        /**
         * 参数名称（如 id、user、page），由 {@link ParameterNameDiscoverer} 解析得到
         */
        private String name;
        /**
         * 参数的实际值，即方法调用时传入的对象
         */
        private Object value;

        public ArgumentDetail(int index, String name, Object value) {
            this.index = index;
            this.name = name;
            this.value = value;
        }
    }

    /**
     * 参数上下文 —— 封装被调用的目标方法、所有参数的详情列表及原始切入点。
     * <p>
     * 由 {@link #argumentResolve(ProceedingJoinPoint)} 返回，
     * 调用方可通过此对象获取目标方法的完整调用信息。
     */
    @Data
    static class ArgumentContext {
        /**
         * 被调用的目标方法反射对象
         */
        private Method method;
        /**
         * 所有参数的详情列表（已按索引对齐）
         */
        private List<ArgumentDetail> details;
        /**
         * 原始 AOP 切入点，可继续执行 proceed()
         */
        private ProceedingJoinPoint joinPoint;

        public ArgumentContext(Method method, List<ArgumentDetail> details, ProceedingJoinPoint joinPoint) {
            this.method = method;
            this.details = details;
            this.joinPoint = joinPoint;
        }
    }

}
