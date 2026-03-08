package com.junhsiun.deathrecord

import com.mojang.brigadier.Command
import com.junhsiun.config.DeathReturnConfigManager
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object DeathPosCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                literal("deathpos")
                    .executes { context ->
                        val source = context.source
                        val player = source.playerOrThrow

                        if (!DeathReturnConfigManager.config.allowPlayersUseCommand && !source.hasPermissionLevel(2)) {
                            source.sendError(Text.literal("管理员已禁用普通玩家使用 /deathpos。"))
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val record = DeathRecordStore.get(player.uuid)
                        if (record == null) {
                            source.sendFeedback({ Text.literal("你还没有死亡记录。") }, false)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val message = formatRecord(record)
                        source.sendFeedback({ Text.literal("最近死亡坐标: $message") }, false)
                        return@executes Command.SINGLE_SUCCESS
                    }
                    .then(
                        net.minecraft.server.command.CommandManager.argument("target", EntityArgumentType.player())
                            .requires { source ->
                                source.hasPermissionLevel(2) && DeathReturnConfigManager.config.adminsCanQueryOthers
                            }
                            .executes { context ->
                                val target = EntityArgumentType.getPlayer(context, "target")
                                val record = DeathRecordStore.get(target.uuid)
                                if (record == null) {
                                    context.source.sendFeedback(
                                        { Text.literal("${target.name.string} 没有死亡记录。") },
                                        false
                                    )
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                val message = formatRecord(record)
                                context.source.sendFeedback(
                                    { Text.literal("${target.name.string} 最近死亡坐标: $message") },
                                    false
                                )
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
        })
    }

    fun notifyPlayer(player: ServerPlayerEntity) {
        if (!DeathReturnConfigManager.config.announceOnDeath) {
            return
        }
        val record = DeathRecordStore.get(player.uuid) ?: return
        player.sendMessage(Text.literal("你已死亡，坐标已记录: ${formatRecord(record)}"))
    }

    private fun formatRecord(record: DeathRecord): String {
        return "[${record.dimension}] (${record.x}, ${record.y}, ${record.z})"
    }
}
