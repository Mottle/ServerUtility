package dev.deepslate.serverutility.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.deepslate.serverutility.command.lexer.CommandLexer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

class CommandConverter {

    private val lexer = CommandLexer()

    private val builder = CommandBuilder()

    fun convert(command: GameCommand): LiteralArgumentBuilder<CommandSourceStack> {
        val tokens = lexer.scan(command.source)

        val execution = execution@{ context: CommandContext<CommandSourceStack> ->

            if (context.source.player != null) {
                val player = context.source.player!!

                if (!command.checkPermission(player)) {
                    context.source.sendFailure(Component.literal("u dont have permission to use this command"))
                    return@execution 0
                }
            }
            return@execution command.execute(context)
        }

        val rawCommand = builder.fromTokens(tokens, execution, command.suggestions)
        return rawCommand
    }
}