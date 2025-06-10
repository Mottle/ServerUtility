package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.commands.SetHome.HomeRecord
import dev.deepslate.serverutility.commands.SetHome.SavedHomes
import net.minecraft.commands.CommandSourceStack
import net.neoforged.neoforge.server.ServerLifecycleHooks

object SetHomeDefault : GameCommand {
    override val source: String = "sethome"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String = "serverutility.home.set"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val homes = ServerLifecycleHooks.getCurrentServer()?.overworld()?.dataStorage?.computeIfAbsent(
            SavedHomes.FACTORY, SavedHomes.KEY
        ) ?: return 0
        homes.insert(
            player, HomeRecord(
                "home",
                player.level().dimension(),
                player.blockPosition()
            )
        )
        homes.markUnsaved()

        return Command.SINGLE_SUCCESS
    }
}