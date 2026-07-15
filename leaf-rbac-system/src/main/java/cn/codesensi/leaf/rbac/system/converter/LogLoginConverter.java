package cn.codesensi.leaf.rbac.system.converter;

import cn.codesensi.leaf.rbac.system.entity.LogLogin;
import cn.codesensi.leaf.rbac.framework.event.LogLoginEvent;
import org.mapstruct.Mapper;

/**
 * 登录日志对象转换
 *
 * @author codesensi
 * @since 2026-07-15
 */
@Mapper(componentModel = "spring")
public interface LogLoginConverter {

    /**
     * LogLoginEvent → LogLogin
     */
    LogLogin toEntity(LogLoginEvent event);

}
