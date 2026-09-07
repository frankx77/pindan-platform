# 校园拼单/组队平台（Pindan Platform）— 组号 11

面向高校学生的拼单/组队平台：学生发布拼单 → 他人申请 → 团长审批 → 满员自动成团，
**成团瞬间团长与成员微信号互见**（未成团全程保密）。含 H5 移动端 + 管理后台。

- 课程：软件课程设计 I（南京理工大学）· 组号 11 · 成员：许以勒（后端+数据库）、何锐旸（前端+文档）· 指导教师：周涛
- 技术栈：Spring Boot 3.2.12 + MyBatis（注解 SQL）+ Vue 3.4 + Vant 4 + MySQL 8 + Apache POI + SpringDoc
- 课程红线合规：状态机为手写 if-else（无框架）、Controller 无业务逻辑、无物理删除（删=改 status）、无 JPA、消息用轮询（无 WebSocket）

---

## 一、环境要求（新机器只需装这三样）

| 依赖 | 版本 | 说明 |
|---|---|---|
| JDK | 17+ | 后端运行（`java -version` 验证） |
| Maven | 3.8+ | 后端构建（`mvn -v` 验证） |
| Node.js | 18+ | 前端构建（`node -v` 验证） |
| Docker Desktop | 最新版 | **推荐**，用于一键启动 MySQL（可用本机 MySQL 8 替代，见 §四·备选） |

## 二、获取代码与目录结构

```bash
git clone https://github.com/frankx77/pindan-platform.git
cd pindan-platform
```

```
├── docker-compose.yml      # MySQL 一键启动（重要）
├── README.md               # 本教程
├── sql/                    # init.sql 建表 + demo-data.sql 演示数据（幂等）
├── backend/                # Spring Boot 后端（Controller/Service/Mapper 三层 + 手写状态机）
├── frontend/               # Vue 3 + Vant 4 移动端（npm 安装依赖后启动）
├── screenshots/            # 11 张实测截图（H5 7 + 管理后台 3 + 全流程 1）
├── showcase/               # 展示网站（纯静态，双击 index.html 即可看）
└── 验收自测清单.md          # A~J 46 项功能验收（全部通过）
```

## 三、快速启动（三步 5 分钟跑起来）

### 第 1 步：启动数据库并初始化数据

```bash
# 1.1 一键启动 MySQL 容器（映射到本机 3307 端口，账号 root / pindan123）
docker compose up -d

# 1.2 初始化建表 + 演示数据（两条都要跑；命令中的 utf8mb4 参数必须保留）
docker exec -i pindan-mysql mysql --default-character-set=utf8mb4 -uroot -ppindan123 \
  < sql/init.sql
docker exec -i pindan-mysql mysql --default-character-set=utf8mb4 -uroot -ppindan123 \
  < sql/demo-data.sql

# 1.3 验证：应显示 10 用户 / 16 拼单 / 18 申请 / 12 消息 / 6 日志
docker exec pindan-mysql mysql --default-character-set=utf8mb4 -uroot -ppindan123 pindan \
  -e "SELECT (SELECT COUNT(*) FROM user) users,(SELECT COUNT(*) FROM groupon) groupons,(SELECT COUNT(*) FROM join_request) jrs;"
```

### 第 2 步：启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run        # 看到 "Started PindanApplication" 即为成功
```

自检：`curl http://127.0.0.1:8080/api/health` 应返回 `{"code":200,...}`；
接口文档：http://127.0.0.1:8080/swagger-ui/index.html

