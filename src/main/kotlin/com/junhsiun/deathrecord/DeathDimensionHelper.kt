package com.junhsiun.deathrecord

object DeathDimensionHelper {
    fun normalize(raw: String): String {
        return when {
            raw.startsWith("ResourceKey[") && raw.contains("/ ") -> {
                raw.substringAfter("/ ").substringBefore("]")
            }

            raw.contains(":") -> raw
            else -> raw
        }
    }

    fun displayName(raw: String): String {
        val dimensionId = normalize(raw)
        return when (dimensionId) {
            "minecraft:overworld" -> "\u4e3b\u4e16\u754c"
            "minecraft:the_nether" -> "\u4e0b\u754c"
            "minecraft:the_end" -> "\u672b\u5730"
            else -> dimensionId
        }
    }
}
