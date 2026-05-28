# MusicOnline 项目部署指南

> 本指南适用于将 MusicOnline 项目分别载入 **IntelliJ IDEA**（后端）、**WebStorm**（前端）和 **Navicat**（数据库），并完成首次启动。

---

## 目录

1. [项目结构](#项目结构)
2. [Step 1 — Navicat 数据库配置](#step-1--navicat-数据库配置)
3. [Step 2 — IDEA 打开后端项目](#step-2--idea-打开后端项目)
4. [Step 3 — WebStorm 打开前端项目](#step-3--webstorm-打开前端项目)
5. [Step 4 — 启动运行](#step-4--启动运行)
6. [演示账号](#演示账号)
7. [常见问题](#常见问题)

---

## 项目结构

```
musiconline/
├── backend/          ← Spring Boot 后端 (IDEA 打开此目录)
│   ├── .idea/        ← 已预置运行配置
│   ├── pom.xml
│   └── src/
├── frontend/         ← 纯 HTML/CSS/JS 前端 (WebStorm 打开此目录)
│   ├── .idea/        ← 已预置 WebStorm 配置
│   ├── index.html
│   ├── pages/
│   ├── css/
│   └── js/
└── database/
    ├── init_mysql.sql     ← 完整建表 + 初始数据
    └── seed_data.sql      ← 仅数据（表已存在时用此文件）
```

---

## Step 1 — Navicat 数据库配置

### 1.1 连接 MySQL

1. 打开 **Navicat Premium**
2. 点击左上角 **"连接"** → 选择 **MySQL**
3. 填写连接信息：

   | 字段       | 值                        |
   |----------|---------------------------|
   | 连接名     | MusicOnline（随意命名）    |
   | 主机       | `127.0.0.1`               |
   | 端口       | `3306`                    |
   | 用户名     | `root`                    |
   | 密码       | `A883709A`                |

4. 点击 **"测试连接"**，看到"连接成功"后点 **确定**

### 1.2 导入数据库

> ✅ **数据库 `music_online` 已自动创建**，所有表和数据已导入完成。
> 你在 Navicat 中刷新连接后，可直接看到：

| 数据库名      | 表                                   | 数据量           |
|-------------|--------------------------------------|-----------------|
| music_online | mo_users / mo_vinyls / mo_audit_log  | 6用户 / 18黑胶 / 4审计 |

### 1.3 在 Navicat 中查看表数据

1. 展开左侧 **MusicOnline 连接** → **music_online** → **表**
2. 双击 **mo_users** 可看到 6 个用户（2 个管理员）
3. 双击 **mo_vinyls** 可看到 18 张黑胶唱片

### 1.4 手动重新导入（如需重置数据）

1. 在 Navicat 右键 **music_online** → **运行 SQL 文件**
2. 选择文件：`musiconline/database/seed_data.sql`
3. 点击**开始**

---

## Step 2 — IDEA 打开后端项目

### 2.1 打开项目

1. 启动 **IntelliJ IDEA 2024.2.1**
2. 选择 **File → Open**（或欢迎界面的 Open）
3. 定位到：`C:\Users\yy\WorkBuddy\20260423171730\musiconline\backend`
4. 点击 **OK**，选择 **"Trust Project"**

### 2.2 配置 JDK

1. 菜单 **File → Project Structure**（快捷键 `Ctrl+Alt+Shift+S`）
2. 左侧选 **SDK**
3. 点 **"+"** → **"Add JDK"**
4. 路径填：`D:\新建文件夹\IntelliJ IDEA 2024.2.1\jbr`（IDEA 自带的 JBR 21）
5. 命名为 `jbr-21`，点 **OK**
6. **Project** → **Project SDK** 选 `jbr-21`，**Language Level** 选 `17`
7. 点 **Apply → OK**

### 2.3 加载 Maven 依赖

1. 右上角会弹出 **Maven** 提示，点 **"Load Maven Project"**
2. 或者打开右侧 **Maven** 面板 → 点刷新图标
3. 等待依赖下载完成（第一次约 2-5 分钟）

### 2.4 运行项目

**方法一（推荐）：使用预置运行配置**
1. 右上角运行配置下拉菜单已有 **"MusicOnlineApplication"**
2. 点绿色三角 ▶ 运行

**方法二：直接运行主类**
1. 打开 `src/main/java/com/musiconline/MusicOnlineApplication.java`
2. 点击类名左侧绿色三角 → **Run MusicOnlineApplication**

**启动成功标志：**
```
Started MusicOnlineApplication in X.XXX seconds
Tomcat started on port 8080
```

---

## Step 3 — WebStorm 打开前端项目

> 前端是纯静态 HTML/CSS/JS，**不需要 Node.js 或构建工具**。
> 运行后端后，前端由 Spring Boot 自动托管，直接访问 `http://localhost:8080` 即可。

### 3.1 在 WebStorm 中查看/编辑前端代码

1. 启动 **WebStorm 2025.2.4**
2. **File → Open**
3. 定位到：`C:\Users\yy\WorkBuddy\20260423171730\musiconline\frontend`
4. 点 **OK**，选择 **"Trust Project"**

### 3.2 前端文件结构

```
frontend/
├── index.html           ← 入口（自动跳转）
├── pages/
│   ├── home.html        ← 公开首页（含搜索）
│   ├── login.html       ← 登录/注册页
│   ├── search.html      ← 搜索结果页
│   ├── vinyl-detail.html← 唱片详情页
│   ├── dashboard.html   ← 用户仪表盘（需登录）
│   ├── my-vinyls.html   ← 我的唱片管理
│   ├── add-vinyl.html   ← 添加唱片
│   ├── profile.html     ← 个人资料
│   └── admin.html       ← 管理员面板（仅 ADMIN）
├── css/
│   ├── variables.css    ← 颜色/间距变量
│   ├── base.css
│   ├── components.css
│   ├── layout.css
│   └── login.css
└── js/
    ├── api-base.js      ← API 请求封装
    ├── auth.js          ← 登录/注册逻辑
    ├── app.js           ← 公共工具函数
    └── three-bg.js      ← 粒子背景动画
```

### 3.3 预览页面

**方法：** 后端启动后，在 WebStorm 内打开任意 HTML 文件，点击右上角浏览器图标，
或直接在浏览器访问 `http://localhost:8080/pages/home.html`

---

## Step 4 — 启动运行

### 启动顺序

```
1. MySQL 服务已运行（当前已自动运行 ✅）
        ↓
2. 在 IDEA 中运行 MusicOnlineApplication
        ↓
3. 浏览器访问 http://localhost:8080
```

### 访问地址

| 页面         | 地址                                        |
|------------|---------------------------------------------|
| 网站首页     | http://localhost:8080                       |
| 公开首页     | http://localhost:8080/pages/home.html       |
| 登录/注册    | http://localhost:8080/pages/login.html      |
| 搜索唱片     | http://localhost:8080/pages/search.html     |
| 管理员面板   | http://localhost:8080/pages/admin.html      |
| API 根路径  | http://localhost:8080/api/public/vinyls     |

---

## 演示账号

> 所有账号密码均为：`password`

| 邮箱                      | 用户名         | 角色      | 权限           |
|--------------------------|--------------|-----------|--------------|
| admin@musiconline.com    | admin1       | ADMIN     | 全部权限        |
| admin2@musiconline.com   | admin2       | ADMIN     | 全部权限        |
| alice@example.com        | alice_vinyl  | USER      | 购买/收藏       |
| bob@example.com          | bob_records  | RETAILER  | 发布/管理唱片   |
| carol@example.com        | carol_music  | USER      | 购买/收藏       |
| dave@example.com         | dave_store   | RETAILER  | 发布/管理唱片   |

---

## 常见问题

### Q: 启动报 "Access denied for user 'root'"
**A:** 检查 `backend/src/main/resources/application.yml` 中密码是否为 `A883709A`

### Q: 启动报 "Port 8080 already in use"
**A:** 在命令行运行 `netstat -ano | findstr :8080`，找到 PID 后在任务管理器结束该进程

### Q: 前端页面显示空白或 API 报错
**A:** 确认后端已启动，浏览器 F12 查看 Console 错误。常见原因是后端未运行

### Q: 在 IDEA 中 Maven 依赖下载失败
**A:** 检查网络，或在 Maven 面板右键 → "Download Sources and Documentation"
也可以在 pom.xml 中添加阿里云镜像（在 settings.xml 配置）

### Q: Navicat 连接失败
**A:** 打开服务管理器（`services.msc`），确认 **MySQL80** 服务状态为"正在运行"

---

## 技术栈概览

| 层次   | 技术                                      |
|------|-------------------------------------------|
| 后端   | Spring Boot 3.2.5 + Spring Security + JPA |
| 认证   | JWT (HS256, jjwt 0.12.5), BCrypt 密码哈希 |
| 数据库 | MySQL 8.0, Hibernate ddl-auto: update     |
| 前端   | 原生 HTML5 / CSS3 / JavaScript (ES6+)     |
| 视觉   | Three.js 粒子背景, Font Awesome 6.5.1      |
| 构建   | Maven 3.9.8, JDK 17 (JBR 21 运行)         |
