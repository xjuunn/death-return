# Death Return

极简 Fabric 死亡坐标记录器。

- 玩家死亡时自动记录最近一次死亡维度和坐标
- 玩家使用 `/deathpos` 查询自己的最近死亡点
- 管理员可查询他人记录并在线修改配置
- 纯服务端逻辑实现，不需要客户端安装配套模组即可在多人服务器使用

## 安装说明

- 专用服务器：只需要服务端安装本模组，客户端无需安装即可正常连接和使用命令结果。
- 单人游戏 / 局域网主机：需要在主机本地安装本模组，因为单人模式使用的是内置服务器。

说明：`客户端不需要安装` 仅适用于连接到已安装本模组的 Fabric 服务器时。单人模式下，客户端和服务器在同一进程内运行，无法只装“服务端”而完全不装本地模组。

## 兼容版本

- Minecraft: `1.21` 到 `1.21.11`
- Fabric Loader: `0.16.0+`
- Java: `21+`

说明：当前工程以 `1.21.11` 编译，代码只使用服务端事件、服务端命令和世界存档，不依赖客户端渲染或界面逻辑。

## 命令

- `/deathpos`
  - 查询自己最近一次死亡坐标
- `/deathpos <玩家>`
  - 管理员查询目标玩家最近一次死亡坐标
- `/deathposadmin reload`
  - 重载配置文件
- `/deathposadmin set announceOnDeath <true|false>`
  - 控制玩家死亡时是否自动提示记录结果
- `/deathposadmin set allowPlayersUseCommand <true|false>`
  - 控制普通玩家是否可使用 `/deathpos`
- `/deathposadmin set adminsCanQueryOthers <true|false>`
  - 控制管理员是否可查询他人记录

## 配置文件

路径：`config/death-return.json`

默认内容：

```json
{
  "announceOnDeath": true,
  "allowPlayersUseCommand": true,
  "adminsCanQueryOthers": true
}
```

## 记录文件

路径：`<世界存档>/death-return/death-records.json`

每个玩家仅保留“最近一次”死亡坐标。

## 构建

```bash
./gradlew build
```

Windows：

```powershell
.\gradlew.bat build
```
