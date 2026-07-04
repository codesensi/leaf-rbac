package cn.codesensi.leaf.rbac.bootstrap;

import cn.codesensi.leaf.rbac.framework.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 项目启动入口。
 * <p>
 * 启动完成后会自动打印 API 文档地址等相关信息，方便开发调试。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@EnableAsync
@MapperScan("cn.codesensi.leaf.rbac.**.mapper")
@SpringBootApplication(scanBasePackages = "cn.codesensi.leaf.rbac")
public class LeafRbacBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeafRbacBootstrapApplication.class, args);
    }

    /**
     * 应用完全启动就绪后，打印项目版本、环境及 API 文档地址等信息。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        String activeProfile = String.join(",", environment.getActiveProfiles());
        String port = environment.getProperty("server.port");
        String localIp = IpUtil.getLocalIp();
        log.info("    Application is running");
        log.info("    Profile:  {}", activeProfile);
        log.info("    Doc URL:  http://{}:{}/swagger-ui.html", localIp, port);
    }

}
