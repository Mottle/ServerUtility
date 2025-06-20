package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object ListHomeOther : GameCommand {
    override val source: String = "listhome %s<player name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "player name" to SimpleSuggestionProvider.ONLINE_PLAYER_NAME
    )

    override val permissionRequired: String? = "serverutility.command.home.list"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val other = context.getArgument("player name", String::class.java)
        val otherPlayer = player.server.playerList.getPlayerByName(other)

        if (otherPlayer == null) {
            context.source.sendSystemMessage(Component.literal("Player not found."))
            return Command.SINGLE_SUCCESS
        }

        val homeStorage = SetHome.obtainHomeStorage() ?: throw CommandRuntimeException.of("Home storage is null.")
        val homes = homeStorage.query(otherPlayer) ?: emptySet()

        if (homes.isNotEmpty()) {
            context.source.sendSystemMessage(Component.literal(homes.joinToString("\n") { it.name }))
        } else {
            context.source.sendSystemMessage(Component.literal("No homes found."))
        }
        return Command.SINGLE_SUCCESS
    }
}