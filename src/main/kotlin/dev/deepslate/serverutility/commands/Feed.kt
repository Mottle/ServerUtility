package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object Feed : GameCommand {
    override val source: String = "feed"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String = "serverutility.command.feed"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val foodData = player.foodData

        foodData.eat(1000, 1000f)

        context.source.sendSuccess({ Component.literal("Feed Successful!") }, true)

        return Command.SINGLE_SUCCESS
    }
}