# AGENTS.md — leaf-rbac 开发规范

一叶后台管理系统：纯净的 RBAC 权限管理脚手架，Spring Boot 3 多模块后端工程。

> 本文档是给参与开发的编码代理（Agent）与开发者的约定文件，随每次会话加载。保持精简；具体路径见下文。
> **安全提醒**：`application-dev.yml` / `application-prod.yml` 中硬编码了数据库与 Redis 的明文密码，请勿提交到公开仓库（`.gitignore` 已忽略部分本地文件，但 yml 未忽略）。

## Project

- 定位：基于 RBAC 的后台管理脚手架，聚焦「认证鉴权 + 系统管理」，横切关注点收敛到 framework 层。
- 技术栈：Java 21、Spring Boot 3.5.16、MyBatis-Flex 1.11.7、Sa-Token 1.45.0、MapStruct 1.6.3、Lombok、Hutool、Bucket4j 限流、Redis(Lettuce)、SpringDoc。
- 入口：`leaf-rbac-bootstrap/src/main/java/.../LeafRbacBootstrapApplication.java`（`@SpringBootApplication(scanBasePackages="cn.codesensi.leaf.rbac")`）。
- 数据库：默认 H2（`app.db.type: h2`），支持 MySQL / PostgreSQL 一键切换；首次启动自动建库建表并初始化数据（见 `DatabaseInitializer` + `sql/{type}/init_ddl.sql`、`init_dml.sql`），完成后写 `data/app.lock` 锁文件防止重复初始化（删除该文件可重新初始化）。

## Commands

> Windows 下用 `.\mvnw.cmd`，Linux/macOS 用 `./mvnw`。本机已验证 Maven 3.9.16 + Java 21。（全局 `mvn` 可能不可用，统一用 mvnw。）

- 构建：`.\mvnw.cmd clean install`（**注意**：父 pom 的 surefire 已硬编码 `<skipTests>true</skipTests>`，`mvn test` 不会真正运行单测；不要再额外加 `-DskipTests` 也 OK）。
- 启动（开发，H2 零依赖）：先 `.\mvnw.cmd install`，再
  `.\mvnw.cmd spring-boot:run -pl leaf-rbac-bootstrap`
  （bootstrap 依赖其余模块，聚合 reactor 下先 install 依赖模块再运行；也可 `-am` 连带构建依赖）。
- 打包：`.\mvnw.cmd clean package` → 产物 `leaf-rbac-bootstrap/target/leaf-rbac-1.0.0.jar`（finalName=`${project.parent.artifactId}-${revision}`）。
- 运行后：Swagger 文档 <http://127.0.0.1:9098/swagger-ui.html>；默认账密 `sadmin / 123456`。

## Architecture

模块依赖单向：`common → framework → system → api → bootstrap`。

- **leaf-rbac-common** — 无 Spring 依赖的基础：常量、枚举、核心对象（`Result` 统一响应）、异常、配置属性（如 `AppDbProperties`）、工具类。
- **leaf-rbac-framework** — 横切关注点：`advice`（`ApiResponseBodyAdvice` 统一响应包装）、`annotation`/`aspect`（`@LogOperate`/`@LogLogin` 审计切面）、`filter`（TraceId/请求日志/用户上下文/缓存请求体）、`interceptor`（演示模式/用户上下文）、`handler`（`GlobalExceptionHandler`）、`event`、`validator`（`@Phone`/`@IdCard`/`@InEnum`）、`context`（`UserContext`/`UserContextHolder`）、`registry`、`config`、`util`。
- **leaf-rbac-system** — 业务领域：`entity`（`SysUser/SysRole/SysMenu/SysUserRole/SysRoleMenu/ConfDict*/ConfRegion/Log*`）、`mapper`、`service`/`impl`、`dto`、`converter`（entity↔DTO）、`strategy`（登录/验证码策略 + 工厂）、`security`（`StpInterfaceImpl` 装配权限/角色）、`listener`（事件落库/刷新缓存）。
- **leaf-rbac-api** — 网络层：`controller`（auth/captcha/conf/log/system）、`request`/`response`、`converter`（DTO↔VO）、`config`（`SwaggerConfig`）。**Controller 只处理 request/response，不直接触达 entity。**
- **leaf-rbac-bootstrap** — 启动与装配：启动类、`application*.yml`、`DynamicDataSourceConfig`、`DatabaseInitializer`。

