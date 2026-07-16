package cn.codesensi.leaf.rbac.api.request;

import cn.codesensi.leaf.rbac.common.enums.GenderEnum;
import cn.codesensi.leaf.rbac.framework.annotation.IdNo;
import cn.codesensi.leaf.rbac.framework.annotation.InEnum;
import cn.codesensi.leaf.rbac.framework.annotation.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存用户请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Schema(description = "保存用户请求参数")
public class UserSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 20, message = "用户名称长度不能超过20")
    @Schema(description = "用户名称", example = "admin")
    private String username;

    /**
     * 用户昵称
     */
    @Size(max = 50, message = "用户昵称长度不能超过50")
    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    /**
     * 用户身份证号码
     */
    @IdNo(message = "身份证号码格式不正确")
    @Schema(description = "用户身份证号码", example = "110101199001011234")
    private String idNo;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户邮箱", example = "admin@example.com")
    private String email;

    /**
     * 用户手机号码
     */
    @Phone(message = "手机号格式不正确")
    @Schema(description = "用户手机号码", example = "13800138000")
    private String phone;

    /**
     * 用户性别:0-保密,1-男,2-女
     */
    @InEnum(enumClass = GenderEnum.class, message = "用户性别不在指定范围内")
    @Schema(description = "用户性别:0-保密,1-男,2-女", example = "1")
    private Integer gender;

    /**
     * 用户头像地址
     */
    @Schema(description = "用户头像地址", example = "https://xxx.com/avatar.png")
    private String avatar;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

}
