package dev.deepslate.serverutility.territory

import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.utils.SnowID
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import org.slf4j.LoggerFactory

class TerritoryManager(val forLevel: ResourceKey<Level>) {

    val level get() = ServerLifecycleHooks.getCurrentServer()?.getLevel(forLevel)

    companion object {
        private val managerMap: Object2ObjectOpenHashMap<ResourceKey<Level>, TerritoryManager> =
            Object2ObjectOpenHashMap()

        operator fun get(level: Level): TerritoryManager =
            managerMap.computeIfAbsent(level.dimension()) { TerritoryManager(level.dimension()) }

        operator fun get(id: SnowID) = managerMap.values.find { m -> m.contains(id) }?.get(id)

        fun findTerritoryIncludingChunk(chunkPos: ChunkPos) =
            managerMap.values.map { m -> m.findTerritoryIncludingChunk(chunkPos) }.firstOrNull()

        fun manage(territory: Territory) {
            managerMap[territory.dimension]?.manage(territory)
        }

        private val logger = LoggerFactory.getLogger(TerritoryManager::class.java)
    }

    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onServerStarted(event: ServerStartedEvent) {
            managerMap.clear()
            val server = event.server
            logger.info("Loading territories...")
            server.allLevels.forEach { level ->

                logger.info("Loading territories for ${level.dimension().location()} begin.")
                val saved = level.dataStorage.get(SavedTerritories.FACTORY, SavedTerritories.KEY).let { data ->
                    if (data == null) {
                        logger.info("No saved territories data found for ${level.dimension().location()}.")
                        logger.info("Creating new territories data for ${level.dimension().location()}.")
                        SavedTerritories.of().let {
                            level.dataStorage.set(SavedTerritories.KEY, it)
                            it
                        }
                    } else data
                }

                val manager = TerritoryManager(level.dimension())

                saved.data.forEach(manager::manage)
                managerMap[level.dimension()] = manager
                logger.info("Loaded ${manager.managedTerritory.size} territories for ${level.dimension().location()}.")
            }
            logger.info("Loaded territories finished.")
        }

        @SubscribeEvent
        fun onServerStopping(event: ServerStoppingEvent) {
            val server = event.server
            logger.info("Saving territories...")
            server.allLevels.forEach { level ->
                logger.info("Saving territories for ${level.dimension().location()} begin.")
                val data = get(level).managedTerritory.values.toList()
                level.dataStorage.set(SavedTerritories.KEY, SavedTerritories(data))
                logger.info("Saved ${data.size} territories for ${level.dimension().location()}.")
            }
            logger.info("Saving territories finished.")
        }
    }

    private val managedTerritory: Long2ObjectOpenHashMap<Territory> = Long2ObjectOpenHashMap()

    fun manage(territory: Territory) {
        managedTerritory[territory.id.value] = territory
    }

    operator fun get(id: SnowID): Territory? = managedTerritory[id.value]

    fun includes(chunkPos: ChunkPos) = managedTerritory.values.any { t -> t.contains(chunkPos) }

    fun findTerritoryIncludingChunk(chunkPos: ChunkPos) = managedTerritory.values.find { t -> t.contains(chunkPos) }

    fun contains(id: SnowID) = managedTerritory.contains(id.value)

    private data class SavedTerritories(val data: List<Territory>) : SavedData() {

        companion object {
            private val codec = Territory.CODEC.listOf()

            const val KEY = "territories"

            fun of() = SavedTerritories(emptyList())

            val FACTORY = Factory(::of, ::load)

            private fun load(tag: CompoundTag, provider: HolderLookup.Provider): SavedTerritories {
                val nbt = tag.get(KEY)

                try {
                    val list = codec.decode(NbtOps.INSTANCE, nbt).orThrow.first

                    return SavedTerritories(list)
                } catch (e: IllegalStateException) {
                    logger.error("Failed to load territories.")
                    logger.error(e.toString())
                }
                return of()
            }
        }


        init {
            markUnsaved()
        }

        fun markUnsaved() {
            isDirty = true
        }

        override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
            try {
                val nbtData = codec.encodeStart(NbtOps.INSTANCE, data).orThrow
                tag.put(KEY, nbtData)
            } catch (e: IllegalStateException) {
                logger.error("Failed to save territories.")
                logger.error(e.stackTraceToString())
            }

            return tag
        }
    }
}