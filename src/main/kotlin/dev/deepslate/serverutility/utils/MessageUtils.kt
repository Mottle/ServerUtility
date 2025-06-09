package dev.deepslate.serverutility.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object MessageUtils {
    fun sendTitleMessage(player: Player, title: Component) {
        if (player !is ServerPlayer) return
        player.connection.send(ClientboundSetTitleTextPacket(title))
    }

    fun sendSubtitleMessage(player: Player, title: Component) {
        if (player !is ServerPlayer) return
        player.connection.send(ClientboundSetSubtitleTextPacket(title))
    }

    fun clearTitle(player: Player) {
        if (player !is ServerPlayer) return
        player.connection.send(ClientboundClearTitlesPacket(true))
    }

    fun setTitleFadeTime(player: Player, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (player !is ServerPlayer) return
        player.connection.send(ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut))
    }
}