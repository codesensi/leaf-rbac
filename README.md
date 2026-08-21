# leaf-rbac · 一叶后台管理系统

> 纯净的 RBAC 权限管理脚手架 · 基于 Spring Boot 3 的多模块后端工程
>
> 模块化分层、多数据库无缝切换、Sa-Token 认证鉴权、开箱即用

---

## 目录

- [项目简介](#项目简介)
- [核心特性](#核心特性)
- [技术栈](#技术栈)
- [模块架构](#模块架构)
  - [各模块职责](#各模块职责)
- [项目目录](#项目目录)
- [快速开始](#快速开始)
  - [环境要求](#环境要求)
  - [启动（开发模式）](#启动开发模式)
  - [切换数据库](#切换数据库)
- [接口概览](#接口概览)
- [统一响应](#统一响应)
- [配置说明](#配置说明)
  - [生产建议](#生产建议)
- [部署方案](#部署方案)
- [许可证](#许可证)

---

## 项目简介

`leaf-rbac` 是一个纯净的 RBAC（基于角色的访问控制）权限管理后台脚手架，为快速搭建企业级后台管理系统而设计。项目聚焦于「认证鉴权 + 系统管理」的通用能力，将所有横切关注点（日志、限流、鉴权、脱敏、统一响应、链路追踪等）收敛到独立的 `framework` 层，业务模块只关心实体与接口，保持高内聚、低耦合、易扩展。

**默认账密：`sadmin / 123456`**

---

## 核心特性

- ✅ **RBAC 权限模型**：用户 / 角色 / 菜单三级模型 + 用户-角色、角色-菜单关联，支持菜单权限与角色编码双重粒度鉴权。
- ✅ **Sa-Token 认证鉴权**：支持随机 token / JWT，多端并发登录、活跃超时冻结、token 一键踢下线，可与 Redis 集成实现集群化存储。
- ✅ **多数据库无缝切换**：一套代码同时支持 MySQL / PostgreSQL / H2，通过 `app.db.type` 一键切换；首次启动自动建库建表并初始化数据。
- ✅ **策略化登录与验证码**：登录方式与验证码均采用「策略 + 工厂」模式，方便扩展账号登录、短信登录、图片/短信验证码等。
- ✅ **Bucket4j 多级限流**：登录防暴力破解、验证码防刷、系统接口按用户限流、全局 IP 兜底，支持 Redis / Lettuce 分布式令牌桶。
- ✅ **统一响应体**：通过 `ResponseBodyAdvice` 自动将 Controller 返回值包装为统一 `Result`，业务代码无需手动包装。
- ✅ **可观测性**：TraceId 贯穿日志、Logbook 请求日志（敏感字段自动脱敏）、登录/操作审计日志、Spring Actuator 监控。
- ✅ **优雅停机**：启用 Spring 生命周期优雅停机，支持线程池任务排空后再关闭。

---

## 技术栈

| 分类 | 选型 |
| --- | --- |
| 核心框架 | Spring Boot 3.5.16（Java 21） |
| ORM | MyBatis-Flex 1.11.7（APT 代码生成、逻辑删除、雪花主键） |
| 认证鉴权 | Sa-Token 1.45.0（starter + Redis 模板 + JWT） |
| 数据库 | MySQL 9.7 / PostgreSQL 42.7 / H2 2.4（HikariCP 连接池） |
| 对象映射 | MapStruct 1.6.3 + Lombok 1.18.46 |
| API 文档 | SpringDoc / Swagger UI |
| 缓存 | Spring Cache + Redis（Lettuce） |
| 限流 | Bucket4j（令牌桶，支持 Redis / Lettuce） |
| 工具库 | Hutool 5.8、Commons-Lang3 |
| 其他 | EasyCaptcha（验证码）、Ip2region（IP 定位）、UserAgentUtils、Logbook、Nashorn |

---

## 模块架构

工程采用 Maven 多模块结构，依赖关系呈单向递进：

```mermaid
flowchart LR
    A[leaf-rbac-common] --> B[leaf-rbac-framework] --> C[leaf-rbac-system] --> D[leaf-rbac-api] --> E[leaf-rbac-bootstrap]
```

### 各模块职责

| 模块 | 职责 | 关键子包 |
| --- | --- | --- |
| `common` | 常量、枚举、核心对象（`Result`）、异常、配置属性、通用工具 | `constants` `core` `enums` `exception` `properties` `util` |
| `framework` | 统一响应、全局异常、AOP 切面、过滤器、拦截器、事件、缓存、自定义校验、用户上下文 | `advice` `annotation` `aspect` `filter` `handler` `event` `context` `validator` `config` |
| `system` | 实体、Mapper、Service、DTO、MapStruct 转换器、登录/验证码策略、Sa-Token 权限装配 | `entity` `mapper` `service` `dto` `converter` `strategy` `security` |
| `api` | Controller、请求/响应对象、DTO↔VO 转换、Swagger 配置 | `controller` `request` `response` `converter` `config` |
| `bootstrap` | 应用启动入口、环境配置、动态数据源、数据库初始化 | `config` `initializer` `condition` |

**分层约定**：`system` 层暴露 `DTO`（领域对象），`api` 层持有网络对象（`request`/`response`），通过两级 MapStruct `converter` 完成转换，实现依赖方向单一、实体不对外暴露。

---

## 项目目录

```
leaf-rbac/
├── leaf-rbac-common/       # 公共基础模块
├── leaf-rbac-framework/    # 框架核心模块
├── leaf-rbac-system/       # 系统业务模块
├── leaf-rbac-api/          # 接口层模块
├── leaf-rbac-bootstrap/    # 启动模块（yml、SQL、xdb）
│   └── src/main/resources/
│       ├── application.yml / application-dev.yml / application-prod.yml
│       ├── sql/{h2,mysql,postgresql}/   # 建表与初始化脚本
│       └── xdb/                          # IP 定位库
├── data/                   # 运行数据（数据库文件、app.lock 锁文件）
├── logs/                   # 运行日志
└── pom.xml                 # 父 POM（版本与依赖统一管理）
```

---

## 快速开始

### 环境要求

- JDK **21+**
- Maven **3.8+**（或直接使用仓库自带的 `mvnw` / `mvnw.cmd`）

### 启动（开发模式）

默认开发环境使用 **MySQL（`app.db.type: ${DB_TYPE:mysql}`，缺省 `mysql`）**，也可通过 `DB_TYPE` 环境变量切换为 `h2` / `postgresql`。首次启动自动完成建库建表与数据初始化；若选用 H2 则无需额外安装任何数据库中间件：

```bash
# Windows
.\mvnw.cmd spring-boot:run -pl leaf-rbac-bootstrap

# Linux / macOS
./mvnw spring-boot:run -pl leaf-rbac-bootstrap
```

或先整体构建再运行：

```bash
./mvnw clean install -DskipTests
java -jar leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar
```

启动成功后：

- 应用入口：<http://127.0.0.1:9098>
- Swagger 接口文档：<http://127.0.0.1:9098/swagger-ui.html>
- 默认账密：`sadmin / 123456`

### 切换数据库

修改 `leaf-rbac-bootstrap/src/main/resources/application-dev.yml` 中的 `app.db.type`（现为环境变量占位符形式，也可直接改默认值）：

```yaml
app:
  db:
    type: ${DB_TYPE:mysql}   # 可选：h2 / mysql / postgresql；开发缺省 mysql，亦可通过环境变量 DB_TYPE 注入
```

项目支持 **MySQL / PostgreSQL / H2** 三种数据库，连接信息集中在 `app.db.*` 下管理；HikariCP 全局参数通过 `spring.datasource.hikari` 绑定。系统会在首次启动时自动创建目标数据库与表结构，并导入初始数据（脚本位于 `leaf-rbac-bootstrap/src/main/resources/sql/{type}/`）。

> 若切换数据库后需要重新初始化，删除根目录 `data/app.lock` 锁文件即可再次触发初始化流程。

---

## 接口概览

接口按业务域组织在 `api` 模块：

| 分组 | 前缀 | 说明 |
| --- | --- | --- |
| 登录认证 | `/auth/**` | 登录、退出登录 |
| 验证码 | `/captcha/**` | 图形 / 短信验证码 |
| 系统管理 | `/sys/**` | 用户、角色、菜单管理 |
| 字典配置 | `/conf/dict/**` | 字典类型与字典数据管理 |
| 行政区划 | `/conf/region/**` | 行政区划数据 |
| 日志 | `/log/**` | 登录日志、操作日志 |

完整接口定义与参数说明请访问 Swagger UI：<http://127.0.0.1:9098/swagger-ui.html>（生产环境默认关闭）。

---

## 统一响应

所有标注了 `@ApiResponseBody` 的 Controller 返回值会被自动包装为统一响应体 `Result`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { },
  "timestamp": 1730000000000
}
```

- 业务接口只需返回业务数据，无需手动包装。
- `String` 返回值、已包装的 `Result` 均有特判处理，避免重复包装。

---

## 配置说明

核心配置集中在 `leaf-rbac-bootstrap/src/main/resources/`：

| 配置项 | 说明 |
| --- | --- |
| `server.port` | 应用端口，默认 `9098` |
| `app.db.type` | 数据库类型：`h2` / `mysql` / `postgresql`（开发缺省 `mysql`，生产缺省 `postgresql`，均支持 `DB_TYPE` 环境变量覆盖） |
| `app.demo-mode` | 演示模式开关（配合 `DemoModeInterceptor` 拦截写操作） |
| `app.captcha` | 验证码开关、类型、过期时间、位数 |
| `app.cache` | 缓存基础 TTL、随机偏移（防雪崩）、启动预加载开关 |
| `app.security.request-cache-limit` | 请求体缓存大小限制（默认 5MB） |
| `sa-token` | token 前缀、名称、有效期、多端登录、token 风格、JWT 密钥 |
| `bucket4j` | 多级限流规则（登录 / 验证码 / 系统接口 / 全局兜底） |
| `spring.redis` | Redis 连接与连接池（缓存、Sa-Token 集群化、分布式限流共用） |
| `thread.pool` | 异步线程池参数（异步日志/缓存事件监听） |
| `logbook` | 请求日志 + 敏感字段脱敏 |
| `management` | Actuator 监控（独立端口 `9099`）与链路追踪采样 |

生产环境（`application-prod.yml`）支持通过环境变量覆盖数据库与 Redis 连接（`DB_TYPE`、`MYSQL_HOST`/`MYSQL_PORT`/`MYSQL_DATABASE`/`MYSQL_USERNAME`/`MYSQL_PASSWORD`、`POSTGRESQL_HOST`/`POSTGRESQL_PORT`/`POSTGRESQL_DATABASE`/`POSTGRESQL_USERNAME`/`POSTGRESQL_PASSWORD`、`REDIS_HOST` 等），用于容器化部署。

### 生产建议

- 生产环境默认关闭 Swagger 文档（`springdoc.enabled: false`）。
- Actuator 独立端口 `9099`，仅暴露 `health,info,metrics,loggers`。
- 建议将 bucket4j 限流与 Sa-Token 均接入 Redis，实现多实例集群一致性。
- 日志按日期 + 大小滚动（100MB × 180 天），留痕可追溯。

---

## 部署方案

部署的核心思路：**一套代码、两种介质（环境变量 + `.env` 文件）、三个数据库（H2 / MySQL / PostgreSQL）**。应用在运行时通过环境变量注入敏感连接信息，生产与开发共用同一份编译产物。

### 产物与运行环境

- **编译产物**：`leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar`（`finalName` 由父 POM 的 `revision=1.0.0` 决定，可 `-Drevision=...` 覆盖）。
- **运行环境**：仅需 **JDK 21+**，无需安装 Maven（产物为可执行 fat jar）。H2 模式零外部中间件依赖。
- **可执行性**：内嵌 Tomcat，`java -jar` 即跑；启用优雅停机（`server.shutdown: graceful`，最长等待 60s）。

### 环境变量清单

以下是全部支持环境变量注入的配置项。变量缺省时回退到 yml 内默认值，因此**不配置也能启动**（但生产环境敏感信息请务必显式注入）。

| 变量 | 作用 | 默认值
| --- | --- | --- |
| `DB_TYPE` | 数据库类型 `h2` / `mysql` / `postgresql` | `mysql`（dev）／`postgresql`（prod） |
| `MYSQL_HOST` / `MYSQL_PORT` / `MYSQL_DATABASE` | MySQL 连接 | `192.168.2.3` / `3306` / `leaf_rbac_dev`（dev）· `leaf_rbac`（prod） |
| `MYSQL_USERNAME` / `MYSQL_PASSWORD` | MySQL 账号 | `root` / 明文默认值 |
| `POSTGRESQL_HOST` / `POSTGRESQL_PORT` / `POSTGRESQL_DATABASE` | PostgreSQL 连接 | `192.168.2.3` / `5432` / `leaf_rbac_dev`（dev）· `leaf_rbac`（prod） |
| `POSTGRESQL_USERNAME` / `POSTGRESQL_PASSWORD` | PostgreSQL 账号 | `postgres` / 明文默认值 |
| `REDIS_DATABASE` / `REDIS_HOST` / `REDIS_PORT` | Redis 连接 | `1` / `192.168.2.3` / `6379` |
| `REDIS_PASSWORD` | Redis 密码 | 明文默认值 |

> **安全提示**：默认 yml 内嵌了连接明文密码，生产务必用环境变量覆盖，且**不要提交 `.env`**（已加入 `.gitignore`）。

### 配置文件加载机制

- 三个 yml 按 Profile 加载：`application.yml`（环境无关）+ `application-{dev,prod}.yml`（可切换项），默认激活 `dev`。
- 环境变量可直接注入（`${MYSQL_HOST:默认值}` 语法），也可通过工作目录下的 **`.env` 文件**统一管理（模板见 `leaf-rbac-bootstrap/src/main/resources/.env.example`）：用 `spring.config.import: optional:file:.env[.properties]` 自动加载，`optional:` 保证文件缺失时不报错。
- **推荐做法**：`.env` 写在应用运行目录下（而非源码目录），一份对应一个部署环境，随容器/主机管理。

### 首次启动自动初始化

无论哪种数据库，**首次启动自动建库建表并导入初始数据**（脚本位于 `leaf-rbac-bootstrap/src/main/resources/sql/{type}/`）：

- 初始化完成后写入运行目录下的 `data/app.lock` 锁文件；
- 之后启动检测到锁文件则跳过初始化；
- **删除 `data/app.lock` 可重新触发初始化**（数据库会被重建/重置，操作前请注意备份）。

### 数据库选型建议

| 场景 | 推荐 | 说明 |
| --- | --- | --- |
| 本地开发 / 演示 | **H2** | 零依赖，文件库落在 `./data/`，`MODE=MySQL` 兼容 MySQL 语法 |
| 生产单机 / 中小规模 | **MySQL** | 生态成熟，运维成本低 |
| 生产多实例 / 高并发 | **PostgreSQL** | 事务与并发控制更强，适合集群化 |

> 多实例部署（集群）时：**Sa-Token token 存储、Spring Cache 缓存、Bucket4j 分布式限流均需接入 Redis**（`bucket4j.cache-to-use: redis-lettuce`），否则各实例状态不一致。

### 部署步骤

#### 1. 方式一：打包 jar 直接部署（推荐）

```bash
# Windows
.\mvnw.cmd clean package

# Linux / macOS
./mvnw clean package
```

产物：`leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar`

**开发 (零配置)**：
```bash
java -jar leaf-rbac-1.0.0.jar
# 默认激活 dev Profile，数据库缺省 mysql（可由 DB_TYPE 切换为 h2 / postgresql），Redis(默认地址)，自动建库建表
```

**开发 (H2 零外部依赖)**：
```bash
java -jar leaf-rbac-1.0.0.jar --DB_TYPE=h2
# 无需安装任何中间件即可运行
```

**生产 (MySQL + 环境变量)**：
```bash
java -jar leaf-rbac-1.0.0.jar \
  --spring.profiles.active=prod \
  --DB_TYPE=mysql \
  --MYSQL_HOST=10.0.0.10 \
  --MYSQL_PORT=3306 \
  --MYSQL_DATABASE=leaf_rbac \
  --MYSQL_USERNAME=leaf \
  --MYSQL_PASSWORD='***' \
  --REDIS_HOST=10.0.0.11 \
  --REDIS_PORT=6379 \
  --REDIS_PASSWORD='***'
```

或借助 `.env` 文件（推荐，避免命令过长 / 明文暴露在进程列表）：
```bash
# 运行目录下创建 .env，填入连接信息
java -jar leaf-rbac-1.0.0.jar --spring.profiles.active=prod
```

#### 2. 方式二：使用 `spring-boot:run`（开发调试）

```bash
# 先整体 install 以便依赖模块被 reactor 解析
.\mvnw.cmd install
.\mvnw.cmd spring-boot:run -pl leaf-rbac-bootstrap
```

### 端口与对外暴露

| 端口 | 用途 | 建议 |
| --- | --- | --- |
| `9098` | 应用主端口（业务 API） | 对外暴露，置于反向代理 / 负载均衡之后 |
| `9099` | Actuator 管理端口 | **仅内网访问**，勿暴露公网 |

生产默认已关闭 Swagger（`springdoc.*.enabled: false`）；Actuator 仅开放 `health,info,metrics,loggers` 且 `read_only`。

### 验证部署

```bash
# 健康检查（独立管理端口）
curl http://127.0.0.1:9099/actuator/health
# 期望返回 {"status":"UP", ...}

# 应用信息
curl http://127.0.0.1:9099/actuator/info
```

访问应用入口并登录（默认账密 `sadmin / 123456`）确认接口正常；查看 `logs/leaf-rbac_{profile}.log` 确认链路日志与审计日志均已落盘。

---

## 许可证

[Apache License 2.0](./LICENSE)（以仓库 LICENSE 文件为准）
