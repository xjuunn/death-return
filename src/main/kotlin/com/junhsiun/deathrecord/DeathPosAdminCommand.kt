package com.junhsiun.deathrecord

import com.junhsiun.config.DeathReturnConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text

object DeathPosAdminCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                literal("deathposadmin")
                    .requires { source -> source.hasPermissionLevel(2) }
                    .then(literal("reload").executes { context ->
                        DeathReturnConfigManager.load()
                        context.source.sendFeedback({ Text.literal("Death Return 配置已重载。") }, true)
                        Command.SINGLE_SUCCESS
                    })
                    .then(
                        literal("set")
                            .then(
                                literal("announceOnDeath")
                                    .then(argument("value", BoolArgumentType.bool()).executes { context ->
                                        val value = BoolArgumentType.getBool(context, "value")
                                        DeathReturnConfigManager.update { it.copy(announceOnDeath = value) }
                                        context.source.sendFeedback(
                                            { Text.literal("announceOnDeath = $value") },
                                            true
                                        )
                                        Command.SINGLE_SUCCESS
                                    })
                            )
                            .then(
                                literal("allowPlayersUseCommand")
                                    .then(argument("value", BoolArgumentType.bool()).executes { context ->
                                        val value = BoolArgumentType.getBool(context, "value")
                                        DeathReturnConfigManager.update { it.copy(allowPlayersUseCommand = value) }
                                        context.source.sendFeedback(
                                            { Text.literal("allowPlayersUseCommand = $value") },
                                            true
                                        )
                                        Command.SINGLE_SUCCESS
                                    })
                            )
                            .then(
                                literal("adminsCanQueryOthers")
                                    .then(argument("value", BoolArgumentType.bool()).executes { context ->
                                        val value = BoolArgumentType.getBool(context, "value")
                                        DeathReturnConfigManager.update { it.copy(adminsCanQueryOthers = value) }
                                        context.source.sendFeedback(
                                            { Text.literal("adminsCanQueryOthers = $value") },
                                            true
                                        )
                                        Command.SINGLE_SUCCESS
                                    })
                            )
                    )
            )
        })
    }
}
