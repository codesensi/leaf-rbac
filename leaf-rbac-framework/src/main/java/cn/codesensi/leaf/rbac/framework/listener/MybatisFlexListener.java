package cn.codesensi.leaf.rbac.framework.listener;

import cn.codesensi.leaf.rbac.framework.base.BaseEntity;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;

/**
 * MybatisFlex监听器，用于自动填充创建人和更新人。
 */
public class MybatisFlexListener implements InsertListener, UpdateListener {
    /**
     * 新增操作的前置操作。
     *
     * @param entity 实体类
     */
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (StpUtil.isLogin()) {
                baseEntity.setCreator(StpUtil.getLoginIdAsLong());
            }
        }
    }

    /**
     * 更新操作的前置操作。
     *
     * @param entity 实体类
     */
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            if (StpUtil.isLogin()) {
                baseEntity.setUpdater(StpUtil.getLoginIdAsLong());
            }
        }
    }
}
