# Pindan · 校园拼单/组队平台 · 官方展示站

> 把「找人凑单」从微信群接龙，升级为规范化、可视化、可互信的线上流程。

本目录是 **Pindan Platform**（校园拼单/组队平台）的官方展示官网，用于答辩演示、项目展示与团队作品集入口。

---

## ✨ 视觉风格

- **Pop Art 波普艺术** · 与主程序 H5 App 一脉相承
- **配色**：黄 `#FFDD00` / 粉 `#FF69B4` / 电光蓝 `#00BFFF` / 紫 `#A486FF` + 黑色硬边描边
- **字体**：标题用漫画体（Impact / Bangers），正文用 PingFang SC
- **风格元素**：粗黑描边、硬阴影（`8px 8px 0 #000`）、Ben-Day 双层网点、爆炸贴纸

## 🛠️ 技术栈

- **纯 HTML + CSS + 原生 JS**（零构建工具）
- **AOS 2.3.4**（CDN 引入）做滚动入场动画
- **Google Fonts** 加载 Bangers 字体
- 适配桌面 / 平板 / 手机三端

## 📂 目录结构

```
showcase/
├── index.html                # 主入口（单页 10 个 section）
├── 404.html                  # GitHub Pages 404 兜底
├── README.md                 # 本文件
├── .nojekyll                 # 关闭 Jekyll（避免下划线开头的文件被忽略）
├── assets/
│   ├── css/
│   │   ├── pop-art-base.css  # 设计令牌 + 通用类
│   │   ├── showcase.css      # 桌面端布局
│   │   └── responsive.css    # 平板 + 手机适配
│   ├── js/
│   │   └── main.js           # 导航、tab、counter、拖拽滚动
│   └── images/
│       ├── screenshots/mobile/   # 7 张 H5 截图
│       ├── screenshots/admin/    # 3 张后台截图
│       └── flow/                 # 1 张完整业务流截图
```

## 🚀 本地预览

任选一种方式起一个静态服务器（直接双击 `index.html` 也能看，但部分浏览器对本地 `file://` 协议下的资源加载有限制）：

```bash
# 方式 1：Python 3
cd showcase
python -m http.server 8000
# 浏览器打开 http://localhost:8000

# 方式 2：Node.js
cd showcase
npx serve .

# 方式 3：PHP
cd showcase
php -S localhost:8000
```

## 🌐 部署到 GitHub Pages

### 方案 A：独立仓库（推荐 · 干净独立）

1. **新建 GitHub 仓库**
   - 仓库名：`pindan-showcase`（或 `username.github.io` 如果想要顶级域名）
   - 可见性：Public（GitHub Pages 免费版需要公开仓库）

2. **推送代码**
   ```bash
   cd showcase
   git init
   git add .
   git commit -m "feat: 初始化 Pindan 官方展示站"
   git branch -M main
   git remote add origin https://github.com/<你的用户名>/pindan-showcase.git
   git push -u origin main
   ```

3. **开启 Pages**
   - 进入仓库 → Settings → Pages
   - Source: `Deploy from a branch`
   - Branch: `main` / `/ (root)`
   - 点击 Save

4. **访问地址**
   `https://<你的用户名>.github.io/pindan-showcase/`

### 方案 B：现有仓库的 `docs/` 子目录（不开新仓库）

1. 把 `showcase/` 内的全部内容（**不是文件夹本身**）复制到 `pindan-platform/docs/`
   ```bash
   cp -r pindan-platform/showcase/* pindan-platform/docs/
   ```

2. 推送到现有仓库的 main 分支

3. 开启 Pages
   - Source: `Deploy from a branch`
   - Branch: `main` / `/docs`

4. **访问地址**
   `https://<你的用户名>.github.io/<仓库名>/`

### 备选：Vercel / Netlify（国内访问更友好）

如果 GitHub Pages 国内访问抽风严重，也可以一键部署到：

- **Vercel**：<https://vercel.com/new> → Import Git Repository → 框架选择「Other」→ 输出目录留空
- **Netlify**：<https://app.netlify.com/drop> → 直接拖拽 `showcase` 文件夹到页面

## 📋 内容来源

展示站的全部文案直接抽取自团队已有的 PRD 文档，无需重新撰写：

| 内容 | 来源文件 |
|---|---|
| 一句话定位 | `pindan-platform/AGENTS.md` |
| 三大核心价值 | `项目建设方案（PRD）.md` §1.1 |
| 四大应用场景 | `项目建设方案（PRD）.md` §五 |
| 功能规划 | `项目建设方案（PRD）.md` §六 |
| 业务规则 | `项目建设方案（PRD）.md` §七 |
| 技术栈 | `项目建设方案（PRD）.md` §九 |
| 实施里程碑 | `项目建设方案（PRD）.md` §十 |
| 11 张截图 | `pindan-platform/screenshots/` |

## ✏️ 自定义修改指引

如果需要修改内容但不确定在哪里改：

| 想改的东西 | 改哪个文件 |
|---|---|
| 页面文字 | `index.html` |
| 颜色 | `assets/css/pop-art-base.css` 顶部 `:root` 变量 |
| 间距 / 阴影 | `assets/css/showcase.css` |
| 手机适配 | `assets/css/responsive.css` |
| 交互行为（tab / counter / 菜单） | `assets/js/main.js` |
| 添加新截图 | 放入 `assets/images/screenshots/mobile/` 或 `admin/`，然后在 `index.html` 加 `.screenshot-tile` |

## ⚠️ 注意事项

1. **不要删除 `.nojekyll` 文件** —— 没有它，GitHub Pages 会启动 Jekyll 处理，下划线开头的目录会被忽略。
2. **截图是中文 UI**，海外环境字体可能略不同，但截图本身是图片无影响。
3. **第一次部署后可能需要 1~2 分钟生效**，刷新几次就好。

## 📜 License

项目内部物料，仅用于答辩与作品集展示。
