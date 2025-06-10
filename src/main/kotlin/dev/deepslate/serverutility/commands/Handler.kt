package dev.deepslate.serverutility.commands

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.command.CommandConverter
import dev.deepslate.serverutility.permission.PermissionManager
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import org.slf4j.LoggerFactory

@EventBusSubscriber(modid = ServerUtility.ID)
object Handler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onRegisterCommand(event: RegisterCommandsEvent) {
        val dispatcher = event.dispatcher
        val converter = CommandConverter()
        val logger = LoggerFactory.getLogger("command register")

        Commands.commands.forEach { cmd ->
            try {
                val raw = converter.convert(cmd).requires { context ->
                    if (!context.isPlayer) return@requires true
                    if (cmd.permissionRequired == null) return@requires true
                    return@requires PermissionManager.query(context.player!!, cmd.permissionRequired!!)
                        .asBooleanStrictly()
                }
                dispatcher.register(raw)
            } catch (e: Exception) {
                logger.error("Failed to register command ${cmd.asContextString()}.")
                logger.error(e.stackTraceToString())
            }
        }
    }
}