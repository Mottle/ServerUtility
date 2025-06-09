package dev.deepslate.serverutility.utils

import com.mojang.serialization.Codec
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.server.ServerLifecycleHooks

object Codecs {
    val COMPONENT_CODEC: Codec<Component> =
        Codec.STRING.xmap({
            try {
                Component.Serializer
                    .fromJson(it, ServerLifecycleHooks.getCurrentServer()!!.registryAccess())
            } catch (e: Exception) {
                Component.literal("")
            }
        }, {
            try {
                Component.Serializer.toJson(it, ServerLifecycleHooks.getCurrentServer()!!.registryAccess())
            } catch (e: Exception) {
                ""
            }
        })
}