# ========== 阶段1: 构建 ==========
FROM eclipse-temurin:21-jdk AS builder

# 设置构建工作目录
WORKDIR /build

# 第一步：复制所有模块的 pom.xml（保持目录结构）
COPY pom.xml ./pom.xml
COPY leaf-rbac-common/pom.xml ./leaf-rbac-common/pom.xml
COPY leaf-rbac-framework/pom.xml ./leaf-rbac-framework/pom.xml
COPY leaf-rbac-system/pom.xml ./leaf-rbac-system/pom.xml
COPY leaf-rbac-api/pom.xml ./leaf-rbac-api/pom.xml
COPY leaf-rbac-bootstrap/pom.xml ./leaf-rbac-bootstrap/pom.xml
COPY .mvn/ ./.mvn/
COPY mvnw ./mvnw
RUN chmod +x mvnw

# 第二步：预下载依赖（缓存挂载，不写入镜像层）
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -pl leaf-rbac-bootstrap -am -B || true

# 第三步：复制全部源码并构建
COPY . ./
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests -pl leaf-rbac-bootstrap -am -B && \
    cp leaf-rbac-bootstrap/target/*.jar app.jar

# ========== 阶段2: 运行 ==========
FROM eclipse-temurin:21-jre-alpine

# 添加镜像元数据标签，便于后续维护和追溯
LABEL maintainer="leaf-rbac-team"

# ----------------------------------------------------------
# 安全加固：创建非 root 专用用户
# ----------------------------------------------------------
# addgroup -S: 创建系统组（GID 从 100-999 分配，避免与宿主机普通用户冲突）
# adduser -S:  创建系统用户（不分配登录 shell、不创建家目录）
#         -H:  不创建 /home/leaf 家目录（应用工作目录为 /app，无需家目录）
#         -G:  将用户加入 leaf 组
#         -s:  指定 shell 为 /sbin/nologin，彻底禁止交互式终端登录
#              即使容器被入侵，攻击者也无法获取 shell
RUN addgroup -S leaf \
    && adduser -S -H -G leaf -s /sbin/nologin leaf

# ----------------------------------------------------------
# 目录准备：创建工作目录及持久化数据/日志目录
# ----------------------------------------------------------
# WORKDIR: 设置应用工作目录，后续指令和容器运行时均以此为基准路径
WORKDIR /app

# mkdir -p: 递归创建 data（数据库文件）和 logs（日志文件）目录
#           必须在 USER 切换之前以 root 身份执行，否则非 root 用户无权创建
# chown -R: 递归修改 /app 及所有子目录的所有者为 leaf:leaf
#           确保 leaf 用户对数据文件和日志文件拥有完整的读写权限
RUN mkdir -p /app/data /app/logs \
    && chown -R leaf:leaf /app

# ----------------------------------------------------------
# 权限收敛：切换到非 root 用户
# ----------------------------------------------------------
# 此后所有 RUN/CMD/ENTRYPOINT 指令及容器运行时进程均以 leaf 用户身份执行
# 遵循最小权限原则，Java 进程不再拥有 root 权限
USER leaf

# ----------------------------------------------------------
# 部署应用：从构建阶段复制产物
# ----------------------------------------------------------
# --from=builder: 引用构建阶段的文件系统
# 仅复制最终 jar 包，不包含源码、Maven 缓存等构建中间产物
# 配合多阶段构建，显著减小最终镜像体积
COPY --from=builder /build/leaf-rbac-bootstrap/target/*.jar app.jar

# ----------------------------------------------------------
# 网络声明：暴露应用端口
# ----------------------------------------------------------
# EXPOSE 仅为文档声明，告知使用者容器内监听的端口号
# 实际端口映射由 docker-compose.yml 中的 ports 字段决定
EXPOSE 9098

# ----------------------------------------------------------
# 启动入口：定义容器启动命令
# ----------------------------------------------------------
# 使用 exec 格式（JSON 数组），使 Java 进程作为 PID 1 直接接收信号
# 避免 shell 包装导致 SIGTERM 无法正确传递、容器优雅关闭失效的问题
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]