### 第 3 步：启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev                # 看到 Local: http://localhost:5173/ 即为成功
```

浏览器访问 **http://localhost:5173**（前端代理已转发 /api 到 8080，勿直开 8080）。

> 若本机 3306 端口已被**本机安装的 MySQL 服务**占用，属正常——本项目统一走 **3307**（compose 已映射、后端连接串已配好），无需任何修改。

## 四、备选：不装 Docker，直接用本机 MySQL

1. 本机 MySQL 8 创建数据库与账号：
   ```sql
   CREATE DATABASE pindan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'root'@'localhost' IDENTIFIED BY 'pindan123';  -- 或使用你本机的 root 密码
   ```
2. 修改 `backend/src/main/resources/application.yml` 的 `url` 为你的端口/密码（若走 3306 请改回 `localhost:3306`）。
3. 执行 `mysql --default-character-set=utf8mb4 -uroot -p < sql/init.sql` 与 `demo-data.sql`。

## 五、测试账号

| 账号 | 密码 | 角色 | 说明 |
|---|---|---|---|
| admin | admin123 | 管理员 | 数据看板 / 拼单管理 / 用户管理 / 操作日志 |
| zhangsan | 123456 | 普通 | 团长，已发布 6 个拼单 |
| wangwu | abcdef | 普通 | 团长/成员 |
| freshA | 123456 | 普通 | 团长（拼单 15 已成团、拼单 16 招募中） |
| freshB | 123456 | 普通 | 成员 |

## 六、功能演示（5 分钟完整流程）

1. **打开**：http://localhost:5173 （未登录自动进登录页）
2. **登录**：zhangsan / 123456
3. **大厅**：默认「招募中」→ 分类筛选（水果/奶茶/电影票/拼车/其他）→ 关键词搜索 → 翻页 → 四色状态标签
4. **详情**：点招募中拼单 → 成员列表显示「🔒 拼团成功后即可查看彼此微信号」
5. **发布**：底部「发布」→ 标题/分类/人数（≥2）/截止时间（五列选择器）→ 发布成功回大厅
6. **申请 → 审批 → 成团**：freshB 登录申请 → 切回 zhangsan → 消息角标 → 审批「通过」→ **满员自动成团 → 微信号互见**
7. **消息与导出**：「消息」Tab 三类通知；「我的拼团」双 Tab + 「导出 Excel」
8. **管理后台**：admin 登录 → 数据看板（状态分布环图 + TOP5 分类柱图）→ 拼单管理强制下架 → **操作日志 Tab** → 用户管理禁用/解禁

### 手机端演示（答辩用）

- **同一 WiFi**（最稳）：手机访问 `http://<电脑局域网IP>:5173`（IP 用 `ipconfig` 查看，别用 172.x 虚拟网卡地址）
- **公网（脱离校园网）**：natapp 内网穿透——本项目隧道方案见 `演示指南补充.md`（authtoken 自备，`frontend/vite.config.js` 已放行 `.natappfree.cc` 域名）

### 展示网站（静态，零依赖）

`showcase/index.html` 双击打开即可（或 `python -m http.server` 后访问）——含 9 大区块：主视觉/核心价值/功能/截图/架构/数据亮点/团队。离线打开时字体与动画会降级（原引用了 Google Fonts 与 AOS CDN），不影响功能展示。

## 七、单元测试与打包

```bash
cd backend
mvn test                   # 27 个用例：状态机 15 + 登录注册 6 + 管理员治理 6
mvn package -DskipTests    # 产物 target/pindan-backend-0.0.1-SNAPSHOT.jar（java -jar 可运行）
```

## 八、常见问题

| 问题 | 处理 |
|---|---|
| 3306 端口被占用 | 正常（本机 MySQL 服务占 3306），本项目统一走 3307，无需改动 |
| 数据导入后中文乱码 | 导入命令**必须带 `--default-character-set=utf8mb4`**（本 README 已给全命令） |
| natapp 访问 429 | 免费隧道限流，等 1-2 分钟自动恢复；优先用局域网直连演示 |
| natapp 域名 404 | 隧道重启域名会变，重新 `docker logs <隧道容器名>` 取最新地址 |
| Swagger 打不开 | 确认后端已启动（第 2 步） |
| 前端 5173 被拒（Vite Host） | 外部域名访问需在 `vite.config.js` 的 `server.allowedHosts` 放行 |

## 九、关键设计说明（答辩要点）

1. **状态机（核心）**：拼单 `0 招募中 → 1 已成团（满员自动）/ 2 已结束（超时任务）/ 3 已取消`；申请 `0 待确认 → 1 已通过 / 2 已拒绝（取消/下架批量流转）/ 3 已退出`，全部在 `JoinRequestService` 手写 if-else + `@Transactional`；审批事务内二次校验防"满员超卖"。
2. **微信号隐私**：未成团时数据层置空，成团后仅团内互见。
3. **鉴权**：BCrypt + UUID Token（内存）+ 登录/管理员双层拦截器（401/403）。
4. **敏感操作留痕**：下架/禁用/解禁写 `sys_log`，后台「操作日志」Tab 可查。
5. **无 WebSocket**：消息 5 秒轮询（设计要求）；超时自动结束由 `@Scheduled`（每 5 分钟）完成。

## 十、交付文档索引

- 设计文档：`Software_KeShe/交付文档/11详细设计说明书（许以勒+何锐旸）.docx`
- 设计总报告（含个人小结×2）：`Software_KeShe/交付文档/11设计总报告（许以勒+何锐旸）.docx`
- 验收清单：`验收自测清单.md`（A~J 46 项全部勾选）
- 截图：`screenshots/`（11 张实测）
