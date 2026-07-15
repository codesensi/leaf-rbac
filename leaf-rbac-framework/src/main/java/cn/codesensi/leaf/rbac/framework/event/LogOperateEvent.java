package cn.codesensi.leaf.rbac.framework.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 操作日志事件
 */

@Getter
@Setter
public class LogOperateEvent extends ApplicationEvent {

    /**
     * 操作所属模块（如：用户管理、角色管理）
     */
    private String module;

    /**
     * 操作类型（如：新增、修改、删除、查询、导出）
     */
    private Integer type;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作描述（如：新增用户、修改角色权限）
     */
    private String descr;

    /**
     * 操作状态（1=成功，0=失败）
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
     * 请求的URL地址
     */
    private String requestUrl;

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
     * 被调用方法的全限定名（包名.类名.方法名）
     */
    private String methodName;

    /**
     * 请求执行耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 请求参数（JSON字符串格式，按配置记录）
     */
    private String params;

    /**
     * 响应结果（JSON字符串格式，按配置记录）
     */
    private String result;

    @Builder
    public LogOperateEvent(Object source, String module, Integer type, Long userId,
                           String descr, Integer status, String errorMsg, String requestIp,
                           String requestUrl, String requestArea, String requestOs,
                           String requestDevice, String requestBrowser,
                           String methodName, Long durationMs, String params, String result) {
        // source：事件来源
        super(source);
        this.module = module;
        this.type = type;
        this.userId = userId;
        this.descr = descr;
        this.status = status;
        this.errorMsg = errorMsg;
        this.requestIp = requestIp;
        this.requestUrl = requestUrl;
        this.requestArea = requestArea;
        this.requestOs = requestOs;
        this.requestDevice = requestDevice;
        this.requestBrowser = requestBrowser;
        this.methodName = methodName;
        this.durationMs = durationMs;
        this.params = params;
        this.result = result;
    }

}
