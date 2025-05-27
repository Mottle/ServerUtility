package dev.deepslate.serverutility.task

import dev.deepslate.serverutility.ServerUtility
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent

@EventBusSubscriber(modid = ServerUtility.ID)
object ServerSchedulerHandler {
    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        ServerScheduler.INSTANCE.process()
    }
}