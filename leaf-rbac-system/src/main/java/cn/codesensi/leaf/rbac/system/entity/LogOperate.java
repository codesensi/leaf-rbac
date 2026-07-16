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
 * 操作日志表 实体类。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "log_operate", onInsert = MybatisFlexListener.class, onUpdate = MybatisFlexListener.class)
public class LogOperate extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Id
    private Long id;

    /**
     * 操作所属模块（如：用户管理、角色管理）
     */
    private String module;

    /**
     * 操作类型:unknown-未知,insert-新增,update-更新,query-查询,delete-删除
     */
    private String type;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作描述
     */
    private String descr;

    /**
     * 操作状态:0-失败,1-成功
     */
    private Integer status;

    /**
     * 错误信息:当 status=0 时记录异常消息
     */
    private String errorMsg;

    /**
     * 请求来源IP地址
     */
    private String ip;

    /**
     * 请求的URL地址
     */
    private String url;

    /**
     * 请求地区
     */
    private String region;

    /**
     * 请求系统
     */
    private String os;

    /**
     * 请求设备
     */
    private String device;

    /**
     * 请求浏览器
     */
    private String browser;

    /**
     * 被调用方法的全限定名(包名.类名.方法名)
     */
    private String method;

    /**
     * 求执行耗时(毫秒)
     */
    private Long durationMs;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 响应结果
     */
    private String result;

}
