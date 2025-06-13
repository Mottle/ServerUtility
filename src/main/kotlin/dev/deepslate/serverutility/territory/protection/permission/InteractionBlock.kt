package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

data object InteractionBlock : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {

        @SubscribeEvent
        fun onRightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
            val player = event.entity as? ServerPlayer ?: return
            val pos = event.pos
            val level = event.level
            val state = level.getBlockState(pos)

            if (state.`is`(Blocks.CHEST) && state.`is`(Blocks.BARREL)) {
                if (TownManager.queryPermission(pos, player, Inventory).asBooleanWeakly()) return
                event.isCanceled = true
                player.sendSystemMessage(Component.literal("You do not have permission to access this inventory!"))
                return
            }

            if (TownManager.queryPermission(pos, player, InteractionBlock).asBooleanWeakly()) return

            player.sendSystemMessage(Component.literal("You do not have permission to interact blocks here!"))
            event.isCanceled = true
        }

        //和break冲突
//        @SubscribeEvent
//        fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
//            val player = event.entity as? ServerPlayer ?: return
//            val pos = event.pos
//
//            if (TownManager.queryPermission(pos, player, InteractionBlock).asBooleanWeakly()) return
//
//            player.sendSystemMessage(Component.literal("You do not have permission to interact blocks here!"))
//            event.isCanceled = true
//        }
    }
}