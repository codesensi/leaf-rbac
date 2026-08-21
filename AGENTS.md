# AGENTS.md — leaf-rbac 开发规范

一叶后台管理系统：纯净的 RBAC 权限管理脚手架 · Spring Boot 3 多模块后端工程。

> 本文档是给参与开发的编码代理（Agent）与开发者的约定文件，随每次会话加载。保持精简精确；具体路径见下文。
> **安全提醒**：`application-dev.yml` / `application-prod.yml` 硬编码了数据库与 Redis 的明文密码，请勿提交到公开仓库（`.gitignore` 已忽略 `.env` 等本地文件，但 **yml 未忽略**）。敏感连接信息用 `${ENV_VAR}` 环境变量注入。

## Project

- 定位：基于 RBAC 的后台管理脚手架，聚焦「认证鉴权 + 系统管理」，把日志、限流、鉴权、脱敏、统一响应、链路追踪等横切关注点全部收敛到 `framework` 层，业务模块只关心实体与接口。
- 技术栈：Java 21 · Spring Boot 3.5.16 · MyBatis-Flex 1.11.7 · Sa-Token 1.45.0（starter + Redis 模板 + JWT）· MapStruct 1.6.3 · Lombok 1.18.46 · Hutool 5.8 · Commons-Lang3 3.20 · Bucket4j 0.13（Redis/Lettuce 分布式令牌桶）· Redis(Lettuce) · SpringDoc 2.8（Swagger UI）。
- 入口：`leaf-rbac-bootstrap/src/main/java/cn/codesensi/leaf/rbac/LeafRbacBootstrapApplication.java`（`@SpringBootApplication(scanBasePackages="cn.codesensi.leaf.rbac")`）。
- 端口：主业务端口 `9098`；Actuator 独立管理端口 `9099`（仅暴露 `health,info,metrics,loggers`，`read_only`）。默认账密 `sadmin / 123456`。
- 数据库：开发缺省 **MySQL**（`app.db.type: ${DB_TYPE:mysql}`）、生产缺省 **PostgreSQL**（`${DB_TYPE:postgresql}`）；用 `DB_TYPE` 环境变量一键切换 `h2`/`mysql`/`postgresql`，其中 H2 零外部依赖、适合本地开发演示。建表/初始数据脚本在 `leaf-rbac-bootstrap/src/main/resources/sql/{type}/init_ddl.sql`、`init_dml.sql`。
- 首次启动自动建库建表并初始化（`DatabaseInitializer` + `LockFileMissingCondition`），完成后写运行目录 `data/app.lock` 防止重复初始化；**删除 `data/app.lock` 可重新初始化**（会重建/重置数据库，操作前备份）。
- 模块划分（5 个）：`common → framework → system → api → bootstrap`，依赖单向递进；`leaf-rbac-codegen` 已删除，勿再引用。

## Commands

> Windows 用 `.\mvnw.cmd`，Linux/macOS 用 `./mvnw`。本机已验证 Maven 3.9.16 + Java 21；全局 `mvn` 可能不可用，统一用 mvnw。

- 构建：`.\mvnw.cmd clean install`。**注意**：父 pom 的 surefire 已硬编码 `<skipTests>true</skipTests>`，`mvn test`/`install` 都不会真正运行单测，**无需**再加 `-DskipTests`。
- 启动（开发）：先 `.\mvnw.cmd install`，再 `.\mvnw.cmd spring-boot:run -pl leaf-rbac-bootstrap`（bootstrap 依赖其余模块，聚合 reactor 下先 install 依赖模块再运行；也可 `-am` 连带构建依赖）。
- 打包：`.\mvnw.cmd clean package` → 产物 `leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar`（finalName=`${project.parent.artifactId}-${revision}`，revision=1.0.0，可 `-Drevision=...` 覆盖）。
- 运行后：Swagger 文档 <http://127.0.0.1:9098/swagger-ui.html>

## Architecture

模块依赖单向：`common → framework → system → api → bootstrap`（各模块只依赖左邻，保持依赖方向单一）。

