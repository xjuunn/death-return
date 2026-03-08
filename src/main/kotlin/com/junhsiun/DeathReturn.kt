package com.junhsiun

import com.junhsiun.config.DeathReturnConfigManager
import com.junhsiun.deathrecord.DeathPosAdminCommand
import com.junhsiun.deathrecord.DeathPosCommand
import com.junhsiun.deathrecord.DeathRecordStore
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.api.ModInitializer
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.LoggerFactory

object DeathReturn : ModInitializer {
    val logger = LoggerFactory.getLogger("death-return")

	override fun onInitialize() {
		DeathReturnConfigManager.load()

		ServerLifecycleEvents.SERVER_STARTED.register { server ->
			DeathRecordStore.load(server)
		}

		ServerLifecycleEvents.SERVER_STOPPING.register {
			DeathRecordStore.save()
		}

		ServerLivingEntityEvents.AFTER_DEATH.register(ServerLivingEntityEvents.AfterDeath { entity, _ ->
			if (entity !is ServerPlayerEntity) {
				return@AfterDeath
			}

			DeathRecordStore.recordDeath(entity)
			DeathPosCommand.notifyPlayer(entity)
		})

		DeathPosCommand.register()
		DeathPosAdminCommand.register()
		logger.info("Death Return 已加载")
	}
}
