package dev.deepslate.serverutility.territory.protection.permission

object Inventory : ProtectionPermission {
//    @EventBusSubscriber(modid = ServerUtility.ID)
//    object Handler {
//        @SubscribeEvent
//        fun onPlayerUseInventoryBlock(event: PlayerInteractEvent.RightClickBlock) {
//            if (event.side == LogicalSide.CLIENT) return
//
//            val entity = event.entity
//            val blockPos = event.pos
//            val level = event.level
////            val side = event.face ?: return
//            val state = level.getBlockState(blockPos)
//
////            if(level.getCapability(Capabilities.ItemHandler.BLOCK, blockPos, side) == null) return
//            if (!state.`is`(Blocks.CHEST) && !state.`is`(Blocks.BARREL)) return
//
//            if (TownManager.queryPermission(blockPos, entity, Inventory).asBooleanWeakly()) return
//            event.isCanceled = true
//            entity.sendSystemMessage(Component.literal("You do not have permission to access this inventory!"))
//        }
//    }
}