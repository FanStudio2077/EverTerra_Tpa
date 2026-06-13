# ⭐ EverTerra-TPA（最终版AI Agent开发规格书）

> 用途：直接交给 AI Coding Agent / 自动生成插件工程
> 目标：Paper 生态 + Geyser 兼容 + Vault 经济 + GUI + I18N + 高稳定生产级 TPA 插件

---

# 1. 项目定位

## 插件名称

```text
EverTerra-TPA
```

## 类型

```text
Paper Plugin（Spigot/Paper API）
+ Geyser Compatible
+ Vault Economy Integration
+ Internationalized (I18N) System
```

---

## 核心目标

构建一个生产级 TPA 系统：

* ✔ TPA / TPAC / TPACancel
* ✔ GUI + 命令双入口
* ✔ Java + Bedrock 完全兼容
* ✔ Vault 经济支持
* ✔ 延迟传送系统
* ✔ 冷却系统
* ✔ 请求过期机制
* ✔ 完整 I18N 国际化
* ✔ 可开源、可扩展架构

---

# 2. 技术依赖

## 必需依赖

* PaperMC 1.20+
* Java 17+
* Bukkit API

---

## 可选依赖

* Vault
* GeyserMC
* Floodgate
* PlaceholderAPI（扩展）

---

# 3. 系统总架构

```text
Player Command / GUI
        ↓
Command Layer
        ↓
TPA Controller
        ↓
Request Manager (Memory Cache)
        ↓
Validation Layer
   ├─ Cooldown System
   ├─ Economy (Vault)
   ├─ Permission Check
        ↓
Teleport Scheduler
        ↓
Bukkit Teleport API
```

---

# 4. 核心模块设计

---

# 4.1 TPA请求系统

## 数据结构

```java
class TpaRequest {
    UUID sender;
    UUID target;
    TpaType type; // TPA / TPAC
    long createTime;
    long expireTime;
}
```

---

## 存储结构

```java
Map<UUID, TpaRequest> requestMap;
```

---

## 规则

* 一个玩家只能有 1 个活跃请求
* 自动覆盖旧请求（可配置）
* 自动过期（默认 30s）

---

# 4.2 命令系统

## 玩家命令

```text
/tpa [player]         → Java: 直接发送 / Bedrock: 打开GUI选择
/tpa gui              → 强制打开选择界面（全平台）
/tpac <player>
/tpaccept
/tpadeny
/tpacancel
/lang <locale>
```

---

## 管理命令

```text
/tpareload
/tpabypass <player>
```

---

# 4.3 GUI系统（核心体验）

## Java GUI（Inventory）

```text
========================
来自: PlayerA

[✔ 接受]
[✖ 拒绝]
[⏳ 剩余时间]
========================
```

---

## Bedrock GUI（Form UI）

```text
PlayerA 请求传送到你

[接受]
[拒绝]
```

---

## Bedrock 发送 TPA GUI（新增 ★）

> 基岩版玩家（手机/主机）输入命令不便，需要独立 GUI 发起请求

### 入口

```text
/tpa        → 自动检测 Bedrock → 打开 Form UI
/tpa gui    → Java/Bedrock 均可打开选择界面
```

### 流程

```text
玩家执行 /tpa（无参数）
        ↓
检测是否为 Bedrock 玩家
   ├─ Java → 提示用法 /tpa <player>
   └─ Bedrock → 打开 Form UI
        ↓
Form UI: 在线玩家列表
   ├─ 点击玩家 → 选择传送类型
   │     ├─ [传送到对方] (TPA)
   │     └─ [邀请对方] (TPAC)
   └─ 取消 → 关闭
        ↓
发送请求 → 显示结果
```

### Bedrock 发送 GUI 设计

```text
第一步：选择目标玩家
┌──────────────────────┐
│  选择传送目标        │
│                      │
│  👤 PlayerA          │
│  👤 PlayerB          │
│  👤 PlayerC          │
│                      │
│  [取消]              │
└──────────────────────┘

第二步：选择传送类型
┌──────────────────────┐
│  PlayerA             │
│                      │
│  [📤 传送到对方]     │
│  [📥 邀请对方]       │
│                      │
│  [返回] [取消]       │
└──────────────────────┘
```

---

## GUI规则

* 点击即处理
* 防重复点击
* 自动关闭界面
* 支持国际化文本

---

# 4.4 传送延迟系统（关键玩法）

## 配置

```yaml
teleport:
  delay: 5
  cancel_on_move: true
  cancel_on_damage: true
```

---

## 流程

```text
接受请求
↓
进入倒计时任务
↓
检测玩家状态
↓
5秒后执行传送
```

---

## 取消条件

* 移动
* 受伤
* 死亡
* 退出
* 切换世界（可选）

---

# 4.5 Vault经济系统

