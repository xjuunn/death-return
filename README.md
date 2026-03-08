# Death Return

中文 | [English](#english)

`Death Return` 是一个面向 Fabric 的死亡坐标记录模组，提供简洁、可追溯、可传送的死亡记录体验。

玩家死亡后，模组会自动记录所在维度、坐标与时间，保留最近 `20` 次死亡历史，并通过命令与可点击聊天消息提供查看和传送能力。模组支持原版维度与其他模组新增维度。

![Preview](./docs/assets/img.png)

## 目录

- [功能概览](#功能概览)
- [兼容性](#兼容性)
- [前置依赖](#前置依赖)
- [安装方式](#安装方式)
- [使用方式](#使用方式)
- [命令说明](#命令说明)
- [配置说明](#配置说明)
- [数据存储](#数据存储)
- [开发与构建](#开发与构建)
- [版本发布](#版本发布)
- [English](#english)

## 功能概览

- 玩家死亡后自动记录当前位置，无需手动操作。
- 每位玩家保留最近 `20` 次死亡历史。
- `/deathpos` 以分页列表形式显示记录，每页 `5` 条。
- 每条记录都提供可点击的传送入口。
- 死亡后立即显示一条紧凑消息，包含坐标、历史查看和回传按钮。
- 支持管理员查询其他玩家的死亡历史。
- 支持原版维度与模组新增维度的记录、显示和传送。
- 主要逻辑由服务端驱动，多人联机时客户端可选安装。

## 兼容性

| 项目 | 说明 |
| --- | --- |
| Minecraft | `1.21` 到 `1.21.11` |
| Fabric Loader | `0.16.0+` |
| Java | `21+` |
| Fabric API | 必需 |
| Fabric Language Kotlin | 必需 |

当前工程以 `Minecraft 1.21.11` 为主目标版本进行开发。

## 前置依赖

安装本模组前，请确保以下依赖已放入对应环境的 `mods` 目录：

| 依赖 | 是否必需 | 说明 |
| --- | --- | --- |
| Fabric API | 必需 | 提供 Fabric 事件、命令和运行时 API |
| Fabric Language Kotlin | 必需 | 本模组使用 Kotlin 编写，缺失时无法加载 |

如果缺少 `fabric-language-kotlin`，游戏启动时会出现类似报错：

```text
Mod 'Death Return' (death-return) ... requires fabric-language-kotlin, which is missing
```

## 安装方式

### 专用服务器

将模组与依赖放入服务器的 `mods` 目录后即可生效。客户端连接时不强制要求安装本模组。

### 客户端 / 单人游戏

本模组也可以直接安装在客户端，并在单人世界或局域网主机中正常工作。单人模式本质上运行的是内置服务器，因此本地安装后功能同样可用。

## 使用方式

玩家死亡后，模组会自动完成以下操作：

1. 记录本次死亡的维度、坐标和时间。
2. 将记录插入历史列表头部。
3. 仅保留最近 `20` 条数据。
4. 在聊天栏中发送一条简洁提示消息。

默认交互包括：

- `查看历史`：打开自己的死亡记录列表。
- `传送回去`：回到最近一次死亡点。

如果服务器管理员关闭了传送功能，传送入口将不会显示。

## 命令说明

### 玩家命令

| 命令 | 说明 |
| --- | --- |
| `/deathpos` | 查看自己的死亡历史第一页 |
| `/deathpos page <页码>` | 查看自己的指定页历史 |
| `/deathpos tp <编号>` | 传送到自己指定编号的死亡记录 |

### 管理员命令

| 命令 | 说明 |
| --- | --- |
| `/deathpos player <玩家>` | 查看目标玩家的死亡历史第一页 |
| `/deathpos player <玩家> page <页码>` | 查看目标玩家的指定页历史 |
| `/deathposadmin reload` | 重载配置文件 |
| `/deathposadmin set announceOnDeath <true\|false>` | 是否在死亡后发送提示消息 |
| `/deathposadmin set allowPlayersUseCommand <true\|false>` | 是否允许普通玩家使用 `/deathpos` |
| `/deathposadmin set adminsCanQueryOthers <true\|false>` | 是否允许管理员查询其他玩家记录 |
| `/deathposadmin set allowTeleport <true\|false>` | 是否允许玩家传送回死亡点 |

## 配置说明

配置文件路径：

```text
config/death-return.json
```

默认配置：

```json
{
  "announceOnDeath": true,
  "allowPlayersUseCommand": true,
  "adminsCanQueryOthers": true,
  "allowTeleport": true
}
```

| 字段 | 默认值 | 说明 |
| --- | --- | --- |
| `announceOnDeath` | `true` | 玩家死亡后是否发送提示消息 |
| `allowPlayersUseCommand` | `true` | 普通玩家是否可以使用 `/deathpos` |
| `adminsCanQueryOthers` | `true` | 管理员是否可以查询其他玩家记录 |
| `allowTeleport` | `true` | 是否允许通过命令或按钮传送回死亡点 |

## 数据存储

死亡记录文件保存在世界存档目录中：

```text
<世界存档>/death-return/death-records.json
```

数据特点：

- 每位玩家保留最近 `20` 条记录。
- 新记录始终插入到列表顶部。
- 旧版本仅存储单条记录的数据会在读取时自动兼容。
- 维度统一保存为标准维度 ID，例如 `minecraft:overworld`、`ad_astra:moon`。

## 开发与构建

### 环境要求

- JDK `21`
- Gradle Wrapper

### 常用命令

构建：

```bash
./gradlew build
```

Windows：

```powershell
.\gradlew.bat build
```

仅编译 Kotlin：

```powershell
.\gradlew.bat compileKotlin
```

启动开发客户端：

```powershell
.\gradlew.bat runClient
```

启动开发服务端：

```powershell
.\gradlew.bat runServer
```

## 版本发布

项目当前采用基于 Git tag 的自动发布方式。

当前版本：

```text
1.1.2
```

版本号定义于：

```text
gradle.properties
```

发布流程：

1. 修改 `gradle.properties` 中的 `mod_version`
2. 提交版本变更
3. 创建并推送 tag，例如：

```bash
git tag v1.1.2
git push origin v1.1.2
```

4. GitHub Actions 会自动：
   - 使用 JDK 21 构建项目
   - 生成 `build/libs/` 下的发布产物
   - 创建对应的 GitHub Release
   - 将构建出的 jar 文件附加到 Release

生成的 jar 文件名格式示例：

```text
death-return-1.1.2-fabricmc1.21.11.jar
```

## English

`Death Return` is a Fabric mod focused on death position tracking with a minimal and practical workflow.

When a player dies, the mod automatically records the dimension, coordinates, and timestamp, keeps the latest `20` death entries, and provides command-based viewing plus clickable chat actions for quick access and teleportation. Both vanilla and modded dimensions are supported.

### Overview

- Automatically records death location without manual input.
- Keeps the latest `20` death history entries per player.
- `/deathpos` shows history in pages of `5` entries.
- Each entry includes a clickable teleport action.
- Sends a compact chat message on death with quick actions.
- Supports admin queries for other players.
- Supports vanilla dimensions and mod-added dimensions.
- Main logic is server-driven, so client installation is optional for multiplayer use.

### Compatibility

| Item | Requirement |
| --- | --- |
| Minecraft | `1.21` to `1.21.11` |
| Fabric Loader | `0.16.0+` |
| Java | `21+` |
| Fabric API | Required |
| Fabric Language Kotlin | Required |

### Required Dependencies

Install the following dependencies into the target `mods` directory together with this mod:

| Dependency | Required | Notes |
| --- | --- | --- |
| Fabric API | Yes | Provides Fabric events, commands, and runtime APIs |
| Fabric Language Kotlin | Yes | Required because this mod is written in Kotlin |

If `fabric-language-kotlin` is missing, the game will fail to load the mod.

### Installation

#### Dedicated Server

Place the mod and its dependencies into the server `mods` directory. Clients do not need to install the mod in order to join.

#### Client / Singleplayer

The mod can also be installed on the client side for singleplayer or LAN host usage. Since singleplayer uses an integrated server, the mod works normally when installed locally.

### Commands

#### Player Commands

| Command | Description |
| --- | --- |
| `/deathpos` | Show the first page of your death history |
| `/deathpos page <page>` | Show a specific page of your history |
| `/deathpos tp <index>` | Teleport to a specific death record |

#### Admin Commands

| Command | Description |
| --- | --- |
| `/deathpos player <player>` | Show the first page of another player's death history |
| `/deathpos player <player> page <page>` | Show a specific page of another player's history |
| `/deathposadmin reload` | Reload the config file |
| `/deathposadmin set announceOnDeath <true\|false>` | Enable or disable death chat notifications |
| `/deathposadmin set allowPlayersUseCommand <true\|false>` | Allow or block regular players from using `/deathpos` |
| `/deathposadmin set adminsCanQueryOthers <true\|false>` | Allow or block admin queries for other players |
| `/deathposadmin set allowTeleport <true\|false>` | Allow or block teleporting back to death points |

### Configuration

Config file path:

```text
config/death-return.json
```

Default config:

```json
{
  "announceOnDeath": true,
  "allowPlayersUseCommand": true,
  "adminsCanQueryOthers": true,
  "allowTeleport": true
}
```

### Data Storage

Death records are stored in:

```text
<world-save>/death-return/death-records.json
```

- Stores up to `20` entries per player.
- New entries are inserted at the top of the list.
- Older single-entry data is migrated automatically when read.
- Dimensions are stored as normalized dimension IDs such as `minecraft:overworld` or `ad_astra:moon`.

### Development and Build

Build:

```bash
./gradlew build
```

Windows:

```powershell
.\gradlew.bat build
```

Run client:

```powershell
.\gradlew.bat runClient
```

Run server:

```powershell
.\gradlew.bat runServer
```

### Release

The project uses Git tag based automated releases.

1. Update `mod_version` in `gradle.properties`
2. Commit the version change
3. Push a tag such as:

```bash
git tag v1.1.2
git push origin v1.1.2
```

GitHub Actions will build the project, create a GitHub Release, and upload the generated jar artifacts automatically.
