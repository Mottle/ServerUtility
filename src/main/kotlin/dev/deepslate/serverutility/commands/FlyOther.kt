package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.common.NeoForgeMod

object FlyOther : GameCommand {
    override val source: String = "fly %s<flag> %s<player name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "flag" to SimpleSuggestionProvider { _ -> listOf("enable", "disable") },
        "player name" to SimpleSuggestionProvider.ONLINE_PLAYER_NAME
    )

    override val permissionRequired: String = "serverutility.command.fly"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val flag = context.getArgument("flag", String::class.java)
        val other = context.getArgument("player name", String::class.java)
        val otherPlayer = player.server.playerList.getPlayerByName(other)

        if (otherPlayer == null) {
            context.source.sendFailure(Component.literal("Player $other not found."))
            return Command.SINGLE_SUCCESS
        }

        val attribute = otherPlayer.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            ?: throw CommandRuntimeException.of("Player does not have creative flight attribute.")

        attribute.baseValue = if (flag == "enable") 1.0 else 0.0

        context.source.sendSuccess({ Component.literal("Set $other's flight to $flag.") }, true)

        return Command.SINGLE_SUCCESS
    }
}