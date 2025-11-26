这是一个为你定制的 `README.md` 文件。我已经为你设计了专业的排版，包含了技术栈徽章、功能介绍、开发指南等标准模块。你可以直接复制使用，或者根据实际的项目文件夹结构稍作调整。

---

# 🌿 植物大战僵尸融合版模组管理器 (PVZ Fusion Mod Manager)

[![Tauri](https://img.shields.io/badge/Tauri-v2-FEC00F?logo=tauri&logoColor=black)](https://tauri.app/)
[![Vue](https://img.shields.io/badge/Vue.js-v3-4FC08D?logo=vue.js&logoColor=white)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![TypeScript](https://img.shields.io/badge/TypeScript-Ready-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**融合版模组管理器** 是一款专为《植物大战僵尸融合版》打造的现代化模组管理工具。它基于 **Tauri + Vue 3** 构建轻量级客户端，配合 **Spring Boot** 后端服务，为玩家提供便捷的在线模组下载、自动安装以及本地模组管理功能。

完美支持 **BepInEx** 和 **MelonLoader** 模组框架。

---

## ✨ 核心功能

### 🚀 在线模组库
*   **云端获取**：连接后端 API，实时获取最新的模组列表。
*   **一键安装**：支持模组（ZIP/DLL）直链下载，自动解压并部署至游戏根目录。
*   **智能检测**：下载前自动检测模组所需的框架（BepInEx/MelonLoader）与当前环境是否匹配。
*   **进度可视化**：提供下载进度条、实时状态反馈及取消下载功能。

### 📂 本地模组管理
*   **自动扫描**：智能识别游戏目录下 `BepInEx/plugins` 或 `MelonLoader/Mods` 中的 DLL 文件。
*   **一键开关**：通过修改文件后缀（`.dll` <-> `.dll-close`）实现模组的快速启用与禁用，无需删除文件。
*   **信息识别**：自动匹配本地文件名与在线数据库，显示模组的中文名称和详细信息。

### ⚙️ 系统设置
*   **路径配置**：支持手动选择或自动检测游戏安装路径。
*   **框架识别**：自动识别当前游戏安装的模组加载器版本。
*   **版本检查**：内置应用版本检测与更新提示。

---

## 🛠 技术栈

### 🖥️ 客户端 (Client)
*   **核心框架**: [Tauri v2](https://tauri.app/) (Rust + Webview)
*   **前端框架**: [Vue 3](https://vuejs.org/) + TypeScript
*   **UI 组件库**: [TDesign Vue Next](https://tdesign.tencent.com/vue-next/)
*   **构建工具**: Vite
*   **状态管理**: Pinia (可选) / Reactivity API

### ☁️ 服务端 (Backend)
*   **核心框架**: Spring Boot
*   **语言**: Java
*   **数据库**: MySQL / SQLite (根据实际情况填写)
*   **API**: RESTful API 提供模组元数据查询

---

## 💻 开发指南

### 前置要求
*   [Node.js](https://nodejs.org/) (建议 v18+)
*   [Rust](https://www.rust-lang.org/) (用于 Tauri 编译)
*   [JDK 17+](https://adoptium.net/) (用于后端运行)
*   [Maven](https://maven.apache.org/) (用于后端构建)

### 1. 启动后端服务
```bash
cd backend
mvn spring-boot:run
# 服务默认运行在 http://localhost:8080 (根据实际配置)
```

### 2. 启动客户端开发环境
```bash
# 安装依赖
npm install

# 启动 Tauri 开发窗口
npm run tauri dev
```

### 3. 打包发布
```bash
# 构建生产环境包 (Windows/macOS/Linux)
npm run tauri build
```

---

## 📖 使用说明

1.  **首次运行**：
    *   打开软件，进入【设置】页面。
    *   点击“浏览”选择《植物大战僵尸融合版》的游戏根目录（包含 `PlantsVsZombies.exe` 的文件夹）。
2.  **下载模组**：
    *   切换到【在线模组】页面，浏览感兴趣的模组。
    *   点击“下载并安装”，等待进度条完成即可。
3.  **管理模组**：
    *   切换到【本地模组】页面。
    *   点击开关即可启用或禁用模组，无需重启游戏（部分模组可能需要重启生效）。

---

## 🤝 贡献 (Contributing)

欢迎提交 Issue 或 Pull Request！

1.  Fork 本仓库
2.  新建分支 `git checkout -b feature/AmazingFeature`
3.  提交更改 `git commit -m 'Add some AmazingFeature'`
4.  推送到分支 `git push origin feature/AmazingFeature`
5.  提交 Pull Request

---

## ⚠️ 免责声明

本项目是由爱好者开发的第三方工具，与《植物大战僵尸》官方或融合版作者无直接关联。
*   请确保您拥有游戏的合法副本。
*   使用模组可能会导致游戏存档损坏，建议在使用前备份存档。
*   本项目仅供学习与交流使用。

---

## 📄 开源协议

MIT License © 2025 [Gaoshu705]