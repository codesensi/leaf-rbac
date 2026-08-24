#!/bin/sh
# leaf-rbac 容器 entrypoint
# 以 root(容器默认用户) 进入，先修正 bind mount 目录属主，再降权到 appuser(UID 1001) 启动应用。
# 这样宿主机 /docker/leaf-rbac/{data,logs} 无需手工 chown，每次启动自动对齐属主。
# 说明：镜像为 Spring Boot 分层 jar（已解压到 /app），故用 JarLauncher 启动而非 -jar。
set -e

mkdir -p /app/data /app/logs
chown -R appuser:appuser /app/data /app/logs

# 降权到 appuser 后启动 Java 应用（setpriv 属 util-linux，Ubuntu 标配；缺失时回退 su）
if command -v setpriv >/dev/null 2>&1; then
    exec setpriv --reuid=1001 --regid=1001 --clear-groups -- java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher
else
    exec su -s /bin/sh appuser -c "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"
fi
