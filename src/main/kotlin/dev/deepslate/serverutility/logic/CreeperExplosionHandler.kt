package dev.deepslate.serverutility.logic

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.configuration.WorldProtectionConfiguration
import net.minecraft.world.entity.monster.Creeper
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.ExplosionEvent

@EventBusSubscriber(modid = ServerUtility.ID)
object CreeperExplosionHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onExplosion(event: ExplosionEvent.Detonate) {
        if (WorldProtectionConfiguration.CREEPER_EXPLOSION_DESTROY_BLOCK.get()) return
        if (event.explosion.directSourceEntity !is Creeper) return

        event.affectedBlocks.clear()
    }
}