package cn.codesensi.leaf.rbac.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存用户请求参数
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Data
@Accessors(chain = true)
@Schema(description = "保存用户请求参数")
public class UserSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称", example = "admin")
    private String username;

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
     * 备注
     */
    @Schema(description = "备注", example = "系统管理员")
    private String remark;

}
