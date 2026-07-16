package cn.codesensi.leaf.rbac.system.entity;

import cn.codesensi.leaf.rbac.common.core.BaseEntity;
import cn.codesensi.leaf.rbac.framework.listener.MybatisFlexListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
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
@Table(value = "log_login", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class LogLogin extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Id
    private Long id;

    /**
     * 登录方式:unknown-未知,account-账号密码,phone-手机号验证码,email-邮箱验证码
     */
    private String loginType;

    /**
     * 事件类型:0-未知,1-登录,2-登出
     */
    private Integer eventType;

    /**
     * 登录标识:账号/手机号
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
     * 登录状态:0-失败,1-成功
     */
    private Integer status;

    /**
     * 登录失败原因
     */
    private String errorMsg;

    /**
     * 请求来源IP地址
     */
    private String requestIp;

    /**
     * 登录地区
     */
    private String requestArea;

    /**
     * 登录系统
     */
    private String requestOs;

    /**
     * 登录设备
     */
    private String requestDevice;

    /**
     * 登录浏览器
     */
    private String requestBrowser;

    /**
     * 请求执行耗时:单位毫秒
     */
    private Long durationMs;

    /**
     * 请求参数
     */
    private String params;

}
