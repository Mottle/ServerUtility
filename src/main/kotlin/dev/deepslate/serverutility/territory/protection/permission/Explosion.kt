package dev.deepslate.serverutility.territory.protection.permission

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.ExplosionEvent

data object Explosion : ProtectionPermission {
    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onExplosion(event: ExplosionEvent.Start) {
            val explosion = event.explosion
            val directSource = explosion.directSourceEntity
            val indirectSource = explosion.indirectSourceEntity
            val sourcePlayer = (indirectSource ?: directSource) as? Player
            val explosionPos = BlockPos(explosion.center().x.toInt(), 0, explosion.center().z.toInt())

            if (sourcePlayer != null) {
                if (!TownManager.queryPermission(explosionPos, sourcePlayer, Explosion).asBooleanWeakly()) {
                    event.isCanceled = true
                }
            } else {
                if (!TownManager.wildProtection.queryDefault(NatureExplosion).asBooleanWeakly()) event.isCanceled = true
            }
        }

        @SubscribeEvent
        fun onExplosionDetonate(event: ExplosionEvent.Detonate) {
            val explosion = event.explosion
            val directSource = explosion.directSourceEntity
            val indirectSource = explosion.indirectSourceEntity
            val sourcePlayer = (indirectSource ?: directSource) as? Player

            if (sourcePlayer == null) {
                if (!TownManager.wildProtection.queryDefault(NatureExplosion).asBooleanWeakly()) {
                    event.affectedBlocks.clear()
                    event.affectedEntities.clear()
                }
                return
            }

            //取消实体影响
            event.affectedEntities.groupBy { e -> e.chunkPosition() }.forEach { (chunkPos, entities) ->
                val protection = TownManager[chunkPos]?.protection ?: TownManager.wildProtection

                if (!protection.query(sourcePlayer, Explosion).asBooleanWeakly()) event.affectedEntities.removeAll(
                    entities
                )
            }

            //取消方块破坏
            event.affectedBlocks.groupBy(::ChunkPos).forEach { (chunkPos, blocks) ->
                val protection = TownManager[chunkPos]?.protection ?: TownManager.wildProtection

                if (!protection.query(sourcePlayer, Explosion).asBooleanWeakly()) event.affectedBlocks.removeAll(blocks)
            }
        }
    }
}