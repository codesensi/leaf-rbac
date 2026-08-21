# ============================================================
# leaf-rbac 多阶段构建 Dockerfile
#   阶段1: Maven 编译打包  阶段2: 精简运行镜像
# 用法:
#   docker build -t leaf-rbac:1.0.0 .
# ============================================================

# ---------- 阶段1：构建 ----------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# 优先拷贝 pom 与 mvnw，利用 Docker 层缓存加速依赖下载
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
COPY leaf-rbac-common/pom.xml leaf-rbac-common/pom.xml
COPY leaf-rbac-framework/pom.xml leaf-rbac-framework/pom.xml
COPY leaf-rbac-system/pom.xml leaf-rbac-system/pom.xml
COPY leaf-rbac-api/pom.xml leaf-rbac-api/pom.xml
COPY leaf-rbac-bootstrap/pom.xml leaf-rbac-bootstrap/pom.xml

# Windows 下 COPY 的 mvnw 可能丢失可执行位，补上
RUN chmod +x mvnw

# 预下载依赖（仅 pom，命中缓存时后续编译秒级完成）
RUN ./mvnw -B -q dependency:go-offline -pl leaf-rbac-bootstrap -am || true

# 拷贝源码并打包
COPY leaf-rbac-common leaf-rbac-common
COPY leaf-rbac-framework leaf-rbac-framework
COPY leaf-rbac-system leaf-rbac-system
COPY leaf-rbac-api leaf-rbac-api
COPY leaf-rbac-bootstrap leaf-rbac-bootstrap

# 父 pom 已硬编码 surefire skipTests=true，无需额外 -DskipTests
RUN ./mvnw -B clean package -pl leaf-rbac-bootstrap -am \
    && cp leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar /build/app.jar

# ---------- 阶段2：运行 ----------
FROM eclipse-temurin:21-jre
LABEL maintainer="codesensi"

WORKDIR /app

# 非 root 用户运行，提升安全性
RUN useradd -r -u 1001 appuser
COPY --from=builder /build/app.jar /app/app.jar

# 数据/日志目录交给卷挂载；运行目录权限交给 appuser
RUN mkdir -p /app/data /app/logs && chown -R appuser:appuser /app

USER appuser

# 主业务端口 9098；Actuator 管理端口 9099（health 检查用）
EXPOSE 9098 9099

ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=prod

# CMD 使用 exec 形式，保证优雅停机信号(TERM)能被 JVM 捕获
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
