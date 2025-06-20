package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.territory.Territory
import dev.deepslate.serverutility.territory.TerritoryManager
import dev.deepslate.serverutility.territory.Town
import dev.deepslate.serverutility.territory.TownManager
import dev.deepslate.serverutility.territory.protection.group.AllAllow
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object CreateTown : GameCommand {
    override val source: String = "createtown %s<name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String = "serverutility.command.createtown"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val name = StringArgumentType.getString(context, "name")

        if (name in TownManager) {
            context.source.sendFailure(
                Component.literal("Town with that name already exists.").withStyle(ChatFormatting.RED)
            )
            return Command.SINGLE_SUCCESS
        }

        if (TownManager.town(player) != null) {
            context.source.sendFailure(Component.literal("You are already in a town.").withStyle(ChatFormatting.RED))
            return Command.SINGLE_SUCCESS
        }

        if (TerritoryManager[player.level()].includes(player.chunkPosition())) {
            context.source.sendFailure(
                Component.literal("You are already in a territory.").withStyle(ChatFormatting.RED)
            )
            return Command.SINGLE_SUCCESS
        }

        val territory = Territory.of(player.level()) + player.chunkPosition()
        val town = Town.of(player, name).addTerritory(territory)
        val updatedProtection = town.protection.addGroup(AllAllow).addPermissionRecord(player, AllAllow)
        val updatedTown = town.copy(protection = updatedProtection)

        TerritoryManager.manage(territory)
        TownManager.manage(updatedTown)
        TownManager.applyTown(player, updatedTown)

        context.source.sendSuccess({ Component.literal("Town created.").withStyle(ChatFormatting.GREEN) }, false)
        return Command.SINGLE_SUCCESS
    }
}