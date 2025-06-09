package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

object Inventory : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onPlayerUseInventoryBlock(event: PlayerInteractEvent.RightClickBlock) {
            if (event.side == LogicalSide.CLIENT) return

            val entity = event.entity
            val blockPos = event.pos
            val level = event.level
//            val side = event.face ?: return
            val state = level.getBlockState(blockPos)

//            if(level.getCapability(Capabilities.ItemHandler.BLOCK, blockPos, side) == null) return
            if (!state.`is`(Blocks.CHEST) && !state.`is`(Blocks.BARREL)) return

            if (TownManager.queryPermission(blockPos, entity, Inventory).asBooleanWeakly()) return
            event.isCanceled = true
        }
    }
}