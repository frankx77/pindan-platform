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

## 十、演示准备（手机扫码演示指南）

> 演示目标：手机扫二维码 → 3 秒打开首页 → 5 分钟走完 登录→发布→申请→审批成团→微信互见→消息→后台看板→导出。

### 10.1 演示机器侧准备（演示前 15 分钟执行）

启动顺序（每步等待上一步完成）：

```bash
# 1. Docker Desktop 启动后，拉起 MySQL 容器（端口 3307，本机 3306 可能被本地 MySQL 服务占用）
docker start pindan-mysql

# 2. 后端（看到 Started PindanApplication 才算好）
cd backend && mvn spring-boot:run

# 3. 前端（看到 Local: http://localhost:5173/ 即为就绪）
cd frontend && npm run dev

# 4. 自检（三条全 200 才合格）
curl -s http://127.0.0.1:8080/api/health          # 期望 {"code":200,...}
curl -s -o /dev/null -w "%{http_code}" http://localhost:5173/
curl -s http://localhost:5173/api/health          # 走 vite 代理再验一次后端
```

**演示路径二选一：**

**方案 A：同一 WiFi / 热点，局域网直连（推荐，稳定无限制）**
手机连电脑同一个 WiFi（或电脑开手机热点），手机访问：
```
http://<电脑局域网IP>:5173
```
电脑 IP 用 `ipconfig` 查看「无线局域网适配器」的 IPv4 地址（如 10.253.214.153，注意别用 172.x 虚拟网卡地址）。

**方案 B：natapp 公网穿透（老师手机不在同一网络时用）**
```bash
# 隧道（authtoken 在 natapp.cn 控制台获取；域名每次启动可能变化）
docker run -d --name pindan-tunnel -p 4040:4040 -e authtoken=<你的authtoken> natapp/natapp
# sidecar 转发（容器内 80 -> 宿主机 5173）
docker run -d --name tunnel-proxy --network container:pindan-tunnel \
  -v "<natapp/proxypy 目录>:/app" python:3.13-alpine python /app/proxy_container.py
# 取隧道地址
docker logs pindan-tunnel 2>&1 | grep "Tunnel established"
```
- 手机访问输出的 `http://xxxx.natappfree.cc`；**提前 1-2 分钟打开热身**（免费隧道有频率限制，见 10.3）
- 域名每次重启可能变化，演示前一天务必重新确认；`frontend/vite.config.js` 已配置 `allowedHosts: ['.natappfree.cc']`

### 10.2 手机侧操作（演示流程，5 分钟版）

1. **打开**：扫码/输入地址 → 3 秒内显示欢迎页（未登录会自动进入登录页）；
2. **登录**：`zhangsan/123456`（团长账号，演示后恢复现场可重刷 demo 数据）；
3. **大厅**：默认只显示「招募中」→ 示范分类筛选（水果/奶茶/电影票/拼车/其他）与关键词搜索、翻页、四色状态标签；
4. **浏览详情**：点一个招募中拼单 → 展示成员列表与「🔒 拼团成功后即可查看彼此微信号」（隐私设计）；
5. **发布拼单**：底部「发布」→ 填标题/分类/人数（≥2）/截止时间（5 列选择器）→ 发布成功回大厅（新拼单 1/N）；
6. **申请与审批成团**：另账号（如 `freshB/123456`）登录 → 申请刚发布的团 → 切回团长账号 → 消息角标提示 → 详情「通过」→ **满员自动成团** → 成员列表**微信号互见**；
7. **消息与导出**：「消息」Tab 看三类通知与角标；「我的拼团」切换我发布的/我参加的 → 点「导出 Excel」下载核对表头；
8. **管理后台**：`admin/admin123` 登录 → 「我的」页入口 → 数据看板两张图（状态分布环图 / TOP5 分类柱图）→ 拼单管理强制下架 → **操作日志 Tab 看到下架记录** → 用户管理禁用一个账号再解禁。

### 10.3 常见问题速查

| 现象 | 原因与处理 |
|---|---|
| 页面出现 `429` | natapp 免费隧道限流：等 1-2 分钟自动恢复；别在短时间狂刷新；演示优先用方案 A（局域网直连） |
| 详情页标题/分类乱码 | 数据库被非 utf8mb4 客户端写入过 → 重刷：`SET FOREIGN_KEY_CHECKS=0;` + mysql 命令**必须加 `--default-character-set=utf8mb4`** 执行 init.sql、demo-data.sql |
| 3306 连不上 | 本机 MySQL 服务占用 3306；本项目用 3307（容器映射与后端连接串均已配置） |
| natapp 域名 404/Web服务错误 | 隧道重启过域名变了：重新 `docker logs pindan-tunnel` 取值 |
| Word 打开文档报「试图打开文件时遇到错误」 | 若目录/文档重新生成时应用了目录占位脚本（pStyle=9 引用）——现在交付版已修复；Word 打开报告后**右键目录 → 更新域**（报告 docx 的目录区需更新一次生成） |
| 报告 docx 目录是空的 | 正常：目录字段未计算，Word/WPS 中右键目录 →「更新域」即可生成；或整个文档 Ctrl+A 后按 F9 |

### 10.4 演示数据恢复（每次演示前后建议执行）

```bash
# 恢复 10 用户/16 拼单/18 申请的干净演示状态（含修复乱码的固定参数）
{ echo "SET FOREIGN_KEY_CHECKS=0;"; cat sql/init.sql; echo "SET FOREIGN_KEY_CHECKS=1;"; } |
  docker exec -i pindan-mysql mysql --default-character-set=utf8mb4 -uroot -ppindan123
{ echo "SET FOREIGN_KEY_CHECKS=0;"; cat sql/demo-data.sql; echo "SET FOREIGN_KEY_CHECKS=1;"; } |
  docker exec -i pindan-mysql mysql --default-character-set=utf8mb4 -uroot -ppindan123
```

---

## 九、关键设计说明（答辩要点）

1. **状态机（核心）**：拼单状态 `0 招募中 → 1 已成团（满员自动）/ 2 已结束（超时定时任务）/ 3 已取消（团长或管理员）`；
   申请状态 `0 待确认 → 1 已通过 / 2 已拒绝（取消/拒绝时批量流转）`，全部在 `JoinRequestService` 手写 if-else + `@Transactional`。
2. **微信号隐私**：成团前成员微信在数据层置空，成团后查询返回（零泄露）。
3. **敏感操作留痕**：管理员下架/禁用/解禁全部写入 `sys_log`，后台「操作日志」Tab 可按操作人/类型查询。
4. **无 WebSocket**：消息通知采用 5 秒轮询（设计要求），未读角标实时更新。
