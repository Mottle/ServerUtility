package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.server.ServerLifecycleHooks

object SpawnHome : GameCommand {
    override val source: String = "home %s<name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> =
        mapOf("name" to Suggestions.HOME_SUGGESTION)

    override val permissionRequired: String = "serverutility.command.home.spawn"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val homeStorage = SetHome.obtainHomeStorage() ?: throw CommandRuntimeException.of("Home storage is null.")
        val homeName = StringArgumentType.getString(context, "name")
        val home = homeStorage.query(player, homeName)

        if (home == null) {
            context.source.sendSystemMessage(Component.literal("No home with that name found."))
            return Command.SINGLE_SUCCESS
        }

        val level = ServerLifecycleHooks.getCurrentServer()?.getLevel(home.dimension) ?: return 0
        player.teleportTo(level, home.pos.x.toDouble(), home.pos.y.toDouble(), home.pos.z.toDouble(), 0f, 0f)

        return Command.SINGLE_SUCCESS
    }
}