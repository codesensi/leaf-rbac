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
 * 用户信息表 实体类。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户信息表实体类")
@Table("sys_user")
public class SysUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Id
    @Schema(description = "用户ID", example = "1")
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称", example = "admin")
    private String username;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码", example = "123456")
    private String password;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    /**
     * 用户身份证号码
     */
    @Schema(description = "用户身份证号码", example = "110101199001011234")
    private String idNo;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "admin@example.com")
    private String email;

    /**
     * 用户手机号码
     */
    @Schema(description = "用户手机号码", example = "13800138000")
    private String phone;

    /**
     * 用户性别:0-保密,1-男,2-女
     */
    @Schema(description = "用户性别:0-保密,1-男,2-女", example = "1")
    private Integer gender;

    /**
     * 用户头像地址
     */
    @Schema(description = "用户头像地址", example = "https://xxx.com/avatar.png")
    private String avatar;

    /**
     * 用户类型:0-系统用户
     */
    @Schema(description = "用户类型:0-系统用户", example = "0")
    private Integer type;

    /**
     * 用户状态:0-启用,1-禁用
     */
    @Schema(description = "用户状态:0-启用,1-禁用", example = "0")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

    /**
     * 系统内置标识:0-自定义,1-内置
     */
    @Schema(description = "系统内置标识:0-自定义,1-内置", example = "1")
    private Integer sysFlag;

}
