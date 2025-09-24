package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object FeedOther : GameCommand {
    override val source: String = "feed %s<player name>"
    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "player name" to SimpleSuggestionProvider.ONLINE_PLAYER_NAME
    )

    override val permissionRequired: String = "serverutility.command.feed"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val server = player.server
        val other = StringArgumentType.getString(context, "player name")
        val otherPlayer = server.playerList.getPlayerByName(other)

        if (otherPlayer == null) {
            context.source.sendFailure(Component.literal("Player not found!"))
            return Command.SINGLE_SUCCESS
        }

        val foodData = otherPlayer.foodData

        foodData.eat(1000, 1000f)

        context.source.sendSuccess({ Component.literal("Fed ${otherPlayer.name}!") }, false)

        return Command.SINGLE_SUCCESS
    }
}