package cn.codesensi.leaf.rbac.framework.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class LogLoginEvent extends ApplicationEvent {

    /**
     * 登录方式（如：账号密码、手机号）
     */
    private Integer loginType;

    /**
     * 事件类型（如：登录、登出）
     */
    private Integer eventType;

    /**
     * 登录标识（账号/手机号）
     */
    private String loginKey;

    /**
     * 登录人ID
     */
    private Long userId;

    /**
     * 登录人账号
     */
    private String username;

    /**
     * 状态（1=成功，0=失败）
     */
    private Integer status;

    /**
     * 错误信息（当 status=0 时记录异常消息）
     */
    private String errorMsg;

    /**
     * 请求来源IP地址
     */
    private String requestIp;

    /**
     * 请求地区
     */
    private String requestArea;

    /**
     * 请求系统
     */
    private String requestOs;

    /**
     * 请求设备
     */
    private String requestDevice;

    /**
     * 请求浏览器
     */
    private String requestBrowser;

    /**
     * 请求执行耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 请求参数（JSON字符串格式，按配置记录）
     */
    private String params;

    @Builder
    public LogLoginEvent(Object source, Integer loginType, Integer eventType,
                         String loginKey, Long userId, String username,
                         Integer status, String errorMsg, String requestIp,
                         String requestArea, String requestOs,
                         String requestDevice, String requestBrowser,
                         Long durationMs, String params) {
        // source：事件来源
        super(source);
        this.loginType = loginType;
        this.eventType = eventType;
        this.loginKey = loginKey;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.errorMsg = errorMsg;
        this.requestIp = requestIp;
        this.requestArea = requestArea;
        this.requestOs = requestOs;
        this.requestDevice = requestDevice;
        this.requestBrowser = requestBrowser;
        this.durationMs = durationMs;
        this.params = params;
    }

}