## Conventions

- **分层不越界**：`api` 层持有 `request`/`response`；`system` 层暴露 `DTO`；实体只出现在 `system`。跨层转换用 MapStruct `converter`（`system` 的 `XxxConverter` 做 entity↔DTO，`api` 的 `XxxConverter` 做 DTO↔VO）。Controller 返回类型是 response/VO。
- **统一响应**：Controller 类上加 `@ApiResponseBody`，返回值会被 `ApiResponseBodyAdvice` 自动包成 `Result{code,msg,data,timestamp}`；业务方法直接返回业务对象，不要手动包 `Result`。
- **统一异常**：不要到处 try/catch 后返回错误，抛出业务异常，由 `GlobalExceptionHandler` 统一转换为错误 `Result`。
- **权限注解**：公开接口用 Sa-Token 的 `@SaIgnore`；受保护接口靠 Sa-Token 拦截器 + `StpUtil` 校验（`StpInterfaceImpl` 已实现权限/角色装配，鉴权时用它）。用户上下文从 `UserContextHolder` 取，不要自行解析 token。
- **审计日志**：写操作/敏感查询加 `@LogOperate(module=…, type=OperateType.…, desc=…, recordResult=…)`，登录/退出加 `@LogLogin(type=LoginEventType.…)`；通过 `framework/event` 的事件 + `system/listener` 异步落库，不要在 Service 内直接写日志表。
- **请求日志链路**：traceId 由 `TraceIdFilter` 生成/透传/清理（MDC `traceId`）；请求体缓存（供业务多读）由 `CacheRequestBodyFilter`；请求/响应体日志与敏感字段脱敏统一由 **Logbook**（`logbook.obfuscate`）承担——**禁止再启用 `RequestLogFilter`**（历史实现，已废弃，仅标 `// @Component` 未注册）。日志脱敏字段请统一在 `logbook.obfuscate.json-body-fields` 维护。
- **缓存**：字典/行政区划等热数据通过 Spring Cache + 缓存事件（`CacheDictEvent`/`CacheRegionEvent`）+ 监听器刷新，变更时发布事件而不是直改缓存。
- **扩展点用策略模式**：新增登录方式 → 实现 `LoginStrategy` 并注册到 `LoginStrategyFactory`；新增验证码 → 实现 `CaptchaStrategy` 并注册到 `CaptchaStrategyFactory`。
- **ORM 写法**：用 MyBatis-Flex 的 `queryChain()` + `TableDef` 静态字段（如 `SYS_USER.ALL_COLUMNS`）写查询；实体逻辑删除列 `del_flag`、主键雪花 `snowFlakeId` 由全局配置处理，不要在 SQL 里手写这些。
- **参数校验**：DTO/request 上使用 Bean Validation（`@Valid`/`@Validated`）与自定义校验标注（`@Phone`/`@IdCard`/`@InEnum`）。
- **配置**：环境无关参数进 `application.yml`；可切换项（db/redis/验证码等）进 `application-dev.yml`/`application-prod.yml`，生产用 `${ENV_VAR}` 环境变量注入敏感连接信息。
- **示例/脚手架生成代码**：`api/conf`、`api/log` 目录下存在大量被 `//` 注释掉的 CRUD（如 `// @GetMapping("/list")`）——这些是骨架预留点，开通接口时按上文分层规范补齐 URL 与查询条件。

## Notes

- 待补充：数据权限（行级）、部门/岗位、文件上传、导入导出等扩展点的落地约定。
