package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.system.entity.LogOperate;
import cn.codesensi.leaf.rbac.system.mapper.LogOperateMapper;
import cn.codesensi.leaf.rbac.system.service.LogOperateService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author codesensi
 * @since 2026-06-28
 */
@Service
public class LogOperateServiceImpl extends ServiceImpl<LogOperateMapper, LogOperate> implements LogOperateService {

}
