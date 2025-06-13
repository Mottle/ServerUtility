package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.commands.CommandSourceStack

object ProtectionPass : GameCommand {
    override val source: String = "protectionpass %s<flag>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "flag" to SimpleSuggestionProvider { _ -> listOf("enable", "disable") }
    )

    override val permissionRequired: String? = "serverutility.commands.protectionpass"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val flag = context.getArgument("flag", String::class.java)

        if (flag != "enable") TownManager.removeTempPass(player.uuid) else TownManager.addTempPass(player.uuid)

        return Command.SINGLE_SUCCESS
    }
}