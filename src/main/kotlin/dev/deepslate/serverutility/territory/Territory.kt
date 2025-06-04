package dev.deepslate.serverutility.territory

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.deepslate.serverutility.utils.SnowID
import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level

data class Territory(val id: SnowID, val dimension: ResourceKey<Level>, val includeChunks: List<ChunkPos>) {
    companion object {
        fun of() = Territory(
            SnowID.generate(),
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld")),
            emptyList()
        )

        fun of(level: Level) = Territory(SnowID.generate(), level.dimension(), emptyList())

        val CODEC: Codec<Territory> = RecordCodecBuilder.create { instance ->
            instance.group(
                SnowID.CODEC.fieldOf("id").forGetter(Territory::id),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Territory::dimension),
                Codec.LONG.listOf().fieldOf("packed_chunk_positions")
                    .forGetter { list -> list.includeChunks.map(ChunkPos::toLong) }
            ).apply(instance) { id, dimension, packedChunkPosList ->
                Territory(id, dimension, packedChunkPosList.map(::ChunkPos))
            }
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, Territory> = StreamCodec.composite(
            SnowID.STREAM_CODEC, Territory::id,
            ResourceKey.streamCodec(Registries.DIMENSION), Territory::dimension,
            ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list()), { t -> t.includeChunks.map(ChunkPos::toLong) },
        ) { id, dimension, packedChunkPosList -> Territory(id, dimension, packedChunkPosList.map(::ChunkPos)) }
    }

    fun contains(chunkPos: ChunkPos) = includeChunks.contains(chunkPos)

    infix operator fun plus(chunkPos: ChunkPos) =
        if (!contains(chunkPos)) copy(includeChunks = includeChunks + chunkPos) else this

    infix operator fun plus(chunkPositions: List<ChunkPos>) =
        copy(includeChunks = (includeChunks + chunkPositions).distinct())

    infix operator fun minus(chunkPos: ChunkPos) = copy(includeChunks = includeChunks - chunkPos)

    val chunkCount = includeChunks.size

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Territory) return false
        if (other.id == id && other.dimension == dimension) return true
        return false
    }

    override fun hashCode(): Int {
        var result = chunkCount
        result = 31 * result + id.hashCode()
        result = 31 * result + dimension.hashCode()
//        result = 31 * result + includeChunks.hashCode()
        return result
    }
}