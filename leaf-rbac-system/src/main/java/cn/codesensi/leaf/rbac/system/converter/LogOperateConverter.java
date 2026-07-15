package cn.codesensi.leaf.rbac.system.converter;

import cn.codesensi.leaf.rbac.system.entity.LogOperate;
import cn.codesensi.leaf.rbac.framework.event.LogOperateEvent;
import org.mapstruct.Mapper;

/**
 * 操作日志对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface LogOperateConverter {

    /**
     * LogOperateEvent → LogOperate
     */
    LogOperate toEntity(LogOperateEvent event);

}
