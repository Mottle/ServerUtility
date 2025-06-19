package dev.deepslate.serverutility.utils

import com.github.yitter.idgen.YitIdHelper
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class SnowID(val value: Long) {
    companion object {
        val CODEC: Codec<SnowID> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.LONG.fieldOf("value").forGetter(SnowID::value)
            ).apply(instance, ::SnowID)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, SnowID> =
            StreamCodec.composite(ByteBufCodecs.VAR_LONG, SnowID::value, ::SnowID)

        fun generate() = YitIdHelper.nextId().let(::SnowID)

        val EMPTY = SnowID(0)

        val NEGATIVE = SnowID(-1)
    }

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SnowID

        return value == other.value
    }

    fun debugComponent(): Component =
        Component.literal("<snow id: ").withStyle(ChatFormatting.BLUE)
            .append(Component.literal("$value").withColor(0x87cefa).withStyle(ChatFormatting.ITALIC)).append(
                Component.literal(">").withStyle(ChatFormatting.BLUE)
            )
}