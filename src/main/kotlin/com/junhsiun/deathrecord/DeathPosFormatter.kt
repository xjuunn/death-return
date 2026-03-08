package com.junhsiun.deathrecord

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DeathPosFormatter {
    private const val PAGE_SIZE = 5
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
        .withZone(ZoneId.systemDefault())

    fun pageSize(): Int = PAGE_SIZE

    fun buildDeathNotice(record: DeathRecord, allowCommand: Boolean, allowTeleport: Boolean): Component {
        val message = Component.empty()
            .append(Component.literal("\u6b7b\u4ea1\u5750\u6807\u5df2\u8bb0\u5f55 ").withStyle(ChatFormatting.GRAY))
            .append(formatLocation(record).withStyle(ChatFormatting.GOLD))

        if (!allowCommand) {
            return message
        }

        val withHistory = message
            .append(Component.literal(" ").withStyle(ChatFormatting.GRAY))
            .append(
                actionButton(
                    label = "[\u67e5\u770b\u5386\u53f2]",
                    command = "/deathpos",
                    hover = "\u67e5\u770b\u6700\u8fd1 20 \u6b21\u6b7b\u4ea1\u8bb0\u5f55",
                    color = ChatFormatting.AQUA
                )
            )

        if (!allowTeleport) {
            return withHistory
        }

        return withHistory
            .append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
            .append(
                actionButton(
                    label = "[\u4f20\u9001\u56de\u53bb]",
                    command = "/deathpos tp 1",
                    hover = "\u4f20\u9001\u5230\u6700\u8fd1\u4e00\u6b21\u6b7b\u4ea1\u70b9",
                    color = ChatFormatting.GREEN
                )
            )
    }

    fun buildHistoryHeader(title: String, page: Int, maxPage: Int): Component {
        return Component.empty()
            .append(Component.literal("\u25c6 ").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(title).withStyle(ChatFormatting.GOLD))
            .append(Component.literal(" ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("\u7b2c $page/$maxPage \u9875").withStyle(ChatFormatting.GRAY))
    }

    fun buildHistoryLine(entry: DeathHistoryEntry, canTeleport: Boolean): Component {
        val record = entry.record
        val line = Component.empty()
            .append(Component.literal("#${entry.index} ").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(formatDimension(record.dimension)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
            .append(formatLocation(record).withStyle(ChatFormatting.WHITE))
            .append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(formatTime(record.timestamp)).withStyle(ChatFormatting.GRAY))

        if (!canTeleport) {
            return line
        }

        return line
            .append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
            .append(
                actionButton(
                    label = "[\u4f20\u9001]",
                    command = "/deathpos tp ${entry.index}",
                    hover = "\u4f20\u9001\u5230\u7b2c ${entry.index} \u6761\u6b7b\u4ea1\u8bb0\u5f55",
                    color = ChatFormatting.GREEN
                )
            )
    }

    fun buildPager(page: Int, maxPage: Int, baseCommand: String): Component? {
        if (maxPage <= 1) {
            return null
        }

        val pager = Component.empty()
        if (page > 1) {
            pager.append(
                actionButton(
                    label = "[\u4e0a\u4e00\u9875]",
                    command = "$baseCommand ${page - 1}",
                    hover = "\u67e5\u770b\u7b2c ${page - 1} \u9875",
                    color = ChatFormatting.AQUA
                )
            )
            pager.append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
        }

        pager.append(Component.literal("\u7b2c $page/$maxPage \u9875").withStyle(ChatFormatting.GRAY))

        if (page < maxPage) {
            pager.append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
            pager.append(
                actionButton(
                    label = "[\u4e0b\u4e00\u9875]",
                    command = "$baseCommand ${page + 1}",
                    hover = "\u67e5\u770b\u7b2c ${page + 1} \u9875",
                    color = ChatFormatting.AQUA
                )
            )
        }

        return pager
    }

    fun formatLocation(record: DeathRecord): MutableComponent {
        return Component.literal("(${record.x}, ${record.y}, ${record.z})")
    }

    fun formatLocationText(record: DeathRecord): String {
        return "(${record.x}, ${record.y}, ${record.z})"
    }

    fun formatDimension(dimension: String): String {
        return when (dimension) {
            "ResourceKey[minecraft:dimension / minecraft:overworld]" -> "\u4e3b\u4e16\u754c"
            "ResourceKey[minecraft:dimension / minecraft:the_nether]" -> "\u4e0b\u754c"
            "ResourceKey[minecraft:dimension / minecraft:the_end]" -> "\u672b\u5730"
            else -> dimension.substringAfterLast("/")
                .substringBefore("]")
                .replace("minecraft:", "")
        }
    }

    private fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(timestamp))
    }

    private fun actionButton(label: String, command: String, hover: String, color: ChatFormatting): MutableComponent {
        return Component.literal(label).setStyle(
            Style.EMPTY
                .withColor(color)
                .withClickEvent(ClickEvent.RunCommand(command))
                .withHoverEvent(HoverEvent.ShowText(Component.literal(hover)))
        )
    }
}
