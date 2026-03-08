# Death Return TODO

## 任务拆分

- [x] 1. 初始化 Git 仓库并建立任务追踪
  - 创建 `todo.md`
  - `git init` 并提交初始基线
- [x] 2. 实现核心功能：死亡坐标记录器
  - 玩家死亡时记录维度和坐标
  - 提供 `/deathpos` 查询最近一次死亡坐标
  - 支持单人和多人（纯服务端逻辑）
- [x] 3. 实现管理员配置能力
  - 增加配置文件（默认值 + 持久化）
  - 增加管理命令 `/deathposadmin`
- [x] 4. 清理模板冗余并完善兼容信息
  - 删除示例 mixin/datagen/client 空实现
  - 更新 `fabric.mod.json` 与项目信息
- [ ] 5. 文档与验证
  - 编写中文 README（安装、命令、配置、兼容版本）
  - 执行构建验证

## 兼容目标

- Fabric Loader `0.16+`
- Minecraft `1.21.x`（主目标：`1.21.11`）
- Fabric API `0.141.3+1.21.11`
