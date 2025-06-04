package dev.deepslate.serverutility.territory

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.utils.SnowID
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import java.util.*

@EventBusSubscriber(modid = ServerUtility.ID)
object TownNameDisplayHandler {
    //player UUID -> town snowID
    private val playersLastStandTowns = mutableMapOf<UUID, SnowID?>()

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        playersLastStandTowns[player.uuid] = null
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        playersLastStandTowns.remove(player.uuid)
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        if (event.server.tickCount % 20 != 0) return

        val players = event.server.playerList.players
        players.forEach { p ->
            try {
                val town = TownManager[p.chunkPosition()] ?: return@forEach
                val oldTownID = playersLastStandTowns[p.uuid]
                if (oldTownID != town.id) {
                    playersLastStandTowns[p.uuid] = town.id
                    p.displayClientMessage(town.name, true)
                }
            } catch (e: Exception) {
                ServerUtility.LOGGER.error(e.stackTraceToString())
            }
        }
    }
}