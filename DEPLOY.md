# leaf-rbac Docker 部署方案

> 场景：**已有** Redis 与数据库（MySQL/PostgreSQL）跑在 Docker 中，仅需把 leaf-rbac 应用容器化。应用**不 join** 中间件的 Docker 网络，通过**宿主机 IP + 端口直连**访问中间件（前提：中间件端口已映射到宿主机）。若你希望连中间件一起用 Docker Compose 编排，见文末「扩展：中间件也纳入 Compose」。

## 目录结构

```
Dockerfile              # 多阶段构建：Maven(JDK21) 编译 → 精简 JRE 运行
docker-entrypoint.sh    # 容器入口：root 进入 chown bind 目录后降权到 appuser 启动
docker-compose.yml      # 仅编排应用，经宿主机 IP 直连中间件（无自定义网络）
.env.example            # 部署环境变量模板（复制为 .env 使用）
DEPLOY.md               # 本文档
.dockerignore           # 构建上下文过滤（在项目根，Docker 构建时自动生效）
```

## 一、前置检查

1. 中间件已在 Docker 中运行，例如：
   - Redis 容器名/别名 `redis`
   - MySQL 容器名/别名 `mysql`（或 PostgreSQL `postgres`）
2. **确认中间件端口已映射到宿主机**（应用容器通过宿主机 IP + 端口访问，不依赖 Docker 网络/DNS）：
   ```bash
   docker ps
   # PORTS 列应能看到类似：
   #   0.0.0.0:3306->3306/tcp   (MySQL)
   #   0.0.0.0:6379->6379/tcp   (Redis)
   ```
   若某中间件没映射端口，需要重新以映射方式启动它，或改用「方案：join 中间件网络」（见文末扩展）。
3. 记下宿主机对外可达的 IP（`hostname -I` 或局域网 IP）。应用与该中间件同机、且中间件用 `bridge` 映射时，填 `127.0.0.1` 即可；跨机/外部访问则填那台机器的局域网或公网 IP。
4. `bind mount` 目录无需手工设置属主：容器以 root 进入，启动时由 `docker-entrypoint.sh` 自动 `chown` `/app/data`、`/app/logs` 到 `appuser(UID 1001)` 后再降权运行，不需要 `sudo chown`。只需确保目标目录存在即可：
   ```bash
   sudo mkdir -p /docker/leaf-rbac/data /docker/leaf-rbac/logs
   ```

## 二、配置环境变量

`.env` 是**唯一配置源**，同时供 docker compose 插值（`${APP_PORT}` 等）和注入容器环境变量用；Spring 在应用内通过容器环境变量读取 `DB_TYPE`/`MYSQL_*`/`POSTGRESQL_*`/`REDIS_*` 等连接信息，**无需**维护配置文件同级的 `.env`（Docker 部署下 Spring 不读文件，全部走环境变量）。

```bash
cd <项目根目录>
cp .env.example .env
# 编辑 .env：把 MYSQL_HOST/POSTGRESQL_HOST/REDIS_HOST 填成宿主机 IP（默认 127.0.0.1），端口填宿主机映射端口
# 该文件包含 compose 插值 + Spring 连接信息的全部变量
```
> 说明：以下命令默认在项目根目录执行；`.env` 位于项目根，用 `--env-file .env` 指定。`.env` 已被 .gitignore 忽略（提交的模板是 `.env.example`）。

## 三、打包镜像

在**项目根目录**执行（Dockerfile 位于项目根，构建上下文是项目根）：

```bash
cd <项目根目录>

# 多阶段构建：先在容器内用 Maven 编译，再生成精简 JRE 镜像（首次含依赖下载，较慢）
docker build -t codesensi/leaf-rbac:latest .
```

- `-t codesensi/leaf-rbac:latest`：镜像标签（名称:标签）。`codesensi` 为镜像仓库命/发布账号，`leaf-rbac` 为镜像名，`latest` 为标签，均可自定义，如 `-t codesensi/leaf-rbac:1.0.0`。
- 构建完成后 `docker images` 可看到 `codesensi/leaf-rbac  latest`。
- 镜像标签与 compose 中的 `image: codesensi/leaf-rbac:${APP_IMAGE_TAG:-latest}` 对应：构建/部署的标签需与 `.env` 的 `APP_IMAGE_TAG`（默认 `latest`）保持一致，否则 compose 会因找不到对应 tag 的镜像而失败。
- 若要把镜像推到私有仓库供其它机器拉取：
  ```bash
  docker tag codesensi/leaf-rbac:latest <registry>/codesensi/leaf-rbac:latest
  docker push <registry>/codesensi/leaf-rbac:latest
  ```

## 四、用镜像部署

compose 是 **image-only**（引用已打包的镜像，不做构建），本地需先有 `codesensi/leaf-rbac:latest`（见上一步，构建时的 tag 必须与 compose 的 image 一致）。在项目根目录执行：

```bash
cd <项目根目录>

# 启动（用已存在的镜像）
docker compose --env-file .env up -d

# 查看启动日志
docker compose --env-file .env logs -f app

# 查看健康状态
docker compose --env-file .env ps
```

> 说明：compose 采用 `image:` 直引镜像（无 `build:` 段，不做构建）。本地若无该镜像且无法从 registry 拉取则会失败，故部署前请先完成上一步的镜像打包。重新打包镜像后，需 `docker compose --env-file .env up -d --force-recreate` 让新镜像生效。

启动成功后：

