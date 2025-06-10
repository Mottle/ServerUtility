package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.server.ServerLifecycleHooks

object ListHome : GameCommand {
    override val source: String = "listhome"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String? = "serverutility.home.list"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val homeStorage = ServerLifecycleHooks.getCurrentServer()?.overworld()?.dataStorage?.computeIfAbsent(
            SetHome.SavedHomes.FACTORY, SetHome.SavedHomes.KEY
        ) ?: return 0
        val homes = homeStorage.query(player)
        if (homes != null && homes.isNotEmpty()) {
            context.source.sendSystemMessage(Component.literal(homes.joinToString("\n") { it.name }))
        } else {
            context.source.sendSystemMessage(Component.literal("No homes found."))
        }
        return Command.SINGLE_SUCCESS
    }
}