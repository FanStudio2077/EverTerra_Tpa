# 🌍 EverTerra-TPA

> Cross-platform TPA system for PaperMC — GUI, Economy & I18N

[📖 中文版](README_zh.md) | [📖 English](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.20%2B-brightgreen)](https://papermc.io/)
[![Build](https://github.com/FanStudio2077/EverTerra_Tpa/actions/workflows/build.yml/badge.svg)](https://github.com/FanStudio2077/EverTerra_Tpa/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/FanStudio2077/EverTerra_Tpa?color=blue)](https://github.com/FanStudio2077/EverTerra_Tpa/releases)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📤 **TPA / TPAC** | Full teleport request system — send, accept, deny, cancel |
| 🖥️ **Java GUI** | Inventory-based GUI with accept/deny buttons & countdown timer |
| 📱 **Bedrock GUI** | Form UI for mobile players — no commands needed! |
| 💰 **Vault Economy** | Configurable cost per teleport type, charged on acceptance |
| ⏱️ **Cooldown System** | Per-type cooldowns to prevent spam (configurable) |
| ⏳ **Delay Teleport** | Countdown with cancel-on-move / damage / death / world-change |
| 🌐 **Full I18N** | zh_CN, en_US built-in — player-specific language preference |
| 🔒 **Permissions** | Granular control: `tpa.use`, `tpa.bypass`, `tpa.reload` |
| ⚡ **Production Ready** | Anti-spam rate limiting, async validation, memory-safe design |
| 🔌 **Soft Dependencies** | Works without Vault / Geyser / Floodgate — graceful degradation |

---

## 📦 Requirements

| Software | Version | Required? |
|----------|---------|-----------|
| PaperMC (or fork) | 1.20+ | ✅ Required |
| Java | 21+ | ✅ Required |
| [Vault](https://www.spigotmc.org/resources/vault.34315/) | Latest | ⭐ Optional (economy) |
| [GeyserMC](https://geysermc.org/) | Latest | ⭐ Optional (Bedrock support) |
| [Floodgate](https://geysermc.org/) | Latest | ⭐ Optional (Bedrock detection) |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | 2.11+ | ⭐ Optional (placeholders) |

---

## 🚀 Quick Start

1. **Download** the latest `EverTerra-TPA.jar` from [Releases](https://github.com/FanStudio2077/EverTerra_Tpa/releases)
2. **Place** the JAR in your server's `plugins/` folder
3. **Start** the server (config auto-generates)
4. **Edit** `plugins/EverTerra-TPA/config.yml` to your liking
5. **Reload** with `/tpareload`

```bash
# Or build from source
git clone https://github.com/FanStudio2077/EverTerra_Tpa.git
cd EverTerra_Tpa
./gradlew build
# Output: build/libs/EverTerra-TPA-*.jar
```

---

## 📋 Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/tpa <player>` | `tpa.use` | Send teleport request (Bedrock: no args → GUI) |
| `/tpac <player>` | `tpa.use` | Send teleport-here request |
| `/tpaccept` | `tpa.use` | Accept pending request |
| `/tpadeny` | `tpa.use` | Deny pending request |
| `/tpacancel` | `tpa.use` | Cancel outgoing request |
| `/lang <locale>` | `tpa.use` | Change language (`zh_CN` / `en_US`) |
| `/tpareload` | `tpa.reload` | Reload config (admin) |
| `/tpabypass <player>` | `tpa.bypass` | Clear cooldowns for a player (admin) |

---

## 🔐 Permissions

| Node | Default | Description |
|------|---------|-------------|
| `tpa.use` | `true` | Basic TPA command access |
| `tpa.bypass` | `op` | Bypass cooldown & economy costs |
| `tpa.reload` | `op` | Reload plugin configuration |

---

## ⚙️ Configuration

```yaml
# plugins/EverTerra-TPA/config.yml

teleport:
  delay: 5                    # Countdown seconds
  cancel_on_move: true
  cancel_on_damage: true
  cancel_on_world_change: false

economy:
  enabled: true
  tpa_cost: 50.0              # Cost for /tpa
  tpac_cost: 80.0             # Cost for /tpac

cooldown:
  tpa: 60                     # Seconds after /tpa
  tpac: 120                   # Seconds after /tpac

request:
  expire_time: 30             # Auto-expire seconds
  max_per_minute: 3           # Anti-spam limit
  overwrite_old: true
  block_self_tpa: true

i18n:
  default: zh_CN
  fallback: en_US
  player_language: true       # Allow per-player language
```

---

## 💰 Vault Economy

When Vault is installed and `economy.enabled: true`:

- Cost is **charged on acceptance** (not on request)
- The **requester** pays the fee
- Players with `tpa.bypass` are exempt
- If the requester cannot afford the fee, both parties are notified

---

## 📱 Geyser / Floodgate Compatibility

| Platform | Receive GUI | Send GUI |
|----------|-------------|----------|
| Java Edition | Inventory GUI (✔/✖ buttons) | `/tpa <name>` command |
| Bedrock Edition | Form UI (Accept/Deny) | `/tpa` (no args) → Player list → Choose type |

Bedrock send workflow:
```
/tpa (no arguments)
  → Form: Choose online player
  → Form: [📤 Teleport to them] or [📥 Invite them here]
  → Done! No manual command typing needed
```

---

## 🌐 Internationalization (I18N)

| Locale | Language | Status |
|--------|----------|--------|
| `zh_CN` | 简体中文 | ✅ Complete |
| `en_US` | English | ✅ Complete |

All messages, GUI titles, buttons, and prompts are fully localized.
Players can switch language with `/lang zh_CN` or `/lang en_US`.

---

## 📸 Screenshots

> *Screenshots placeholder — add your own images here*

<!--
![Java GUI](docs/screenshots/java-gui.png)
![Bedrock Form](docs/screenshots/bedrock-form.png)
![Bedrock Send](docs/screenshots/bedrock-send.png)
-->

---

## 🗺️ Roadmap

- [x] TPA / TPAC core commands
- [x] Java Inventory GUI
- [x] Bedrock Form UI (receive + send)
- [x] Vault economy integration
- [x] Cooldown & delay systems
- [x] I18N (zh_CN / en_US)
- [x] CI/CD with GitHub Actions
- [ ] PlaceholderAPI expansion support
- [ ] Multi-world restrictions
- [ ] MySQL storage for cross-server TPA
- [ ] Warmup effects (particles / sounds)

---

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

We follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` — New feature
- `fix:` — Bug fix
- `docs:` — Documentation
- `refactor:` — Code refactoring
- `chore:` — Build / CI

---

## 🏗️ Building

```bash
# Clone
git clone https://github.com/FanStudio2077/EverTerra_Tpa.git
cd EverTerra_Tpa

# Build (requires JDK 21)
./gradlew build

# Output
ls build/libs/EverTerra-TPA-*.jar
```

---

## 📄 License

[MIT License](LICENSE) © 2026 EverTerra

---

## ⭐ Star History

If you find this plugin useful, please consider giving it a ⭐ on GitHub!