- **leaf-rbac-common** — 无 Spring 依赖的纯基础，被所有模块依赖：
  - `constants`：`AppConst`（应用常量）、`CacheConst`（缓存 key）、`RbacConst`、`ThreadConst`
  - `core`：`Result`（统一响应体）、`ResultCode`、`BaseEntity`（雪花主键 `snowFlakeId` + 逻辑删除 `del_flag`）
  - `enums`：`BaseEnum` + 业务枚举（`OperateType`/`LoginEventType`/`LoginType`/`CaptchaType`/`GenderEnum`/`MenuType`/`EnableEnum`/`YesEnum`/`SysFlagEnum`/`DelFlagEnum`/`SuccessEnum`）
  - `exception`：`BaseException` + 子类（`BusinessException`/`SystemException`/`ValidationException`/`AuthorizationException`）
  - `properties`：`AppProperties`/`AppDbProperties`/`AppCaptchaProperties`/`AppCacheProperties`/`AppSecurityProperties`/`AppRequestLogProperties`/`ThreadPoolProperties`
  - `util`：`CacheUtil`/`EnumUtil`/`ExceptionUtil`
- **leaf-rbac-framework** — 横切关注点（业务不可见）：
  - `advice`：`ApiResponseBodyAdvice` 统一响应包装
  - `annotation`/`aspect`：`@LogOperate`/`@LogLogin` 审计切面
  - `filter`：`TraceIdFilter`/`UserContextFilter`/`CacheRequestBodyFilter`（请求/响应日志由 **Logbook** 承担，`RequestLogFilter` 已废弃勿启用）
  - `interceptor`：`DemoModeInterceptor`（演示模式）/`UserContextInterceptor`
  - `handler`：`GlobalExceptionHandler` 统一异常转 `Result`
  - `event`：`LogLoginEvent`/`LogOperateEvent`/`CacheDictEvent`/`CacheRegionEvent`
  - `context`：`UserContext`/`UserContextHolder`；`registry`：`UserContextAccessorRegistry`/`MdcAccessorRegistry`
  - `config`：`WebMvcConfig`/`JacksonConfig`/`CacheConfig`/`Bucket4jLettuceConfig`/`ThreadPoolConfig`/`RestClientConfig`/`ValidatorConfig`
  - `validator`：`@Phone`/`@IdCard`/`@InEnum`；`listener`：`AppSaTokenListener`/`MybatisFlexListener`（其它内部生命周期处理）
- **leaf-rbac-system** — 业务领域（实体只在此层）：
  - `entity`：`SysUser`/`SysRole`/`SysMenu`/`SysUserRole`/`SysRoleMenu`/`ConfDict*`/`ConfRegion`/`Log*`
  - `mapper`/`service`+`impl`/`dto`；`converter`（entity↔DTO，MapStruct）
  - `strategy`：登录 `LoginStrategy`+`LoginStrategyFactory`（`AccountLoginStrategy`）、验证码 `CaptchaStrategy`+`CaptchaStrategyFactory`（`ImageCaptchaStrategy`/`SmsCaptchaStrategy`）
  - `security`：`StpInterfaceImpl` 装配权限/角色
  - `listener`：`LogLoginListener`/`LogOperateListener`（审计落库）、`CacheDictListener`/`CacheRegionListener`（缓存刷新）
- **leaf-rbac-api** — 网络层：
  - `controller`：`auth`（`/auth/**`：login/logout）、`captcha`（`/captcha`）、`system`（`/sys/**`：用户/角色/菜单）、`conf`（`/conf/dict/**`、`/conf/region/**`）、`log`（`/log/**`）
  - `request`/`response` 网络对象、`converter`（DTO↔VO，MapStruct）、`config`（`SwaggerConfig`）。**Controller 只处理 request/response，不直接触达 entity**
- **leaf-rbac-bootstrap** — 启动与装配：`LeafRbacBootstrapApplication`、`application*.yml`、`condition/LockFileMissingCondition`、`config/DynamicDataSourceConfig`（动态数据源）、`initializer/DatabaseInitializer`（建表+初始化）。

## Conventions

