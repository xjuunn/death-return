package com.junhsiun.deathrecord

import com.junhsiun.config.DeathReturnConfigManager
import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.Permissions

object DeathPosCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("deathpos")
                    .executes { context ->
                        val source = context.source
                        val player = source.playerOrException

                        if (
                            !DeathReturnConfigManager.config.allowPlayersUseCommand &&
                            !source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
                        ) {
                            source.sendFailure(Component.literal("管理员已禁用普通玩家使用 /deathpos。"))
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val record = DeathRecordStore.get(player.uuid)
                        if (record == null) {
                            source.sendSuccess({ Component.literal("你还没有死亡记录。") }, false)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        source.sendSuccess(
                            { Component.literal("最近死亡坐标: ${formatRecord(record)}") },
                            false
                        )
                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        Commands.argument("target", EntityArgument.player())
                            .requires { source ->
                                source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) &&
                                    DeathReturnConfigManager.config.adminsCanQueryOthers
                            }
                            .executes { context ->
                                val target = EntityArgument.getPlayer(context, "target")
                                val record = DeathRecordStore.get(target.uuid)

                                if (record == null) {
                                    context.source.sendSuccess(
                                        { Component.literal("${target.name.string} 没有死亡记录。") },
                                        false
                                    )
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                context.source.sendSuccess(
                                    {
                                        Component.literal(
                                            "${target.name.string} 最近死亡坐标: ${formatRecord(record)}"
                                        )
                                    },
                                    false
                                )
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
        })
    }

    fun notifyPlayer(player: ServerPlayer) {
        if (!DeathReturnConfigManager.config.announceOnDeath) {
            return
        }

        val record = DeathRecordStore.get(player.uuid) ?: return
        player.sendSystemMessage(Component.literal("你已死亡，坐标已记录: ${formatRecord(record)}"))
    }

    private fun formatRecord(record: DeathRecord): String {
        return "[${record.dimension}] (${record.x}, ${record.y}, ${record.z})"
    }
}
