package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockEvent

data object BlockBreak : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onBlockBreak(event: BlockEvent.BreakEvent) {
            val player = event.player
            val queryResult = TownManager.queryPermission(event.pos, player, BlockBreak)

            if (!queryResult.asBooleanWeakly()) {
                event.isCanceled = true
                player.sendSystemMessage(Component.literal("You do not have permission to break blocks here!"))
            }
        }
    }
}