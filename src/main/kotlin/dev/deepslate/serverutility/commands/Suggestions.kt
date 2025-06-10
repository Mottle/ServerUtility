package dev.deepslate.serverutility.commands

import dev.deepslate.serverutility.command.suggestion.SimpleSuggestionProvider
import net.neoforged.neoforge.server.ServerLifecycleHooks

object Suggestions {
    val HOME_SUGGESTION = SimpleSuggestionProvider { ctx ->
        val player = ctx.source.player ?: return@SimpleSuggestionProvider emptyList()
        val server = ServerLifecycleHooks.getCurrentServer() ?: return@SimpleSuggestionProvider emptyList()
        val homeRecords =
            server.overworld().dataStorage.computeIfAbsent(SetHome.SavedHomes.FACTORY, SetHome.SavedHomes.KEY)
        val homes = homeRecords.query(player) ?: return@SimpleSuggestionProvider emptyList()
        return@SimpleSuggestionProvider homes.map(SetHome.HomeRecord::name)
    }
}