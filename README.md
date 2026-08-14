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
- [快速开始](#快速开始)
- [接口概览](#接口概览)
- [统一响应](#统一响应)
- [配置说明](#配置说明)
- [运行部署](#运行部署)
- [项目目录](#项目目录)
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
| API 文档 | SpringDoc / Swagger UI（Knife4j 风格） |
| 缓存 | Spring Cache + Redis（Lettuce） |
| 限流 | Bucket4j（令牌桶，支持 Redis / Lettuce） |
| 工具库 | Hutool 5.8、Commons-Lang3 |
| 其他 | EasyCaptcha（验证码）、Ip2region（IP 定位）、UserAgentUtils、Logbook、Nashorn |

---

## 模块架构

工程采用 Maven 多模块结构，依赖关系呈单向递进：

```
┌─────────────────────────────────────────────┐
│  leaf-rbac-common     公共基础（常量/枚举/   │ ◄── 被所有模块依赖
│                       异常/工具/配置属性）    │
└─────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────┐
│  leaf-rbac-framework  框架核心（切面/过滤器/  │
│                       拦截器/全局异常/配置）  │
└─────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────┐
│  leaf-rbac-system     系统业务（实体/Mapper/  │
│                       Service/策略实现）      │
└─────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────┐
│  leaf-rbac-api        接口层（Controller/    │
│                       Request/Response）     │
└─────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────┐
│  leaf-rbac-bootstrap  启动模块（启动类/yml/   │
│                       数据库初始化）          │
└─────────────────────────────────────────────┘

   leaf-rbac-codegen    代码生成器（独立模块，不依赖业务模块）
```

### 各模块职责

| 模块 | 职责 | 关键子包 |
| --- | --- | --- |
| `common` | 常量、枚举、核心对象（`Result`）、异常、配置属性、通用工具 | `constants` `core` `enums` `exception` `properties` `util` |
| `framework` | 统一响应、全局异常、AOP 切面、过滤器、拦截器、事件、缓存、自定义校验、用户上下文 | `advice` `annotation` `aspect` `filter` `handler` `event` `context` `validator` `config` |
| `system` | 实体、Mapper、Service、DTO、MapStruct 转换器、登录/验证码策略、Sa-Token 权限装配 | `entity` `mapper` `service` `dto` `converter` `strategy` `security` |
| `api` | Controller、请求/响应对象、DTO↔VO 转换、Swagger 配置 | `controller` `request` `response` `converter` `config` |
| `bootstrap` | 应用启动入口、环境配置、动态数据源、数据库初始化 | `config` `initializer` `condition` |
| `codegen` | MyBatis-Flex 代码生成器 | — |

**分层约定**：`system` 层暴露 `DTO`（领域对象），`api` 层持有网络对象（`request`/`response`），通过两级 MapStruct `converter` 完成转换，实现依赖方向单一、实体不对外暴露。

---

## 快速开始

### 环境要求

- JDK **21+**
- Maven **3.8+**（或直接使用仓库自带的 `mvnw` / `mvnw.cmd`）

### 启动（开发模式）

默认开发环境使用 **H2 内存/文件数据库（`app.db.type: h2`）**，无需额外安装任何数据库中间件，首次启动自动完成建库建表与数据初始化：

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

修改 `leaf-rbac-bootstrap/src/main/resources/application-dev.yml` 中的 `app.db.type`：

```yaml
app:
  db:
    type: mysql        # 可选：h2 / mysql / postgresql
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
| `app.db.type` | 数据库类型：`h2` / `mysql` / `postgresql`（默认 `h2`） |
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

生产环境（`application-prod.yml`）支持通过环境变量覆盖数据库与 Redis 连接（`DB_TYPE`、`MYSQL_URL`、`REDIS_HOST` 等），用于容器化部署。

---

## 运行部署

### Docker Compose

仓库 `docker/` 目录提供了完整的容器化编排（应用 + 可选托管数据库 + Redis）：

```bash
cd docker
# 使用托管数据库：DB_MODE=managed 并指定 DB_TYPE
DB_TYPE=mysql DB_MODE=managed docker compose up -d
# 外部数据库：DB_MODE=external REDIS_MODE=external 并传入连接环境变量
docker compose up -d
```

- 应用镜像：`codesensi/leaf-rbac:latest`
- 端口映射：`9098:9098`
- 健康检查：`GET /actuator/health`
- 数据与日志通过 volume 持久化到宿主机 `/docker/leafrbac/{data,logs}`

### 生产建议

- 生产环境默认关闭 Swagger 文档（`springdoc.enabled: false`）。
- Actuator 独立端口 `9099`，仅暴露 `health,info,metrics,loggers`。
- 建议将 bucket4j 限流与 Sa-Token 均接入 Redis，实现多实例集群一致性。
- 日志按日期 + 大小滚动（100MB × 180 天），留痕可追溯。

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
├── leaf-rbac-codegen/      # 代码生成器
├── docker/                 # Dockerfile 与 Compose 编排
├── data/                   # 运行数据（数据库文件、app.lock 锁文件）
├── logs/                   # 运行日志
└── pom.xml                 # 父 POM（版本与依赖统一管理）
```

---

## 许可证

[Apache License 2.0](./LICENSE)（以仓库 LICENSE 文件为准）
