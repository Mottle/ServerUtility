package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockEvent

data object BlockPlace : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) {
            val player = event.entity as? Player ?: return

            if (!TownManager.queryPermission(event.pos, player, BlockPlace).asBooleanWeakly()) {
                event.isCanceled = true
            }
        }
    }
}