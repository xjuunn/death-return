package com.junhsiun.deathrecord

import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object DeathPosCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                literal("deathpos").executes { context ->
                    val player = context.source.playerOrThrow
                    val record = DeathRecordStore.get(player.uuid)
                    if (record == null) {
                        context.source.sendFeedback({ Text.literal("你还没有死亡记录。") }, false)
                        return@executes Command.SINGLE_SUCCESS
                    }

                    val message = formatRecord(record)
                    context.source.sendFeedback({ Text.literal("最近死亡坐标: $message") }, false)
                    Command.SINGLE_SUCCESS
                }
            )
        })
    }

    fun notifyPlayer(player: ServerPlayerEntity) {
        val record = DeathRecordStore.get(player.uuid) ?: return
        player.sendMessage(Text.literal("你已死亡，坐标已记录: ${formatRecord(record)}"))
    }

    private fun formatRecord(record: DeathRecord): String {
        return "[${record.dimension}] (${record.x}, ${record.y}, ${record.z})"
    }
}
