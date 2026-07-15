package cn.codesensi.leaf.rbac.framework.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户上下文 —— 当前登录用户的全局会话快照数据载体。
 * <p>
 * 与 {@link UserContextHolder} 配合使用，通过 {@link ThreadLocal} 在线程范围内传递
 * 当前登录用户的详细信息（用户ID、用户名、昵称、登录标识），
 * 适用于同步请求和异步任务两种场景。
 * </p>
 * <p>
 * <b>生命周期：</b>
 * </p>
 * <ul>
 *   <li><b>初始化</b> — 登录成功后，{@code LoginService} 从数据库查询完整用户信息，
 *       构建 {@code UserContext} 存入 SaToken Session；</li>
 *   <li><b>请求级绑定</b> — {@code UserContextFilter} 在每个 HTTP 请求的入口处，
 *       从 SaToken Session 读取快照到 {@link UserContextHolder} 的 ThreadLocal 中；</li>
 *   <li><b>消费</b> — Controller → Service → Mapper 各层通过 {@code UserContextHolder.get()}
 *       获取当前操作人的完整信息（ID、用户名、昵称等）；</li>
 *   <li><b>销毁</b> — 请求结束时，{@code UserContextFilter} 在 {@code finally} 块中调用
 *       {@link UserContextHolder#clear()}，防止 Tomcat 线程池复用时上下文泄漏。</li>
 * </ul>
 *
 * @author codesensi
 * @see UserContextHolder
 * @since 1.0
 */
@Builder
@Data
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

}
