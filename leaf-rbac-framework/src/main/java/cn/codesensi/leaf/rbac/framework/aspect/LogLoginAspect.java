package cn.codesensi.leaf.rbac.framework.aspect;

import cn.codesensi.leaf.rbac.common.enums.LoginEventType;
import cn.codesensi.leaf.rbac.common.enums.YesEnum;
import cn.codesensi.leaf.rbac.framework.annotation.LogLogin;
import cn.codesensi.leaf.rbac.framework.annotation.LoginKeyProvider;
import cn.codesensi.leaf.rbac.framework.event.LogLoginEvent;
import cn.codesensi.leaf.rbac.framework.util.Ip2regionUtil;
import cn.codesensi.leaf.rbac.framework.util.IpUtil;
import cn.codesensi.leaf.rbac.framework.util.ServletUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 登录日志 AOP 切面。
 * <p>
 * 通过环绕通知拦截所有标注了 {@link LogLogin} 注解的方法，
 * 自动采集请求元数据（IP、地区、设备、浏览器等）、登录标识、执行耗时及结果状态，
 * 封装为 {@link LogLoginEvent} 事件发布，由
 * {@link cn.codesensi.leaf.rbac.system.listener.LogLoginListener} 异步写入登录日志表。
 * <p>
 * 与 {@link LogOperateAspect} 采用相同的切面模式：
 * 切面负责元数据采集 + 事件发布，监听器负责异步持久化，
 * 将日志记录与业务逻辑完全解耦。
 *
 * @author codesensi
 * @see LogOperateAspect
 * @see LogLoginEvent
 * @since 2026-07-15
 */
@Slf4j
@Aspect
@Component
public class LogLoginAspect {

