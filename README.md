# 🌍 EverTerra-TPA

> Cross-platform TPA system for PaperMC with GUI, Economy & I18N

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.20%2B-brightgreen)](https://papermc.io/)

## ✨ Features

- 📤 **TPA / TPAC / Cancel** — Full teleport request system
- 🖥️ **Dual GUI** — Java Inventory for PC + Form UI for Bedrock (mobile-friendly!)
- 📱 **Bedrock Send GUI** — No commands needed! Open GUI → pick player → pick type
- 💰 **Vault Economy** — Configurable cost per teleport type
- ⏱️ **Smart Cooldown** — Prevent spam with per-type cooldowns
- ⏳ **Delayed Teleport** — Countdown with cancel-on-move/damage/death
- 🌐 **Full I18N** — zh_CN, en_US built-in, easy to extend
- 🔒 **Permission System** — Granular control for admins
- ⚡ **Production Ready** — Anti-spam, async validation, memory-safe

## 📦 Requirements

- Paper 1.20+ (or compatible fork)
- Java 17+
- **Optional:** Vault (economy), GeyserMC + Floodgate (Bedrock detection)

## 🚀 Quick Start

1. Download `EverTerra-TPA.jar` from [Releases](https://github.com/EverTerra/EverTerra-TPA/releases)
2. Place it in `plugins/` folder
3. Start server (config auto-generates)
4. Edit `plugins/EverTerra-TPA/config.yml` to your liking
5. Run `/tpareload` to apply changes

## 📋 Commands

| Command | Description |
|---------|-------------|
| `/tpa <player>` | Send teleport request (Bedrock: no args → GUI) |
| `/tpac <player>` | Send teleport-here request |
| `/tpaccept` | Accept pending request |
| `/tpadeny` | Deny pending request |
| `/tpacancel` | Cancel outgoing request |
| `/lang <locale>` | Change language (zh_CN / en_US) |
| `/tpareload` | Reload config (admin) |
| `/tpabypass <player>` | Toggle bypass mode (admin) |

## 🔧 Configuration

```yaml
teleport:
  delay: 5
  cancel_on_move: true
  cancel_on_damage: true

economy:
  enabled: true
  tpa_cost: 50.0
  tpac_cost: 80.0

cooldown:
  tpa: 60
  tpac: 120
```

## 🌐 Supported Languages

| Language | Code |
|----------|------|
| 简体中文 | `zh_CN` |
| English | `en_US` |

> PRs welcome for more languages!

## 🏗️ Building

```bash
./gradlew build
# Output: build/libs/EverTerra-TPA.jar
```

## 📄 License

MIT © EverTerra
