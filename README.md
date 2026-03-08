# Death Return

极简 Fabric 死亡坐标记录器。

- 玩家死亡时自动记录死亡维度和坐标
- 每位玩家保留最近 `20` 次死亡历史
- 玩家使用 `/deathpos` 以分页列表查看死亡记录
- 支持点击传送回对应死亡点
- 管理员可查询他人记录并在线修改配置
- 同时支持服务端和客户端安装
- 多人服务器场景下，客户端可不安装也能正常连接和使用

## 安装说明

- 专用服务器：服务端安装后即可生效，客户端可以不安装。
- 客户端单独安装：可用于单人游戏，或提前放入整合包中统一分发。
- 单人游戏 / 局域网主机：本地安装后即可生效，因为单人模式使用的是内置服务器。

说明：本模组功能完全由服务端逻辑驱动，所以在多人联机时客户端安装不是必需项；但本模组也可以直接安装在客户端，并在单人世界中正常工作。

## 兼容版本

- Minecraft: `1.21` 到 `1.21.11`
- Fabric Loader: `0.16.0+`
- Java: `21+`

说明：当前工程以 `1.21.11` 编译，核心功能只依赖通用侧 API，没有客户端渲染或界面依赖，因此可同时用于服务端和客户端安装。

## 命令

- `/deathpos`
  - 查看自己的死亡历史第一页
- `/deathpos page <页码>`
  - 查看自己的指定页历史
- `/deathpos tp <编号>`
  - 传送到自己的指定死亡记录
- `/deathpos player <玩家>`
  - 管理员查看目标玩家的死亡历史第一页
- `/deathpos player <玩家> page <页码>`
  - 管理员查看目标玩家的指定页历史
- `/deathposadmin reload`
  - 重载配置文件
- `/deathposadmin set announceOnDeath <true|false>`
  - 控制玩家死亡时是否自动提示记录结果
- `/deathposadmin set allowPlayersUseCommand <true|false>`
  - 控制普通玩家是否可使用 `/deathpos`
- `/deathposadmin set adminsCanQueryOthers <true|false>`
  - 控制管理员是否可查询他人记录
- `/deathposadmin set allowTeleport <true|false>`
  - 控制是否允许玩家传送回死亡点

## 交互说明

- 玩家死亡后会立即收到一条简洁消息，直接显示本次死亡坐标。
- 消息中包含 `查看历史` 和 `传送回去` 按钮，点击即可操作。
- `/deathpos` 列表中每条记录都带有 `传送` 按钮，默认按“最近一次死亡”为 `#1` 排序。
- 历史记录按每页 `5` 条显示，共保留最近 `20` 条。

## 配置文件

路径：`config/death-return.json`

默认内容：

```json
{
  "announceOnDeath": true,
  "allowPlayersUseCommand": true,
  "adminsCanQueryOthers": true,
  "allowTeleport": true
}
```

## 记录文件

路径：`<世界存档>/death-return/death-records.json`

每个玩家保留最近 `20` 次死亡记录。

## 构建

```bash
./gradlew build
```

Windows：

```powershell
.\gradlew.bat build
```
