package dev.deepslate.serverutility.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack

object SetHome : GameCommand {
    override val source: String
        get() = "sethome %s<name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>>
        get() = emptyMap()

    override val permissionRequired: String? = "serverutility.sethome"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        TODO("Not yet implemented")
    }
}