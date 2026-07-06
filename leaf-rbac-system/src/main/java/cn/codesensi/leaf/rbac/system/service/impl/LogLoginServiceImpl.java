package cn.codesensi.leaf.rbac.system.service.impl;

import cn.codesensi.leaf.rbac.system.entity.LogLogin;
import cn.codesensi.leaf.rbac.system.mapper.LogLoginMapper;
import cn.codesensi.leaf.rbac.system.service.LogLoginService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 登录日志表 服务层实现。
 *
 * @author codesensi
 * @since 2026-07-06
 */
@Service
public class LogLoginServiceImpl extends ServiceImpl<LogLoginMapper, LogLogin> implements LogLoginService {

}
