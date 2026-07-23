package cn.codesensi.leaf.rbac.bootstrap;

import cn.codesensi.leaf.rbac.framework.event.CacheDictEvent;
import cn.codesensi.leaf.rbac.framework.event.CacheRegionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
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
@RequiredArgsConstructor
@Slf4j
@EnableCaching
@EnableAsync
@MapperScan("cn.codesensi.leaf.rbac.**.mapper")
@SpringBootApplication(scanBasePackages = "cn.codesensi.leaf.rbac")
public class LeafRbacBootstrapApplication {

    private final ApplicationEventPublisher eventPublisher;

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
        log.info("    Application is running");
        log.info("    Profile:  {}", activeProfile);
        log.info("    Doc URL:  http://127.0.0.1:{}/swagger-ui.html", port);

        // 发布行政区划缓存事件
        Boolean enabledRegion = environment.getProperty("app.cache.preload-region", Boolean.class);
        if (Boolean.TRUE.equals(enabledRegion)) {
            eventPublisher.publishEvent(new CacheRegionEvent(this, "应用启动"));
        }

        // 发布字典缓存事件
        Boolean enabledDict = environment.getProperty("app.cache.preload-dict", Boolean.class);
        if (Boolean.TRUE.equals(enabledDict)) {
            eventPublisher.publishEvent(new CacheDictEvent(this, "应用启动"));
        }
    }

}
