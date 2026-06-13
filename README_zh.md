# 🌍 EverTerra-TPA

> PaperMC 跨平台 TPA 系统 — GUI 交互 · 经济支持 · 多语言

[📖 中文版](README_zh.md) | [📖 English](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.20%2B-brightgreen)](https://papermc.io/)
[![Build](https://github.com/FanStudio2077/EverTerra_Tpa/actions/workflows/build.yml/badge.svg)](https://github.com/FanStudio2077/EverTerra_Tpa/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/FanStudio2077/EverTerra_Tpa?color=blue)](https://github.com/FanStudio2077/EverTerra_Tpa/releases)

---

## ✨ 功能特性

| 功能 | 说明 |
|------|------|
| 📤 **TPA / TPAC** | 完整传送请求系统 — 发送 / 接受 / 拒绝 / 取消 |
| 🖥️ **Java GUI** | 背包界面交互，接受/拒绝按钮 + 倒计时显示 |
| 📱 **基岩版 GUI** | 手机版表单 UI，无需输入任何命令！ |
| 💰 **Vault 经济** | 可配置的传送费用，接受时扣费 |
| ⏱️ **冷却系统** | 按类型独立冷却，防止滥用（可配置） |
| ⏳ **延迟传送** | 倒计时机制，移动/受伤/死亡/退出/世界切换自动取消 |
| 🌐 **完整 I18N** | 内置 zh_CN / en_US，玩家可自行切换语言 |
| 🔒 **权限控制** | 细粒度权限：`tpa.use` / `tpa.bypass` / `tpa.reload` |
| ⚡ **生产就绪** | 频率限制、异步验证、内存安全设计 |
| 🔌 **软依赖** | 无需 Vault / Geyser / Floodgate 也能正常运行 |

---

## 📦 环境要求

| 软件 | 版本 | 必需？ |
|------|------|--------|
| PaperMC（或兼容分支） | 1.20+ | ✅ 必需 |
| Java | 21+ | ✅ 必需 |
| [Vault](https://www.spigotmc.org/resources/vault.34315/) | 最新 | ⭐ 可选（经济） |
| [GeyserMC](https://geysermc.org/) | 最新 | ⭐ 可选（基岩版支持） |
| [Floodgate](https://geysermc.org/) | 最新 | ⭐ 可选（基岩版检测） |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | 2.11+ | ⭐ 可选（变量扩展） |

---

## 🚀 快速开始

1. **下载** 最新 `EverTerra-TPA.jar` 从 [Releases](https://github.com/FanStudio2077/EverTerra_Tpa/releases)
2. **放入** 服务器 `plugins/` 文件夹
3. **启动** 服务器（自动生成配置文件）
4. **编辑** `plugins/EverTerra-TPA/config.yml` 按需调整
5. **重载** 使用 `/tpareload`

```bash
# 或从源码构建
git clone https://github.com/FanStudio2077/EverTerra_Tpa.git
cd EverTerra_Tpa
./gradlew build
# 输出: build/libs/EverTerra-TPA-*.jar
```

---

## 📋 命令列表

| 命令 | 权限 | 说明 |
|------|------|------|
| `/tpa <玩家>` | `tpa.use` | 发送传送请求（基岩版无参数 → 打开 GUI） |
| `/tpac <玩家>` | `tpa.use` | 发送邀请传送请求 |
| `/tpaccept` | `tpa.use` | 接受待处理请求 |
| `/tpadeny` | `tpa.use` | 拒绝待处理请求 |
| `/tpacancel` | `tpa.use` | 取消发出的请求 |
| `/lang <语言>` | `tpa.use` | 切换语言（`zh_CN` / `en_US`） |
| `/tpareload` | `tpa.reload` | 重载配置（管理员） |
| `/tpabypass <玩家>` | `tpa.bypass` | 清除玩家冷却（管理员） |

---

## 🔐 权限节点

| 节点 | 默认 | 说明 |
|------|------|------|
| `tpa.use` | `true` | 基础 TPA 命令权限 |
| `tpa.bypass` | `op` | 跳过冷却和经济扣费 |
| `tpa.reload` | `op` | 重载插件配置 |

---

## ⚙️ 配置文件

```yaml
# plugins/EverTerra-TPA/config.yml

teleport:
  delay: 5                    # 传送倒计时（秒）
  cancel_on_move: true        # 移动取消传送
  cancel_on_damage: true      # 受伤取消传送
  cancel_on_world_change: false # 切换世界取消传送

economy:
  enabled: true               # 启用经济
  tpa_cost: 50.0              # /tpa 费用
  tpac_cost: 80.0             # /tpac 费用

cooldown:
  tpa: 60                     # /tpa 冷却（秒）
  tpac: 120                   # /tpac 冷却（秒）

request:
  expire_time: 30             # 请求过期时间（秒）
  max_per_minute: 3           # 每分钟最大请求数
  overwrite_old: true         # 自动覆盖旧请求
  block_self_tpa: true        # 禁止向自己发送请求

i18n:
  default: zh_CN              # 默认语言
  fallback: en_US             # 回退语言
  player_language: true       # 允许玩家切换语言
```

---

## 💰 Vault 经济

安装 Vault 并启用 `economy.enabled: true` 后：

- **接受请求时扣费**（非发送时）
- 由**请求发起者**支付费用
- 拥有 `tpa.bypass` 权限的玩家免扣费
- 余额不足时双方都会收到提示

---

## 📱 Geyser / Floodgate 基岩版兼容

| 平台 | 接收 GUI | 发送 GUI |
|------|----------|----------|
| Java 版 | 背包界面（✔接受 / ✖拒绝） | `/tpa <玩家名>` 命令 |
| 基岩版 | 表单 UI（接受 / 拒绝） | `/tpa`（无参数）→ 选玩家 → 选类型 |

基岩版发送流程：
```
/tpa（不跟参数）
  → 表单：选择在线玩家
  → 表单：[📤 传送到对方] 或 [📥 邀请对方过来]
  → 完成！无需手动输入任何命令
```

---

## 🌐 国际化 (I18N)

| 语言代码 | 语言 | 状态 |
|----------|------|------|
| `zh_CN` | 简体中文 | ✅ 完整 |
| `en_US` | English | ✅ 完整 |

所有消息、GUI 标题、按钮文字均完全本地化。
玩家可使用 `/lang zh_CN` 或 `/lang en_US` 随时切换语言。

---

## 📸 截图

> *在此处添加你的截图*

<!--
![Java GUI](docs/screenshots/java-gui.png)
![基岩版表单](docs/screenshots/bedrock-form.png)
![基岩版发送](docs/screenshots/bedrock-send.png)
-->

---

## 🗺️ 路线图

- [x] TPA / TPAC 核心命令
- [x] Java 背包 GUI
- [x] 基岩版表单 UI（接收 + 发送）
- [x] Vault 经济集成
- [x] 冷却与延迟传送系统
- [x] I18N（zh_CN / en_US）
- [x] CI/CD (GitHub Actions)
- [ ] PlaceholderAPI 变量扩展
- [ ] 多世界传送限制
- [ ] MySQL 跨服 TPA 存储
- [ ] 传送读条特效（粒子/音效）

---

## 🤝 参与贡献

欢迎贡献！请先阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 了解规范。

我们采用 [Conventional Commits](https://www.conventionalcommits.org/)：
- `feat:` — 新功能
- `fix:` — 修复
- `docs:` — 文档
- `refactor:` — 重构
- `chore:` — 构建/CI

---

## 🏗️ 构建

```bash
# 克隆仓库
git clone https://github.com/FanStudio2077/EverTerra_Tpa.git
cd EverTerra_Tpa

# 构建（需要 JDK 21）
./gradlew build

# 输出
ls build/libs/EverTerra-TPA-*.jar
```

---

## 📄 许可证

[MIT License](LICENSE) © 2026 EverTerra

---

## ⭐ Star 历史

如果这个插件对你有帮助，请在 GitHub 上给它一个 ⭐！
