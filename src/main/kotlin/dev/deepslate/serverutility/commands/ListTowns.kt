package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object ListTowns : GameCommand {
    override val source: String = "listtowns"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String = "serverutility.command.listtowns"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val towns = TownManager.towns()

        if (towns.isNotEmpty()) {
            towns.forEachIndexed { idx, town ->
                context.source.sendSystemMessage(town.debugComponent(idx))
            }
        } else {
            context.source.sendSystemMessage(Component.literal("No towns found."))
        }

        return Command.SINGLE_SUCCESS
    }
}