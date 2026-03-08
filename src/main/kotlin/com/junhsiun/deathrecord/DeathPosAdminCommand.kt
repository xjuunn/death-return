package com.junhsiun.deathrecord

import com.junhsiun.config.DeathReturnConfig
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
                                    { Component.literal("Death Return \u914d\u7f6e\u5df2\u91cd\u8f7d\u3002") },
                                    true
                                )
                                Command.SINGLE_SUCCESS
                            }
                    )
                    .then(
                        Commands.literal("set")
                            .then(booleanNode("announceOnDeath") { value -> copy(announceOnDeath = value) })
                            .then(booleanNode("allowPlayersUseCommand") { value -> copy(allowPlayersUseCommand = value) })
                            .then(booleanNode("adminsCanQueryOthers") { value -> copy(adminsCanQueryOthers = value) })
                            .then(booleanNode("allowTeleport") { value -> copy(allowTeleport = value) })
                    )
            )
        })
    }

    private fun booleanNode(
        name: String,
        updater: DeathReturnConfig.(Boolean) -> DeathReturnConfig
    ) = Commands.literal(name)
        .then(
            Commands.argument("value", BoolArgumentType.bool())
                .executes { context ->
                    val value = BoolArgumentType.getBool(context, "value")
                    DeathReturnConfigManager.update { current -> current.updater(value) }
                    context.source.sendSuccess({ Component.literal("$name = $value") }, true)
                    Command.SINGLE_SUCCESS
                }
        )
}
