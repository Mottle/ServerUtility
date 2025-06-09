package dev.deepslate.serverutility.territory

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.utils.MessageUtils
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*

@EventBusSubscriber(modid = ServerUtility.ID)
object TownNameDisplayHandler {
    //player UUID -> town snowID
    private val playersLastStandTowns = mutableMapOf<UUID, SnowID?>()

    private val playerLastDisplayTicks = mutableMapOf<UUID, Int>()

//    private val logger = LoggerFactory.getLogger(TownNameDisplayHandler::class.java)

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
        if (event.server.tickCount % 40 != 0) return

        val players = event.server.playerList.players
        players.forEach { p ->
            val town = TownManager[p.chunkPosition()]
            val oldTownID = playersLastStandTowns[p.uuid]

            if (oldTownID != town?.id) {
                playersLastStandTowns[p.uuid] = town?.id

                if (town?.id != null) {
                    trySendMessage(p, town.displayName)
                } else {
                    val message = Component.literal("Wild").withStyle(ChatFormatting.GREEN)
                    trySendMessage(p, message)
                }
            }
        }
    }

    private fun trySendMessage(player: Player, message: Component) {
        val uuid = player.uuid
        val playerLastDisplayTick = playerLastDisplayTicks.computeIfAbsent(uuid) { 0 }
        val currentTick = ServerLifecycleHooks.getCurrentServer()?.tickCount ?: 0

        if (currentTick - playerLastDisplayTick <= 5 * 20) return

        MessageUtils.setTitleFadeTime(player, 10, 40, 10)
        MessageUtils.sendTitleMessage(player, message)
    }
}