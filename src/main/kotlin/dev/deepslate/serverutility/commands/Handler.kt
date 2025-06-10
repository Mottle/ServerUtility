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

//    val permissionRequired = mapOf(
//        "gamemode" to "minecraft.gamemode",
//        "tp" to "minecraft.teleport",
//        "ban" to "minecraft.ban",
//        "unban" to "minecraft.unban",
//        "kick" to "minecraft.kick",
//        "op" to "minecraft.op",
//        "deop" to "minecraft.deop",
//        "whitelist" to "minecraft.whitelist",
//        "attribute" to "minecraft.attribute",
//        "ban-ip" to "minecraft.ban_ip",
//        "clear" to "minecraft.clear",
//        "damage" to "minecraft.damage",
//        "data" to "minecraft.data",
//        "datapack" to "minecraft.datapack",
//    )
//
//    @SubscribeEvent(priority = EventPriority.HIGH)
//    fun onUseCommand(event: CommandEvent) {
//        val context = event.parseResults.context
//
//        if (!context.source.isPlayer) return
//        if (event.exception != null) return
//        if (context.nodes.isEmpty()) return
//
//        val commandText = context.nodes.first().node.usageText
//
//        if (commandText !in permissionRequired) return
//
//        val permissionReq = permissionRequired[commandText]!!
//
//        if (!PermissionManager.query(context.source.player!!, permissionReq).asBooleanStrictly()) {
//            context.source.sendFailure(Component.literal("You do not have permission to use this command."))
//            event.isCanceled = true
//        }
//    }
}