- Swagger 文档：`http://<宿主机IP>:<APP_PORT>/swagger-ui.html`（宿主机端口以 `.env` 的 `APP_PORT` 为准，默认 `9098`）
- 健康检查：由 Docker healthcheck 在容器内部访问 `127.0.0.1:9099/actuator/health`（`json` 显示为 `UP` 即健康），不对外暴露
- 默认账密：`sadmin / 123456`

## 五、关键说明

### 1. 首次启动自动初始化
应用内置 `DatabaseInitializer`：首次启动自动**建库建表并灌入初始数据**，随后在 `/app/data/app.lock` 写锁文件防止重复初始化（对应 `app.lock-file`）。`/app/data` 已 bind mount 到 `/docker/leaf-rbac/data`，**容器重建不会重复初始化**。

- 手动重置：`docker compose --env-file .env exec app rm -f /app/data/app.lock && docker compose --env-file .env restart app`（会重置数据库，先备份！）

### 2. 数据库类型切换
`DB_TYPE=mysql` 或 `DB_TYPE=postgresql`。两种类型对应的连接变量分别填值，未使用的一组可留空。默认（未填）dev/prod yml 的占位符默认值是 `192.168.2.3`，请务必在 `.env` 覆盖为宿主机 IP 与映射端口。

### 3. 端口
- `9098`：主业务端口，Swagger/接口。**唯一映射到宿主机的端口**。宿主机侧端口可在 `.env` 的 `APP_PORT` 配置（默认 `9098`，例如改为 `APP_PORT=18098` 即 `18098:9098`）；容器内部始终固定 `9098`（application.yml 定义，勿改）。
- `9099`：Actuator 独立管理端口（仅 `health,info,metrics,loggers`，`read_only`）。**不映射到宿主机**——Docker healthcheck 在容器内部用 `127.0.0.1:9099` 探活，无需对外。这样管理端点对外不可达，更安全。
  - 若确有外部探活/监控需求（如 Prometheus、K8s liveness、云 LB），再按需补映射或经内网网络访问。

### 4. 日志
- 应用日志写 `/app/logs/{name}_prod.log`（bind mount 到宿主机 `/docker/leaf-rbac/logs`），可用 `docker compose --env-file .env logs app` 实时查看。数据（含 `app.lock`）在宿主机 `/docker/leaf-rbac/data`。
- traceId 链路：每次请求会生成 traceId 打印在日志中，跨服务排查可在日志中检索。

### 5. 限定访问 / 安全
- 应用进程以非 root 用户（`appuser`, UID 1001）运行：容器以 root 进入，`docker-entrypoint.sh` 先 `chown` bind 挂载目录，再用 `setpriv`（缺失时回退 `su`）降权到 `appuser` 启动 Java。
- 连接密码通过 `.env` 注入，`.env` **已被 .gitignore 忽略，勿提交**。镜像内不含明文密码。
- 若需限制单台实例内存：改 `JAVA_OPTS`（默认 `-Xms256m -Xmx512m`）。
- 生产建议在应用前加 Nginx/Traefik 反代 https；`9099` 管理端点默认不映射，天然不对外。

## 六、常用运维命令

```bash
cd <项目根目录>

docker compose --env-file .env ps            # 查看状态
docker compose --env-file .env logs -f app   # 跟踪日志
docker compose --env-file .env restart app   # 重启应用
docker compose --env-file .env down          # 停止
# 注：本次为 bind mount 到宿主机目录，down -v 不会影响 /docker/leaf-rbac 下的数据/日志；如需清理数据请手动删除 /docker/leaf-rbac/data
docker compose --env-file .env exec app sh   # 进入容器
```

## 七、备选方案

### A. 中间件未映射端口时：让应用 join 中间件所在网络

上文默认「宿主机 IP 直连」要求中间件端口映射到宿主机。若某中间件**没有**映射端口（如 MySQL 只在容器内网暴露），可改为让应用加入它所在的 Docker 网络，用服务名访问。此时在 `docker-compose.yml` 中给 `app` 加回网络归属并声明外部网络：

```yaml
services:
  app:
    # ...（其余同上）
    networks:
      - ext-net

networks:
  ext-net:
    name: <查到的中间件网络名>   # docker inspect <中间件容器> --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}}{{println}}{{end}}'
    external: true
```

并把 `.env` 中 `MYSQL_HOST`/`REDIS_HOST` 改为容器服务名（`mysql`/`redis`），而不是宿主机 IP。

### B. 想连中间件一起用 Compose 编排

本方案是「统一由 Compose 管理中间件 + 应用」，与「已有中间件」场景不同，供没有现成中间件容器时参考。为应用声明内部网络并与中间件同网：

```yaml
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USERNAME}
    volumes: ["mysql_data:/var/lib/mysql"]
    networks: ["leaf-net"]
  redis:
    image: redis:7
    command: ["redis-server", "--requirepass", "${REDIS_PASSWORD}"]
    volumes: ["redis_data:/data"]
    networks: ["leaf-net"]

networks:
  leaf-net: {}          # 非 external，由 Compose 创建
volumes:
  mysql_data:
  redis_data:
```

> 提醒：此方案下 `MYSQL_HOST`/`REDIS_HOST` 填服务名 `mysql`/`redis`（Compose DNS 解析），端口也填容器内端口（3306/6379），无需靠宿主机映射。

### 不引入 Maven、直接 COPY 已打好的 jar 的替代 Dockerfile

若你在宿主机用 `.\mvnw.cmd clean package` 已打好 `leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar`，可用更快的单阶段镜像：

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -r -u 1001 appuser
COPY leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar /app/app.jar
RUN mkdir -p /app/data /app/logs && chown -R appuser:appuser /app
USER appuser
EXPOSE 9098 9099
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
```
