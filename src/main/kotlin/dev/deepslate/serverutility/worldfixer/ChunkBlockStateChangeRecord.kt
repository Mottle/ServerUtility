package dev.deepslate.serverutility.worldfixer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos

data class ChunkBlockStateChangeRecord(
    private val positionSet: MutableSet<Long>,
    private val records: MutableList<BlockStateChangeRecord>
) {

    companion object {
        fun of(records: List<BlockStateChangeRecord>): ChunkBlockStateChangeRecord = ChunkBlockStateChangeRecord(
            records.map { it.pos.asLong() }.toMutableSet(),
            records.toMutableList()
        )

        fun empty(): ChunkBlockStateChangeRecord = ChunkBlockStateChangeRecord(mutableSetOf(), mutableListOf())

        val CODEC: Codec<ChunkBlockStateChangeRecord> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockStateChangeRecord.CODEC.listOf().fieldOf("records")
                    .forGetter(ChunkBlockStateChangeRecord::records)
            ).apply(instance, ::of)
        }
    }

    fun contains(pos: BlockPos) = positionSet.contains(pos.asLong())

    fun insert(record: BlockStateChangeRecord): Boolean {
        if (contains(record.pos)) return false

        forceInsert(record)

        return true
    }

    fun forceInsert(record: BlockStateChangeRecord) {
        positionSet.add(record.pos.asLong())
        records += record
    }

    fun isEmpty() = records.isEmpty()

    fun isNotEmpty() = records.isNotEmpty()

    fun peek() = records.firstOrNull()

    fun poll(): BlockStateChangeRecord? {
        val record = records.removeFirstOrNull() ?: return null
        positionSet.remove(record.pos.asLong())
        return record
    }

    fun take(n: Int): List<BlockStateChangeRecord> {
        val result = mutableListOf<BlockStateChangeRecord>()

        repeat(n) {
            val record = poll() ?: return@repeat
            result += record
        }

        return result.toList()
    }

    fun takeWith(n: Int, until: (BlockStateChangeRecord) -> Boolean): List<BlockStateChangeRecord> {
        val result = mutableListOf<BlockStateChangeRecord>()

        repeat(n) {
            if (peek() == null) return@repeat
            if (!until(peek()!!)) return@repeat
            val record = poll()!!
            result += record
        }

        return result.toList()
    }

    val size: Int
        get() = records.size
}