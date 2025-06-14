package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack

object Heal : GameCommand {
    override val source: String = "heal"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String? = "serverutility.command.heal"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        player.heal(player.maxHealth)

        return Command.SINGLE_SUCCESS
    }
}