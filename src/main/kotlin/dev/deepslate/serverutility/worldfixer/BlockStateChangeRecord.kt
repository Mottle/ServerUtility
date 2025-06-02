package dev.deepslate.serverutility.worldfixer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState

data class BlockStateChangeRecord(
    val stamp: Long,
//    val dimension: ResourceLocation,
    val pos: BlockPos,
    val changed: BlockState,
//    val new: BlockState
) {
    companion object {
        val CODEC: Codec<BlockStateChangeRecord> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.LONG.fieldOf("stamp").forGetter(BlockStateChangeRecord::stamp),
//                ResourceLocation.CODEC.fieldOf("dimension").forGetter(BlockStateChangeRecord::dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(BlockStateChangeRecord::pos),
                BlockState.CODEC.fieldOf("changed").forGetter(BlockStateChangeRecord::changed),
//                BlockState.CODEC.fieldOf("new").forGetter(BlockStateChangeRecord::new)
            ).apply(builder, ::BlockStateChangeRecord)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, BlockStateChangeRecord> = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            BlockStateChangeRecord::stamp,
            BlockPos.STREAM_CODEC,
            BlockStateChangeRecord::pos,
            ResourceLocation.STREAM_CODEC,
            { r ->
                r.changed.block.let(BuiltInRegistries.BLOCK::getKeyOrNull)
                    ?: ResourceLocation.withDefaultNamespace("air")
            },
        ) { stamp, pos, blockID ->
            BlockStateChangeRecord(stamp, pos, BuiltInRegistries.BLOCK.get(blockID).defaultBlockState())
        }
    }
}