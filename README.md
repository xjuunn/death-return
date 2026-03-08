# Death Return

极简 Fabric 死亡坐标记录器。

- 玩家死亡时自动记录最近一次死亡维度和坐标
- 玩家使用 `/deathpos` 查询自己的最近死亡点
- 管理员可查询他人记录并在线修改配置
- 服务端逻辑实现，天然支持单人（内置服）和多人（专用服）

## 兼容版本

- Minecraft: `1.21` 到 `1.21.11`
- Fabric Loader: `0.16.0+`
- Java: `21+`

说明：当前工程以 `1.21.11` 编译，功能设计保持极简并尽量使用稳定 API，以覆盖 1.21.x 多个小版本。

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
