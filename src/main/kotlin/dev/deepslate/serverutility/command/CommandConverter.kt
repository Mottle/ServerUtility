package dev.deepslate.serverutility.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.command.lexer.CommandLexer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

class CommandConverter {

    private val lexer = CommandLexer()

    private val builder = CommandBuilder()

    fun convert(command: GameCommand): LiteralArgumentBuilder<CommandSourceStack> {
        val tokens = lexer.scan(command.source)

        val execution = execution@{ context: CommandContext<CommandSourceStack> ->
            if (context.source.source is Player || context.source.source is Entity) {
                ServerUtility.LOGGER.info("${context.source.textName} executed ${command.asContextString()}")
            }
//            if (context.source.player != null) {
//                val player = context.source.player!!
//
//                if (!command.checkPermission(player)) {
//                    context.source.sendFailure(Component.literal("u dont have permission to use this command."))
//                    return@execution 0
//                }
//            }
            return@execution try {
                val ret = command.execute(context)

                if (ret == 0) context.source.sendFailure(Component.literal("An error occurred while executing this command."))

                ret
            } catch (e: Exception) {
                context.source.sendFailure(Component.literal("An error occurred while executing this command."))
                ServerUtility.LOGGER.error("Error while executing command ${command.asContextString()}.")
                ServerUtility.LOGGER.error(e.stackTraceToString())
                0
            }
        }

        val rawCommand = builder.fromTokens(tokens, execution, command.suggestions).requires { source ->
            if (source.source is MinecraftServer) return@requires true

            val player = source.player ?: return@requires false

            if (!command.checkPermission(player)) {
                source.sendFailure(Component.literal("u dont have permission to use this command."))
                return@requires false
            }

            return@requires true
        }
        return rawCommand
    }
}