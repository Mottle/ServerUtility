package dev.deepslate.serverutility.command

//import dev.deepslate.fallacy.permission.PermissionManager
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.commands.CommandSourceStack

interface GameCommand {
    val source: String

    val suggestions: Map<String, SuggestionProvider<CommandSourceStack>>

    val permissionRequired: String?

    fun execute(context: CommandContext<CommandSourceStack>): Int

//    fun checkPermission(player: ServerPlayer): Boolean =
//        if (permissionRequired != null) PermissionManager.query(player, permissionRequired!!).asBoolean() else true
}