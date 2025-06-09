package dev.deepslate.serverutility.worldfixer

import dev.deepslate.serverutility.Calendar
import dev.deepslate.serverutility.ModAttachments
import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.task.ScheduledExecutable
import dev.deepslate.serverutility.task.ServerScheduler
import dev.deepslate.serverutility.task.StateSetter
import dev.deepslate.serverutility.task.TaskState
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ChunkHolder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.level.ExplosionEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks

//@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = ServerUtility.ID)
object WorldFixer {
//    private val cache: MutableList<BlockStateChangeRecord> = mutableListOf()

    fun getChunkRecord(level: ServerLevel, pos: BlockPos): ChunkBlockStateChangeRecord =
        level.getData(ModAttachments.BLOCK_STATE_CHANGE_RECORD)

    fun insert(record: BlockStateChangeRecord, level: ServerLevel) {
        val chunk = level.getChunk(record.pos)
        val chunkRecord = chunk.getData(ModAttachments.BLOCK_STATE_CHANGE_RECORD)

        if (chunkRecord.insert(record)) chunk.isUnsaved = true
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onEntityBreakBlock(event: BlockEvent.BreakEvent) {
        if (event.level.isClientSide) return
        if (event.isCanceled) return

        val level = event.level as? ServerLevel ?: return
//        val dimension = level.dimension()
        val state = event.state
        val stamp = Calendar.stamp
        val record = BlockStateChangeRecord(stamp, event.pos, state)

        insert(record, level)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onEntityPlaceBlock(event: BlockEvent.EntityPlaceEvent) {
        if (event.level.isClientSide) return
        if (event.isCanceled) return
        if (event.entity == null) return

        val level = event.level as? ServerLevel ?: return
        val stamp = Calendar.stamp
        val record = BlockStateChangeRecord(stamp, event.pos, Blocks.AIR.defaultBlockState())

        insert(record, level)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onExplosion(event: ExplosionEvent.Detonate) {
        if (event.level.isClientSide) return

        val level = event.level as? ServerLevel ?: return
        val blocks = event.affectedBlocks
        val exp = event.explosion
        exp.blockInteraction
        for (pos in blocks) {
            val state = level.getBlockState(pos)
            if (state.isAir) continue
            val stamp = Calendar.stamp
            val record = BlockStateChangeRecord(stamp, pos, state)

            insert(record, level)
        }
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        val server = event.server
        val ticks = event.server.tickCount

        val tickInterval = 20 * 10
        val maxFixPerTick = 5
        val fixDelay = 20 * 60 * 60
//        val maxFixed = tickInterval * maxFixPerTick

        if (ticks % tickInterval != 0) return

        val fixTasksMap = mutableMapOf<ResourceKey<Level>, MutableList<List<BlockStateChangeRecord>>>()
        for (level in server.allLevels) {
            val fixTasks = mutableListOf<List<BlockStateChangeRecord>>()
            val chunks = level.chunkSource.chunkMap.chunks
            val tickingChunks = chunks.mapNotNull(ChunkHolder::getTickingChunk).shuffled()
            val chunkRecords =
                tickingChunks.associateWith { chunk -> chunk.getData(ModAttachments.BLOCK_STATE_CHANGE_RECORD) }
                    .filter { (_, r) -> r.isNotEmpty() }

            for ((chunk, record) in chunkRecords) {
                val chunkFix = record.takeWith(maxFixPerTick) { r -> r.stamp < Calendar.stamp - fixDelay }

                if (chunkFix.isEmpty()) continue

                chunk.isUnsaved = true
                fixTasks.add(chunkFix)
            }

            fixTasksMap[level.dimension()] = fixTasks

//            val size = chunkRecords.sumOf(ChunkBlockStateChangeRecord::size)
//
//            if (size == 0) continue
//
//            val fixPerChunk = if (maxFixed >= size) maxFixed / chunkRecords.size else 1
//            val blocksNeedFix = chunkRecords.map { record -> record.take(fixPerChunk) }.flatten()
//            val fixGroup = blocksNeedFix.chunked(maxFixPerTick)
//
//            fixGroup.forEach(fixTasks::addLast)
//            fixTasksMap[level] = fixTasks
        }

        if (fixTasksMap.isNotEmpty()) ServerScheduler.INSTANCE.schedule(generateWorldFixExecutable(fixTasksMap))
    }

    fun generateWorldFixExecutable(fixList: Map<ResourceKey<Level>, MutableList<List<BlockStateChangeRecord>>>) =
        object : ScheduledExecutable {

            val tasks = fixList

            override fun execute(stateSetter: StateSetter) {
                for ((dimension, fixGroup) in tasks) {
                    val blocksNeedFix = fixGroup.removeFirstOrNull()
                    val server = ServerLifecycleHooks.getCurrentServer()
                    val level = server?.getLevel(dimension)

                    if (blocksNeedFix == null) continue

                    blocksNeedFix.forEach { record ->
                        level?.setBlockAndUpdate(record.pos, record.changed)
                    }
                }

                if (tasks.all { (_, fixTask) -> fixTask.isEmpty() }) stateSetter.invoke(TaskState.COMPLETED)
            }

        }
}