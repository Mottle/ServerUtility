package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.TownManager
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockEvent

object BlockBreak : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        fun onBlockBreak(event: BlockEvent.BreakEvent) {
            val player = event.player
            val chunkPos = player.chunkPosition()
            val town = TownManager[chunkPos] ?: return
            val queryResult = town.protection.query(player, BlockBreak)
            if (queryResult == PermissionQueryResult.DENY) {
                event.isCanceled = true
                return
            }
        }
    }
}