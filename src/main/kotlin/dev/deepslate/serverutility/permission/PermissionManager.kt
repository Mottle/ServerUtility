package dev.deepslate.serverutility.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.permission.integration.LuckPermsProvider
import dev.deepslate.serverutility.permission.integration.SimplePermissionProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import org.slf4j.LoggerFactory

private var instance: PermissionProvider = SimplePermissionProvider()

object PermissionManager : PermissionProvider by instance {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        private val logger = LoggerFactory.getLogger(PermissionManager::class.java)

        @SubscribeEvent
        fun onServerAboutToStart(event: ServerAboutToStartEvent) {
            val luckPerms = FMLLoader.getLoadingModList().mods.any { mod -> mod.modId == "luckperms" }

            if (luckPerms) {
                instance = LuckPermsProvider()
            }
            logger.info("Using ${instance.name} for permissions")
        }
    }
}