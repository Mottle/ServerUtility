package dev.deepslate.serverutility.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack

object Suicide : GameCommand {
    override val source: String = "suicide" +
            ""
    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String? = "serverutility.command.suicide"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0

        player.kill()

        return 0
    }
}