- **分层不越界**：`api` 层持有 `request`/`response`；`system` 层暴露 `DTO`；实体只出现在 `system`。跨层转换用两级 MapStruct `converter`（`system` 的 `XxxConverter` 做 entity↔DTO，`api` 的 `XxxConverter` 做 DTO↔VO）。Controller 返回类型是 response/VO。
- **统一响应**：Controller 类上加 `@ApiResponseBody`，返回值会被 `ApiResponseBodyAdvice` 自动包成 `Result{code,msg,data,timestamp}`；业务方法直接返回业务对象，不要手动包 `Result`。
- **统一异常**：不要到处 try/catch 后返回错误——抛业务异常（`BusinessException` 等 `BaseException` 子类），由 `GlobalExceptionHandler` 统一转换为错误 `Result`。
- **权限注解**：公开接口用 Sa-Token 的 `@SaIgnore`；受保护接口靠 Sa-Token 拦截器 + `StpUtil` 校验（`StpInterfaceImpl` 已装配权限/角色，鉴权时用它）。用户上下文从 `UserContextHolder` 取，**不要自行解析 token**。
- **审计日志**：写操作/敏感查询加 `@LogOperate(module=…, type=OperateType.…, desc=…, recordResult=…)`，登录/退出加 `@LogLogin(type=LoginEventType.…)`；通过 `framework/event` 发布事件 + `system/listener` **异步落库**，不要在 Service 内直接写日志表。
- **请求日志链路**：traceId 由 `TraceIdFilter` 生成/透传/清理（MDC `traceId`）；请求体缓存（供业务多读）由 `CacheRequestBodyFilter`；请求/响应体日志与敏感字段脱敏统一由 **Logbook**（`logbook.obfuscate`）承担——**禁止再启用 `RequestLogFilter`**（历史实现，已废弃，仅标 `// @Component` 未注册）。脱敏字段统一在 `logbook.obfuscate.json-body-fields` 维护（现含 `accessToken`/`password`/`oldPassword`/`newPassword`/`confirmPassword`）。
- **缓存**：字典/行政区划等热数据用 Spring Cache + 缓存事件（`CacheDictEvent`/`CacheRegionEvent`）+ `system/listener` 刷新；变更时**发布事件**而不是直改缓存。
- **扩展点用策略模式**：新增登录方式 → 实现 `LoginStrategy` 并注册到 `LoginStrategyFactory`；新增验证码 → 实现 `CaptchaStrategy` 并注册到 `CaptchaStrategyFactory`。
- **限流**：Bucket4j 规则集中在 `application-{dev,prod}.yml` 的 `bucket4j.filters`（登录按 IP、验证码按 IP、`/sys/.*` 按用户 ID、全局按 IP 兜底）；集群部署需 `cache-to-use: redis-lettuce` 接入 Redis 才能跨实例一致。
- **ORM 写法**：用 MyBatis-Flex 的 `queryChain()` + `TableDef` 静态字段（如 `SYS_USER.ALL_COLUMNS`）写查询；实体逻辑删除列 `del_flag`、主键雪花 `snowFlakeId` 由全局配置处理，**不要在 SQL 里手写这些**。
- **参数校验**：DTO/request 上使用 Bean Validation（`@Valid`/`@Validated`）与自定义校验标注（`@Phone`/`@IdCard`/`@InEnum`）。
- **配置分层**：环境无关参数进 `application.yml`；可切换项（db/redis/验证码/bucket4j/线程池等）进 `application-dev.yml`/`application-prod.yml`；生产用 `${ENV_VAR}` 环境变量注入敏感连接信息（`.env` 模板见 `leaf-rbac-bootstrap/src/main/resources/.env.example`）。
- **示例/脚手架生成代码**：`api/conf`、`api/log` 目录存在大量被 `//` 注释掉的 CRUD（如 `// @GetMapping("/list")`）——这些是骨架预留点，开通接口时按上文分层规范补齐 URL 与查询条件。

## Notes

- 待补充：数据权限（行级）、部门/岗位、文件上传、导入导出等扩展点的落地约定。

