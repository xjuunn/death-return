package com.junhsiun.deathrecord

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.junhsiun.DeathReturn
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.LevelResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

object DeathRecordStore {
    private const val MAX_HISTORY = 20

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val recordType = DeathRecord::class.java
    private val records = mutableMapOf<UUID, MutableList<DeathRecord>>()
    private var storagePath: Path? = null

    fun load(server: MinecraftServer) {
        val dir = server.getWorldPath(LevelResource.ROOT).resolve("death-return")
        val file = dir.resolve("death-records.json")
        storagePath = file
        records.clear()

        if (!Files.exists(file)) {
            return
        }

        runCatching {
            Files.newBufferedReader(file).use { reader ->
                val root = gson.fromJson(reader, JsonObject::class.java) ?: JsonObject()
                root.entrySet().forEach { (uuidString, value) ->
                    val uuid = runCatching { UUID.fromString(uuidString) }.getOrNull() ?: return@forEach
                    val history = parseHistory(value)
                    if (history.isNotEmpty()) {
                        records[uuid] = history.toMutableList()
                    }
                }
            }
        }.onFailure { throwable ->
            DeathReturn.logger.error("Failed to read death records: {}", file, throwable)
        }
    }

    fun save() {
        val file = storagePath ?: return
        val dir = file.parent ?: return

        runCatching {
            Files.createDirectories(dir)
            val export = JsonObject()
            records.forEach { (uuid, history) ->
                val array = JsonArray()
                history.take(MAX_HISTORY).forEach { record ->
                    array.add(gson.toJsonTree(record, recordType))
                }
                export.add(uuid.toString(), array)
            }

            Files.newBufferedWriter(file).use { writer ->
                gson.toJson(export, writer)
            }
        }.onFailure { throwable ->
            DeathReturn.logger.error("Failed to save death records: {}", file, throwable)
        }
    }

    fun recordDeath(player: ServerPlayer) {
        val pos = player.blockPosition()
        val history = records.getOrPut(player.getUUID()) { mutableListOf() }

        history.add(
            0,
            DeathRecord(
                dimension = player.level().dimension().toString(),
                x = pos.getX(),
                y = pos.getY(),
                z = pos.getZ(),
                timestamp = System.currentTimeMillis()
            )
        )

        if (history.size > MAX_HISTORY) {
            history.subList(MAX_HISTORY, history.size).clear()
        }

        save()
    }

    fun getLatest(playerUuid: UUID): DeathRecord? = records[playerUuid]?.firstOrNull()

    fun getHistory(playerUuid: UUID): List<DeathHistoryEntry> {
        return records[playerUuid]
            ?.mapIndexed { index, record -> DeathHistoryEntry(index + 1, record) }
            ?: emptyList()
    }

    fun getRecord(playerUuid: UUID, index: Int): DeathRecord? {
        if (index <= 0) {
            return null
        }

        return records[playerUuid]?.getOrNull(index - 1)
    }

    private fun parseHistory(element: JsonElement): List<DeathRecord> {
        return when {
            element.isJsonArray -> {
                element.asJsonArray.mapNotNull { item ->
                    runCatching { gson.fromJson(item, recordType) }.getOrNull()
                }.take(MAX_HISTORY)
            }

            element.isJsonObject -> {
                listOfNotNull(runCatching { gson.fromJson(element, recordType) }.getOrNull())
            }

            else -> emptyList()
        }
    }
}
