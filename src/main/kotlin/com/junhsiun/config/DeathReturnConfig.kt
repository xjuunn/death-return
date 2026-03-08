package com.junhsiun.config

import com.google.gson.GsonBuilder
import com.junhsiun.DeathReturn
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path

data class DeathReturnConfig(
    val announceOnDeath: Boolean = true,
    val allowPlayersUseCommand: Boolean = true,
    val adminsCanQueryOthers: Boolean = true
)

object DeathReturnConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configPath: Path = FabricLoader.getInstance().configDir.resolve("death-return.json")

    @Volatile
    var config: DeathReturnConfig = DeathReturnConfig()
        private set

    fun load() {
        if (!Files.exists(configPath)) {
            save()
            return
        }

        runCatching {
            Files.newBufferedReader(configPath).use { reader ->
                config = gson.fromJson(reader, DeathReturnConfig::class.java) ?: DeathReturnConfig()
            }
        }.onFailure {
            DeathReturn.logger.error("读取配置失败: {}", configPath, it)
            config = DeathReturnConfig()
        }
    }

    fun update(transform: (DeathReturnConfig) -> DeathReturnConfig) {
        config = transform(config)
        save()
    }

    private fun save() {
        runCatching {
            Files.createDirectories(configPath.parent)
            Files.newBufferedWriter(configPath).use { writer ->
                gson.toJson(config, writer)
            }
        }.onFailure {
            DeathReturn.logger.error("保存配置失败: {}", configPath, it)
        }
    }
}
