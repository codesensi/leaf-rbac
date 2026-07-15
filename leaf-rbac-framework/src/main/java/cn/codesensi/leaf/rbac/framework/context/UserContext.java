package cn.codesensi.leaf.rbac.framework.context;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户上下文 —— 当前登录用户的会话快照数据载体。
 * <p>
 * 与 {@link UserContextHolder} 配合使用，通过 {@link ThreadLocal} 在线程范围内传递
 * 当前登录用户的详细信息（用户ID、用户名、昵称、登录标识），
 * 适用于同步请求和异步任务两种场景。
 * <p>
 * <ul>
 *   <li>{@code UserContext} 包含更丰富的用户信息，由外部业务代码显式管理生命周期
 *       （如登录成功后 {@code LoginService} 调用 {@code UserContextHolder.set()} 存入，
 *        请求结束时在 {@code Filter} 或 {@code Interceptor} 中调用
 *        {@code UserContextHolder.clear()} 清理）；</li>
 *   <li>两者可以共存：{@code OperatorContext} 解决的是异步线程获取登录用户ID的通用问题，
 *       {@code UserContext} 解决的是业务层需要完整用户信息的场景。</li>
 * </ul>
 *
 * @author codesensi
 * @see UserContextHolder
 * @since 1.0
 */
@Getter
@Setter
public class UserContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录标识（账号/手机号等）
     */
    private String loginKey;

    @Builder
    public UserContext(Long userId) {
        this.userId = userId;
    }

}
