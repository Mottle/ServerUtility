package dev.deepslate.serverutility.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import dev.deepslate.serverutility.command.lexer.TokenType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument

internal class CommandBuilder {

    fun fromTokens(
        commands: List<TokenType>,
        callback: (CommandContext<CommandSourceStack>) -> Int,
        suggestionMap: Map<String, SuggestionProvider<CommandSourceStack>>
    ): LiteralArgumentBuilder<CommandSourceStack> {
        if (commands.isEmpty()) throw IllegalArgumentException("Command Builder: No commands provided")

        val first = commands.first() as? TokenType.Word
            ?: throw IllegalArgumentException("Command Builder: First command must be a word")

        if (commands.size == 1) return Commands.literal(first.word).executes(callback)

        val rest = commands.drop(1)
        val cmd = Commands.literal(first.word)

        return cmd.then(internalRec(rest, callback, suggestionMap))
    }

    private fun internalRec(
        commands: List<TokenType>,
        callback: (CommandContext<CommandSourceStack>) -> Int,
        suggestionMap: Map<String, SuggestionProvider<CommandSourceStack>>
    ): ArgumentBuilder<CommandSourceStack, *> {
        val first = commands.first()
        val rest = commands.drop(1)

        val cmd = when (val c = first) {
            is TokenType.Word -> Commands.literal(c.word)
            is TokenType.StringArg -> Commands.argument(c.word, StringArgumentType.word())
                .suggests(suggestionMap.getOrDefault(c.word, null))

            is TokenType.IntArg -> Commands.argument(c.word, IntegerArgumentType.integer())
                .suggests(suggestionMap.getOrDefault(c.word, null))

            is TokenType.FloatArg -> Commands.argument(c.word, FloatArgumentType.floatArg())
                .suggests(suggestionMap.getOrDefault(c.word, null))

            is TokenType.BoolArg -> Commands.argument(c.word, BoolArgumentType.bool())
                .suggests(suggestionMap.getOrDefault(c.word, null))

            is TokenType.ResourceLocationArg -> Commands.argument(c.word, ResourceLocationArgument.id())
                .suggests(suggestionMap.getOrDefault(c.word, null))
        }
        if (rest.isEmpty()) return cmd.executes(callback)
        return cmd.then(internalRec(rest, callback, suggestionMap))
    }
}