    /**
     * Jackson ObjectMapper，用于从登录方法的入参中提取用户名及序列化请求参数。
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Spring 应用事件发布器，用于发布 {@link LogLoginEvent} 事件，
     * 由事件监听器异步处理日志持久化，不阻塞登录主流程。
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 构造方法，通过构造器注入 {@link ApplicationEventPublisher}。
     *
     * @param eventPublisher 应用事件发布器
     */
    public LogLoginAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 环绕通知 —— 拦截所有标注了 {@link LogLogin} 的方法，采集登录/登出日志并发布事件。
     * <p>
     * 根据 {@link LogLogin#type()} 区分登录和登出两种处理逻辑：
     * </p>
     * <p>
     * <b>登录（{@link LoginEventType#LOGIN}）：</b>
     * </p>
     * <ol>
     *   <li>从方法入参中反射提取登录用户名（{@code username} 字段），不依赖具体 DTO 类型；</li>
     *   <li>采集请求上下文：客户端 IP、地理区域、操作系统、设备、浏览器；</li>
     *   <li>执行目标方法 {@code proceed()}；</li>
     *   <li>成功 — 从 {@link StpUtil} 获取 userId；</li>
     *   <li>失败 — 捕获 Throwable，记录失败状态和错误消息，继续向上抛出；</li>
     *   <li>{@code finally} 中计算耗时并发布 {@link LogLoginEvent} 事件。</li>
     * </ol>
     * <p>
     * <b>登出（{@link LoginEventType#LOGOUT}）：</b>
     * </p>
     * <ol>
     *   <li>在 {@code proceed()} 前从 {@link StpUtil} 获取 userId
     *       （登出后 token 及会话失效，必须在执行前获取）；</li>
     *   <li>采集请求元数据；</li>
     *   <li>执行目标方法，计算耗时并发布事件。</li>
     * </ol>
     *
     * @param joinPoint AOP 切入点
     * @param logLogin  方法上的 {@link LogLogin} 注解
     * @return 目标方法的返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("@annotation(logLogin)")
    public Object around(ProceedingJoinPoint joinPoint, LogLogin logLogin) throws Throwable {
        long start = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        LoginEventType loginEventType = logLogin.type();
        boolean isLogin = loginEventType == LoginEventType.LOGIN;

        // 构建事件构建器（source 传入方法签名，满足 ApplicationEvent 的构造要求）
        LogLoginEvent.LogLoginEventBuilder builder = LogLoginEvent.builder()
                .source(signature.toLongString())
                .eventType(loginEventType.getCode());

        // 先采集不需依赖业务执行结果的元数据
        collectRequestMetadata(builder);

        if (isLogin) {
            // 登录：从方法入参反射提取用户名（不依赖具体 DTO 类型，无 system 模块依赖）
            extractLoginArgs(joinPoint, builder);
        } else {
            // 登出：在方法执行前获取 userId 和用户名（登出后 token 及会话均失效）
            builder.userId(StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null);
            builder.status(YesEnum.YES.getCode());
        }

        try {
            Object result = joinPoint.proceed();
            // 登录成功：从当前会话获取 userId
            if (isLogin) {
                if (StpUtil.isLogin()) {
                    builder.userId(StpUtil.getLoginIdAsLong());
                }
                builder.status(YesEnum.YES.getCode());
            }
            return result;
        } catch (Throwable t) {
            // 登录失败：记录失败状态，登出不进入此分支
            if (isLogin) {
                builder.status(YesEnum.NO.getCode());
                builder.errorMsg(t.getMessage());
            }
            throw t;
        } finally {
            builder.durationMs(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(builder.build());
        }
    }

    /**
     * 从登录方法入参中提取登录标识及请求参数。
     * <p>
     * 遍历方法参数，找到第一个实现了 {@link cn.codesensi.leaf.rbac.framework.annotation.LoginKeyProvider}
     * 接口的参数，通过 {@code getLoginKey()} 获取登录标识，并使用 Jackson 序列化完整 DTO 作为请求参数。
     * 不依赖任何 system 模块的具体 DTO 类型。
     * </p>
     *
     * @param joinPoint AOP 切入点
     * @param builder   登录日志事件构建器
     */
    private void extractLoginArgs(ProceedingJoinPoint joinPoint,
                                  LogLoginEvent.LogLoginEventBuilder builder) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof LoginKeyProvider provider) {
                String loginKey = provider.getLoginKey();
                builder.loginType(provider.getLoginType());
                builder.loginKey(loginKey);
                builder.username(loginKey);
                builder.params(objectMapper.valueToTree(arg).toString());
                return;
            }
        }
    }

    /**
     * 采集当前请求的元数据并填充到事件构建器中。
     * <p>
     * 采集内容包括：
     * </p>
     * <ul>
     *   <li>客户端 IP 地址及地理区域（通过 {@link Ip2regionUtil} 解析）；</li>
     *   <li>操作系统、设备类型、浏览器（通过 {@link UserAgent} 解析）。</li>
     * </ul>
     * <p>
     * 采集失败时仅打印 warn 日志不阻塞主流程，
     * 与 {@link LogOperateAspect} 的处理方式一致。
     * </p>
     *
     * @param builder 登录日志事件构建器
     */
    private void collectRequestMetadata(LogLoginEvent.LogLoginEventBuilder builder) {
        try {
            String ipAddr = IpUtil.getIpAddr();
            if (StrUtil.isNotBlank(ipAddr)) {
                builder.requestIp(ipAddr);
                builder.requestArea(Ip2regionUtil.search(ipAddr));
            }

            String userAgentStr = ServletUtil.getUserAgent();
            if (StrUtil.isNotBlank(userAgentStr)) {
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
                builder.requestOs(userAgent.getOperatingSystem().getName());
                builder.requestDevice(userAgent.getOperatingSystem().getDeviceType().getName());
                builder.requestBrowser(userAgent.getBrowser().getName());
            }
        } catch (Exception e) {
            log.warn("采集登录日志请求元数据异常", e);
        }
    }

}
