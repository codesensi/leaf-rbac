package cn.codesensi.leaf.rbac.framework.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 侦听器
 *
 * <p> 你可以通过实现此接口在用户登录、退出等关键性操作时进行一些AOP切面操作 </p>
 *
 * @author click33
 * @since 1.17.0
 */
@Component
public class AppSaTokenListener implements SaTokenListener {
    /**
     * 每次登录时触发
     *
     * @param loginType      账号类别
     * @param loginId        账号id
     * @param tokenValue     本次登录产生的 token 值
     * @param loginParameter 登录参数
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {

    }

    /**
     * 每次注销时触发
     *
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token值
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {

    }

    /**
     * 每次被踢下线时触发
     *
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token值
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {

    }

    /**
     * 每次被顶下线时触发
     *
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token值
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {

    }

    /**
     * 每次被封禁时触发
     *
     * @param loginType   账号类别
     * @param loginId     账号id
     * @param service     指定服务
     * @param level       封禁等级
     * @param disableTime 封禁时长，单位: 秒
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {

    }

    /**
     * 每次被解封时触发
     *
     * @param loginType 账号类别
     * @param loginId   账号id
     * @param service   指定服务
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {

    }

    /**
     * 每次打开二级认证时触发
     *
     * @param loginType  账号类别
     * @param tokenValue token值
     * @param service    指定服务
     * @param safeTime   认证时间，单位：秒
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {

    }

    /**
     * 每次关闭二级认证时触发
     *
     * @param loginType  账号类别
     * @param tokenValue token值
     * @param service    指定服务
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {

    }

    /**
     * 每次创建 SaSession 时触发
     *
     * @param id SessionId
     */
    @Override
    public void doCreateSession(String id) {

    }

    /**
     * 每次注销 SaSession 时触发
     *
     * @param id SessionId
     */
    @Override
    public void doLogoutSession(String id) {

    }

    /**
     * 每次 Token 续期时触发（注意：是 timeout 续期，而不是 active-timeout 续期）
     *
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token 值
     * @param timeout    续期时间
     */
    @Override
    public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {

    }
}
