package com.junhsiun.deathrecord

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.junhsiun.DeathReturn
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.LevelResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

object DeathRecordStore {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val mapType = object : TypeToken<MutableMap<String, DeathRecord>>() {}.type

    private val records = mutableMapOf<UUID, DeathRecord>()
    private var storagePath: Path? = null

    fun load(server: MinecraftServer) {
        // 使用世界存档目录，保证不同存档之间互不干扰。
        val dir = server.getWorldPath(LevelResource.ROOT).resolve("death-return")
        val file = dir.resolve("death-records.json")
        storagePath = file
        records.clear()

        if (!Files.exists(file)) {
            return
        }

        runCatching {
            Files.newBufferedReader(file).use { reader ->
                val loaded = gson.fromJson<MutableMap<String, DeathRecord>>(reader, mapType) ?: mutableMapOf()
                loaded.forEach { (uuid, record) ->
                    records[UUID.fromString(uuid)] = record
                }
            }
        }.onFailure {
            DeathReturn.logger.error("读取死亡坐标记录失败: {}", file, it)
        }
    }

    fun save() {
        val file = storagePath ?: return
        val dir = file.parent ?: return

        runCatching {
            Files.createDirectories(dir)
            val export = records.mapKeys { it.key.toString() }
            Files.newBufferedWriter(file).use { writer ->
                gson.toJson(export, mapType, writer)
            }
        }.onFailure {
            DeathReturn.logger.error("保存死亡坐标记录失败: {}", file, it)
        }
    }

    fun recordDeath(player: ServerPlayer) {
        val pos = player.blockPosition()
        // 只保留最近一次死亡点，保持功能和数据结构足够简单。
        records[player.uuid] = DeathRecord(
            dimension = player.level().dimension().location().toString(),
            x = pos.x,
            y = pos.y,
            z = pos.z,
            timestamp = System.currentTimeMillis()
        )
        save()
    }

    fun get(playerUuid: UUID): DeathRecord? = records[playerUuid]
}
