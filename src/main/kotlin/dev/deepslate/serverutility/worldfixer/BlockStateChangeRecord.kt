package dev.deepslate.serverutility.worldfixer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
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
    }
}