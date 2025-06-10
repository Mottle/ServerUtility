package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.server.ServerLifecycleHooks

object SpawnHomeDefault : GameCommand {
    override val source: String = "home"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String? = "serverutility.home.spawn"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val server = ServerLifecycleHooks.getCurrentServer() ?: return 0
        val homeStorage = server.overworld().dataStorage.computeIfAbsent(
            SetHome.SavedHomes.FACTORY, SetHome.SavedHomes.KEY
        ) ?: throw CommandRuntimeException.of("Home storage is null.")
        val home = homeStorage.query(player, "home")

        if (home == null) {
            context.source.sendSystemMessage(Component.literal("No home with that name found."))
            return 0
        }
        val level = server.getLevel(home.dimension) ?: return 0
        player.teleportTo(level, home.pos.x.toDouble(), home.pos.y.toDouble(), home.pos.z.toDouble(), 0f, 0f)

        return Command.SINGLE_SUCCESS
    }
}