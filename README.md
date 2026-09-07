# 校园拼单/组队平台（Pindan Platform）— 组号 11

## 一、项目简介

面向高校学生的拼单/组队平台：学生发布拼单 → 他人申请 → 团长审批 → 满员自动成团，
**成团瞬间团长与成员微信号互见**（未成团全程保密）。包含 H5 移动端 + 管理后台。

- 成员：许以勒（924106840238）、何锐旸（924106840219）
- 指导教师：周涛
- 技术栈：Spring Boot 3.2.12 + MyBatis（注解 SQL）+ Vue 3.4 + Vant 4 + MySQL 8 + Apache POI + SpringDoc（Swagger）
- 课程红线合规：状态机为手写 if-else（无状态机框架）、Controller 无业务逻辑、无物理删除（删=改 status）、无 JPA

## 二、准备环境

| 依赖 | 版本 | 说明 |
|---|---|---|
| JDK | 17+ | 后端运行 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 18+ | 前端构建 |
| MySQL | 8.x | 数据库（本项目使用 Docker 容器 `pindan-mysql`，root/pindan123；**宿主机 3306 与本地 MySQL 服务冲突时容器映射 3307**，后端连接串对应修改） |

## 三、数据库初始化

```bash
# 若使用 Docker（推荐）：
docker start pindan-mysql

# 导入建表与演示数据（全新环境）：
mysql -uroot -ppindan123 < sql/init.sql
mysql -uroot -ppindan123 < sql/demo-data.sql
```

或通过 `docker exec -i pindan-mysql mysql -uroot -ppindan123 < sql/init.sql` 导入。
> 如遇 DROP TABLE 外键报错，可先执行 `SET FOREIGN_KEY_CHECKS=0;` 再导入（init.sql 已按依赖顺序建表）。

## 四、启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

- Swagger 接口文档：http://localhost:8080/swagger-ui/index.html
- 打包可执行 jar：`mvn package -DskipTests`（产物 `backend/target/pindan-backend-0.0.1-SNAPSHOT.jar`，`java -jar` 即可运行）
  > 说明：fat jar 约 51MB，超出课程 20M 提交上限，故本源码包未附 jar，请按上述命令自行构建。

## 五、启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 **http://localhost:5173**（前端代理转发 /api 到后端 8080，勿直开 8080）。

## 六、测试账号

| 账号 | 密码 | 角色 | 说明 |
|---|---|---|---|
| admin | admin123 | 管理员 | 数据看板 / 拼单管理 / 用户管理 / 操作日志 |
| zhangsan | 123456 | 普通 | 团长，已发布 6 个拼单 |
| wangwu | abcdef | 普通 | 团长/成员 |
| freshA | 123456 | 普通 | 团长（拼单 15 已成团、拼单 16 招募中） |
| freshB | 123456 | 普通 | 成员 |

## 七、单元测试

```bash
cd backend
mvn test        # 27 个用例：状态机 15 + 登录注册 6 + 管理员治理 6
```

## 八、目录结构

```
├── sql/                  # init.sql 建表 + demo-data.sql 演示数据（幂等）
├── backend/              # Spring Boot 后端（Controller/Service/Mapper 三层 + 手写状态机）
├── frontend/             # Vue 3 + Vant 4 移动端（含 dist 构建产物）
├── screenshots/          # 11 张实测截图（H5 7 + 管理后台 3 + 全流程 1）
└── 验收自测清单.md        # A~J 46 项功能验收（2026-08-28 全部走查通过）
```

## 九、关键设计说明（答辩要点）

1. **状态机（核心）**：拼单状态 `0 招募中 → 1 已成团（满员自动）/ 2 已结束（超时定时任务）/ 3 已取消（团长或管理员）`；
   申请状态 `0 待确认 → 1 已通过 / 2 已拒绝（取消/拒绝时批量流转）`，全部在 `JoinRequestService` 手写 if-else + `@Transactional`。
2. **微信号隐私**：成团前成员微信在数据层置空，成团后查询返回（零泄露）。
3. **敏感操作留痕**：管理员下架/禁用/解禁全部写入 `sys_log`，后台「操作日志」Tab 可按操作人/类型查询。
4. **无 WebSocket**：消息通知采用 5 秒轮询（设计要求），未读角标实时更新。
