package com.junhsiun.deathrecord

import com.junhsiun.config.DeathReturnConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.Permissions
import net.minecraft.world.entity.Relative
import net.minecraft.world.level.Level

object DeathPosCommand {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("deathpos")
                    .executes { context ->
                        val player = context.source.playerOrException
                        if (!canUseBaseCommand(context.source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))) {
                            context.source.sendFailure(
                                Component.literal(
                                    "\u7ba1\u7406\u5458\u5df2\u7981\u7528\u666e\u901a\u73a9\u5bb6\u4f7f\u7528 /deathpos\u3002"
                                )
                            )
                            return@executes Command.SINGLE_SUCCESS
                        }

                        sendHistory(player, player, 1)
                        Command.SINGLE_SUCCESS
                    }
                    .then(
                        Commands.literal("page")
                            .then(
                                Commands.argument("page", IntegerArgumentType.integer(1))
                                    .executes { context ->
                                        val player = context.source.playerOrException
                                        if (!canUseBaseCommand(context.source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))) {
                                            context.source.sendFailure(
                                                Component.literal(
                                                    "\u7ba1\u7406\u5458\u5df2\u7981\u7528\u666e\u901a\u73a9\u5bb6\u4f7f\u7528 /deathpos\u3002"
                                                )
                                            )
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        sendHistory(
                                            viewer = player,
                                            target = player,
                                            page = IntegerArgumentType.getInteger(context, "page")
                                        )
                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
                    .then(
                        Commands.literal("tp")
                            .then(
                                Commands.argument("index", IntegerArgumentType.integer(1, 20))
                                    .executes { context ->
                                        val player = context.source.playerOrException
                                        val allowAdminBypass = context.source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
                                        if (!canTeleport(allowAdminBypass)) {
                                            context.source.sendFailure(
                                                Component.literal(
                                                    "\u7ba1\u7406\u5458\u5df2\u5173\u95ed\u6b7b\u4ea1\u70b9\u4f20\u9001\u3002"
                                                )
                                            )
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        teleportToRecord(player, IntegerArgumentType.getInteger(context, "index"))
                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
                    .then(
                        Commands.literal("player")
                            .requires { source ->
                                source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) &&
                                    DeathReturnConfigManager.config.adminsCanQueryOthers
                            }
                            .then(
                                Commands.argument("target", EntityArgument.player())
                                    .executes { context ->
                                        val viewer = context.source.playerOrException
                                        val target = EntityArgument.getPlayer(context, "target")
                                        sendHistory(viewer, target, 1)
                                        Command.SINGLE_SUCCESS
                                    }
                                    .then(
                                        Commands.literal("page")
                                            .then(
                                                Commands.argument("page", IntegerArgumentType.integer(1))
                                                    .executes { context ->
                                                        val viewer = context.source.playerOrException
                                                        val target = EntityArgument.getPlayer(context, "target")
                                                        sendHistory(
                                                            viewer = viewer,
                                                            target = target,
                                                            page = IntegerArgumentType.getInteger(context, "page")
                                                        )
                                                        Command.SINGLE_SUCCESS
                                                    }
                                            )
                                    )
                            )
                    )
            )
        })
    }

    fun notifyPlayer(player: ServerPlayer) {
        if (!DeathReturnConfigManager.config.announceOnDeath) {
            return
        }

        val record = DeathRecordStore.getLatest(player.getUUID()) ?: return
        player.sendSystemMessage(
            DeathPosFormatter.buildDeathNotice(
                record = record,
                allowCommand = DeathReturnConfigManager.config.allowPlayersUseCommand,
                allowTeleport = DeathReturnConfigManager.config.allowTeleport
            )
        )
    }

    private fun sendHistory(viewer: ServerPlayer, target: ServerPlayer, page: Int) {
        val history = DeathRecordStore.getHistory(target.getUUID())
        if (history.isEmpty()) {
            val message = if (viewer.getUUID() == target.getUUID()) {
                "\u4f60\u8fd8\u6ca1\u6709\u6b7b\u4ea1\u8bb0\u5f55\u3002"
            } else {
                "${target.name.string} \u8fd8\u6ca1\u6709\u6b7b\u4ea1\u8bb0\u5f55\u3002"
            }
            viewer.sendSystemMessage(Component.literal(message))
            return
        }

        val pageSize = DeathPosFormatter.pageSize()
        val maxPage = ((history.size - 1) / pageSize) + 1
        val safePage = page.coerceIn(1, maxPage)
        val startIndex = (safePage - 1) * pageSize
        val pageRecords = history.drop(startIndex).take(pageSize)
        val canTeleport = viewer.getUUID() == target.getUUID() && canTeleport(false)

        val title = if (viewer.getUUID() == target.getUUID()) {
            "\u6700\u8fd1\u6b7b\u4ea1\u8bb0\u5f55"
        } else {
            "${target.name.string} \u7684\u6b7b\u4ea1\u8bb0\u5f55"
        }

        viewer.sendSystemMessage(DeathPosFormatter.buildHistoryHeader(title, safePage, maxPage))
        pageRecords.forEach { entry ->
            viewer.sendSystemMessage(DeathPosFormatter.buildHistoryLine(entry, canTeleport))
        }

        val baseCommand = if (viewer.getUUID() == target.getUUID()) {
            "/deathpos page"
        } else {
            "/deathpos player ${target.name.string} page"
        }
        DeathPosFormatter.buildPager(safePage, maxPage, baseCommand)?.let(viewer::sendSystemMessage)
    }

    private fun teleportToRecord(player: ServerPlayer, index: Int) {
        val record = DeathRecordStore.getRecord(player.getUUID(), index)
        if (record == null) {
            player.sendSystemMessage(
                Component.literal("\u627e\u4e0d\u5230\u7b2c $index \u6761\u6b7b\u4ea1\u8bb0\u5f55\u3002")
            )
            return
        }

        val targetLevel = resolveLevel(player, record.dimension)
        if (targetLevel == null) {
            player.sendSystemMessage(
                Component.literal("\u76ee\u6807\u7ef4\u5ea6\u4e0d\u5b58\u5728\uff0c\u65e0\u6cd5\u4f20\u9001\u3002")
            )
            return
        }

        val success = player.teleportTo(
            targetLevel,
            record.x + 0.5,
            record.y.toDouble(),
            record.z + 0.5,
            emptySet<Relative>(),
            player.getYRot(),
            player.getXRot(),
            true
        )

        if (!success) {
            player.sendSystemMessage(
                Component.literal("\u4f20\u9001\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002")
            )
            return
        }

        player.sendSystemMessage(
            Component.literal(
                "\u5df2\u4f20\u9001\u81f3 #$index ${DeathPosFormatter.formatDimension(record.dimension)} ${DeathPosFormatter.formatLocationText(record)}"
            )
        )
    }

    private fun resolveLevel(player: ServerPlayer, dimension: String): ServerLevel? {
        val id = extractDimensionId(dimension) ?: return null
        val key = ResourceKey.create(Level.RESOURCE_KEY, Identifier.parse(id))
        return player.level().getServer()?.getLevel(key)
    }

    private fun extractDimensionId(raw: String): String? {
        return when {
            raw.startsWith("ResourceKey[") && raw.contains("/ ") -> raw.substringAfter("/ ").substringBefore("]")
            raw.contains(":") -> raw
            else -> null
        }
    }

    private fun canUseBaseCommand(isAdmin: Boolean): Boolean {
        return DeathReturnConfigManager.config.allowPlayersUseCommand || isAdmin
    }

    private fun canTeleport(isAdmin: Boolean): Boolean {
        return DeathReturnConfigManager.config.allowTeleport || isAdmin
    }
}
