package cn.codesensi.leaf.rbac.framework.listener;

import cn.codesensi.leaf.rbac.framework.base.BaseEntity;
import cn.codesensi.leaf.rbac.framework.context.UserContextHolder;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;

/**
 * MybatisFlex 全局监听器 —— 自动填充实体审计字段。
 * <p>
 * 实现 {@link InsertListener} 和 {@link UpdateListener} 接口，
 * 在数据插入/更新前自动从 {@link UserContextHolder} 获取当前登录用户 ID，
 * 并设置到 {@link BaseEntity} 的创建人/更新人字段，实现审计日志的自动记录。
 * </p>
 *
 * @author codesensi
 * @since 1.0
 */
public class MybatisFlexListener implements InsertListener, UpdateListener {

    /**
     * 插入操作前置回调 —— 自动填充创建人。
     * <p>
     * 当实体继承自 {@link BaseEntity} 且当前请求上下文中存在用户 ID 时，
     * 将用户 ID 设置为实体的创建人字段。
     * </p>
     *
     * @param entity 待插入的实体对象
     */
    @Override
    public void onInsert(Object entity) {
        // 检查实体是否继承自 BaseEntity，确保具备审计字段
        if (entity instanceof BaseEntity baseEntity) {
            // 从当前请求线程的 UserContextHolder 中获取用户 ID
            Long userId = UserContextHolder.getUserId();
            // 仅在用户 ID 不为空时设置，避免被未登录场景（如内部批处理）的空值覆盖
            if (ObjUtil.isNotNull(userId)) {
                baseEntity.setCreator(userId);
            }
        }
    }

    /**
     * 更新操作前置回调 —— 自动填充更新人。
     * <p>
     * 逻辑与 {@link #onInsert(Object)} 一致，区别在于设置的是更新人字段而非创建人字段。
     * 注意：创建人字段在更新时不会被修改，以保留首次创建时的记录。
     * </p>
     *
     * @param entity 待更新的实体对象
     */
    @Override
    public void onUpdate(Object entity) {
        // 检查实体是否继承自 BaseEntity，确保具备审计字段
        if (entity instanceof BaseEntity baseEntity) {
            // 从当前请求线程的 UserContextHolder 中获取用户 ID
            Long userId = UserContextHolder.getUserId();
            // 仅在用户 ID 不为空时设置，避免被未登录场景（如内部批处理）的空值覆盖
            if (ObjUtil.isNotNull(userId)) {
                baseEntity.setUpdater(userId);
            }
        }
    }
}
