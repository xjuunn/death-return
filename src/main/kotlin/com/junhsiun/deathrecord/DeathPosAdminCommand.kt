package com.junhsiun.deathrecord

import com.junhsiun.config.DeathReturnConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

object DeathPosAdminCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("deathposadmin")
                    .requires { source ->
                        source.permissions().hasPermission(Permissions.COMMANDS_ADMIN)
                    }
                    .then(
                        Commands.literal("reload")
                            .executes { context ->
                                DeathReturnConfigManager.load()
                                context.source.sendSuccess(
                                    { Component.literal("Death Return 配置已重载。") },
                                    true
                                )
                                Command.SINGLE_SUCCESS
                            }
                    )
                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.literal("announceOnDeath")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes { context ->
                                                val value = BoolArgumentType.getBool(context, "value")
                                                DeathReturnConfigManager.update {
                                                    it.copy(announceOnDeath = value)
                                                }
                                                context.source.sendSuccess(
                                                    { Component.literal("announceOnDeath = $value") },
                                                    true
                                                )
                                                Command.SINGLE_SUCCESS
                                            }
                                    )
                            )
                            .then(
                                Commands.literal("allowPlayersUseCommand")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes { context ->
                                                val value = BoolArgumentType.getBool(context, "value")
                                                DeathReturnConfigManager.update {
                                                    it.copy(allowPlayersUseCommand = value)
                                                }
                                                context.source.sendSuccess(
                                                    { Component.literal("allowPlayersUseCommand = $value") },
                                                    true
                                                )
                                                Command.SINGLE_SUCCESS
                                            }
                                    )
                            )
                            .then(
                                Commands.literal("adminsCanQueryOthers")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes { context ->
                                                val value = BoolArgumentType.getBool(context, "value")
                                                DeathReturnConfigManager.update {
                                                    it.copy(adminsCanQueryOthers = value)
                                                }
                                                context.source.sendSuccess(
                                                    { Component.literal("adminsCanQueryOthers = $value") },
                                                    true
                                                )
                                                Command.SINGLE_SUCCESS
                                            }
                                    )
                            )
                    )
            )
        })
    }
}