## 依赖

Vault

---

## 配置

```yaml
economy:
  enabled: true
  tpa_cost: 50
  tpac_cost: 80
```

---

## 逻辑

```text
确认接受 → 检查余额 → 扣费 → 执行传送
```

---

## 原则

* ❌ 不在发送请求时扣费
* ✔ 在“确认接受”时扣费

---

# 4.6 冷却系统

## 配置

```yaml
cooldown:
  tpa: 60
  tpac: 120
```

---

## 逻辑

```text
发送请求 → 记录时间
↓
未冷却结束 → 拒绝发送
```

---

# 4.7 请求过期机制

```yaml
expire_time: 30
```

---

## 行为

```text
30秒未处理 → 自动删除请求
```

---

# 4.8 权限系统

```text
tpa.use
tpa.bypass
tpa.reload
```

---

# 4.9 兼容 Geyser / Bedrock

## 原则

* ❌ 不依赖客户端逻辑
* ✔ 统一 Bukkit Player API
* ✔ GUI自动适配 Form UI

---

## 可选检测

Floodgate

---

# 5. I18N 国际化系统（核心亮点）

---

# 5.1 支持语言

```text
zh_CN
en_US
ja_JP（可扩展）
ko_KR（可扩展）
```

---

# 5.2 目录结构

```text
/lang
 ├── zh_CN.yml
 ├── en_US.yml
```

---

# 5.3 配置

```yaml
i18n:
  default: zh_CN
  fallback: en_US
  player_language: true
```

---

# 5.4 语言文件结构

## zh_CN.yml

```yaml
tpa:
  sent: "&a已发送TPA请求"
  received: "&e{player} 请求传送到你"
  accepted: "&a已接受"
  denied: "&c已拒绝"

error:
  cooldown: "&c冷却中 {time}s"
  no_money: "&c余额不足 {cost}"
```

---

## en_US.yml

```yaml
tpa:
  sent: "&aTPA request sent"
  received: "&e{player} wants to teleport"
  accepted: "&aAccepted"
  denied: "&cDenied"

error:
  cooldown: "&cCooldown {time}s"
  no_money: "&cNot enough money {cost}"
```

---

# 5.5 API设计

```java
lang.get(player, "tpa.sent");

lang.format(player, "error.cooldown",
    Map.of("time", 30));
```

---

# 6. GUI国际化

```text
GUI标题 / 内容 / 按钮全部来自语言文件
```

---

# 7. 数据结构设计

```java
Map<UUID, TpaRequest> requests;
Map<UUID, Long> cooldowns;
Map<UUID, String> playerLang;
```

---

# 8. 插件启动流程

```text
onEnable()
  ↓
加载配置
  ↓
加载语言文件
  ↓
初始化Vault
  ↓
注册命令
  ↓
注册事件监听
  ↓
启动过期清理任务
```

---

# 9. 事件监听

必须监听：

```text
PlayerQuitEvent
PlayerMoveEvent
PlayerDeathEvent
InventoryClickEvent
```

---

# 10. 性能设计

## 要求

* 全异步验证
* 主线程仅执行 teleport
* requestMap 内存结构
* 定时清理任务（避免泄漏）

---

# 11. 安全机制

```yaml
limits:
  max_requests_per_minute: 3
  block_self_tpa: true
```

---

# 12. 开源规范（GitHub级）

## 项目结构

```text
src/
lang/
config.yml
README.md
LICENSE
```

---

## README要求

* 英文为主
* 中文补充
* 示例命令
* 配置说明
* API扩展说明

---

# 13. 验收标准

## 功能

* /tpa 可用
* GUI 可用（Java + Bedrock）
* 延迟传送有效
* 冷却系统生效
* Vault扣费正确
* 请求过期正常

---

## 国际化

* 所有文本可切换语言
* 无硬编码字符串
* GUI完全国际化
* 支持玩家语言切换

---

## 兼容性

* Java 正常
* Bedrock 正常（Geyser）
* Floodgate 不影响逻辑

---

## 稳定性

* 无内存泄漏
* 无重复任务
* 高频请求不崩服

---

# 14. 最终定义

```text
EverTerra-TPA =
跨平台TPA系统
+ GUI交互系统
+ 经济系统
+ 延迟传送机制
+ 冷却机制
+ 国际化系统
+ 开源级工程架构
```

---

# 如果你下一步要做（建议）

我可以继续帮你生成：

## ✔ AI Agent工程生成版（Maven完整项目）

包含：

* 完整 package 结构
* Java代码骨架
* GUI实现（Inventory + Form）
* Vault封装
* I18N加载器完整实现
* TPA核心逻辑代码
* 事件监听完整实现

只要你说：

> 👉 “生成TPA插件工程代码版”

我可以直接给你**可自动生成代码的工程模板**。
