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
 * 操作日志表 实体类。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志表")
@Table("log_operate")
public class LogOperate extends BaseEntity implements Serializable {

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
     * 操作所属模块（如：用户管理、角色管理）
     */
    @Schema(description = "操作所属模块")
    private String module;

    /**
     * 操作类型:0-未知,1-新增,2-更新,3-查询,4-删除
     */
    @Schema(description = "操作类型:0-未知,1-新增,2-更新,3-查询,4-删除")
    private Integer type;

    /**
     * 操作描述
     */
    @Schema(description = "操作描述")
    private String descr;

    /**
     * 操作状态:0-失败,1-成功
     */
    @Schema(description = "操作状态:0-失败,1-成功")
    private Integer status;

    /**
     * 错误信息:当 status=0 时记录异常消息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 请求来源IP地址
     */
    @Schema(description = "请求来源IP地址")
    private String requestIp;

    /**
     * 请求的URL地址
     */
    @Schema(description = "请求的URL地址")
    private String requestUrl;

    /**
     * 请求地区
     */
    @Schema(description = "请求地区")
    private String requestArea;

    /**
     * 请求系统
     */
    @Schema(description = "请求系统")
    private String requestOs;

    /**
     * 请求设备
     */
    @Schema(description = "请求设备")
    private String requestDevice;

    /**
     * 请求浏览器
     */
    @Schema(description = "请求浏览器")
    private String requestBrowser;

    /**
     * 被调用方法的全限定名（包名.类名.方法名）
     */
    @Schema(description = "被调用方法的全限定名")
    private String methodName;

    /**
     * 请求执行耗时（毫秒）
     */
    @Schema(description = "请求执行耗时（毫秒）")
    private Long durationMs;

    /**
     * 请求参数（JSON字符串格式，按配置记录）
     */
    @Schema(description = "请求参数")
    private String params;

    /**
     * 响应结果（JSON字符串格式，按配置记录）
     */
    @Schema(description = "响应结果")
    private String result;

}
