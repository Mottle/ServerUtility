package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import dev.deepslate.serverutility.territory.TerritoryManager
import dev.deepslate.serverutility.territory.Town
import dev.deepslate.serverutility.territory.TownManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object RemoveTown : GameCommand {
    override val source: String = "removetown %s<name>"
    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "name" to SimpleSuggestionProvider { TownManager.towns().map(Town::name) }
    )
    override val permissionRequired: String = "serverutility.command.removetown"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val name = context.getArgument("name", String::class.java)
        val town = TownManager.findByName(name)

        if (town == null) {
            context.source.sendFailure(Component.literal("Town not found!"))
            return 0
        }

        TownManager.unmanage(town)
        town.territoryIDs.forEach(TerritoryManager::unmanage)
        (town.members.memories + town.members.owner).forEach(TownManager::deapplyTown)

        return Command.SINGLE_SUCCESS
    }
}