package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.common.NeoForgeMod

object Fly : GameCommand {
    override val source: String = "fly %s<flag>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = mapOf(
        "flag" to SimpleSuggestionProvider { _ -> listOf("enable", "disable") }
    )

    override val permissionRequired: String? = "serverutility.command.fly"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val flag = context.getArgument("flag", String::class.java)

        val attribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
            ?: throw CommandRuntimeException.of("Player does not have creative flight attribute.")

        attribute.baseValue = if (flag == "enable") 1.0 else 0.0

        context.source.sendSuccess(
            { Component.literal("Flight ${if (flag == "enable") "enabled" else "disabled"}") },
            false
        )

        return Command.SINGLE_SUCCESS
    }
}