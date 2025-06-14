package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object ProtectionPassOther : GameCommand {
    override val source: String = "protectionpass %s<flag> %s<player name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "flag" to SimpleSuggestionProvider { _ -> listOf("enable", "disable") },
        "player name" to SimpleSuggestionProvider.ONLINE_PLAYER_NAME
    )

    override val permissionRequired: String? = "serverutility.commands.protectionpass"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val flag = StringArgumentType.getString(context, "player name")
        val other = context.getArgument("player name", String::class.java)
        val otherPlayer = player.server.playerList.getPlayerByName(other)

        if (otherPlayer == null) {
            context.source.sendSystemMessage(Component.literal("Player not found."))
            return 0
        }

        when (flag) {
            "enable" -> TownManager.addTempPass(otherPlayer.uuid)
            "disable" -> TownManager.removeTempPass(otherPlayer.uuid)
            else -> return 0
        }

        return Command.SINGLE_SUCCESS
    }
}