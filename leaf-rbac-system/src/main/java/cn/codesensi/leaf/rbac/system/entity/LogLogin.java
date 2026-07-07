package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.system.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录日志表 实体类。
 *
 * @author codesensi
 * @since 2026-07-06
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录日志表实体类")
@Table("log_login")
public class LogLogin extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Id
    @Schema(description = "日志ID")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 登录方式:0-未知,1-账号密码,2-手机号
     */
    @Schema(description = "登录方式:0-未知,1-账号密码,2-手机号")
    private Integer loginType;

    /**
     * 事件类型:0-未知,1-登录,2-登出
     */
    @Schema(description = "事件类型:0-未知,1-登录,2-登出")
    private Integer eventType;

    /**
     * 登录标识:账号/手机号
     */
    @Schema(description = "登录标识:账号/手机号")
    private String loginKey;

    /**
     * 登录人ID
     */
    @Schema(description = "登录人ID")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long userId;

    /**
     * 登录人账号
     */
    @Schema(description = "登录人账号")
    private String username;

    /**
     * 登录状态:0-失败,1-成功
     */
    @Schema(description = "登录状态:0-失败,1-成功")
    private Integer status;

    /**
     * 登录失败原因
     */
    @Schema(description = "登录失败原因")
    private String errorMsg;

    /**
     * 请求来源IP地址
     */
    @Schema(description = "请求来源IP地址")
    private String requestIp;

    /**
     * 登录地区
     */
    @Schema(description = "登录地区")
    private String requestArea;

    /**
     * 登录系统
     */
    @Schema(description = "登录系统")
    private String requestOs;

    /**
     * 登录设备
     */
    @Schema(description = "登录设备")
    private String requestDevice;

    /**
     * 登录浏览器
     */
    @Schema(description = "登录浏览器")
    private String requestBrowser;

    /**
     * 请求执行耗时:单位毫秒
     */
    @Schema(description = "请求执行耗时:单位毫秒")
    private Long durationMs;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String params;

}
