package dev.deepslate.serverutility.territory

import dev.deepslate.serverutility.ModAttachments
import dev.deepslate.serverutility.ServerUtility
import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.WildProtection
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import org.slf4j.LoggerFactory
import java.util.*

object TownManager {

    private val logger = LoggerFactory.getLogger(TownManager::class.java)

    private val tempPasses = mutableSetOf<UUID>()

    fun addTempPass(uuid: UUID) {
        tempPasses += uuid
    }

    fun removeTempPass(uuid: UUID) {
        tempPasses -= uuid
    }

    fun towns() = managedTown.values.toList()

    fun town(player: Player) = player.getData(ModAttachments.TOWN_BELONG).let { this[it] }

    fun applyTown(player: Player, town: Town) {
        player.setData(ModAttachments.TOWN_BELONG, town.id)
    }

    fun deapplyTown(player: Player) {
        player.setData(ModAttachments.TOWN_BELONG, SnowID.EMPTY)
    }

//    companion object {
//        private val logger = LoggerFactory.getLogger(TownManager::class.java)
//
//        val INSTANCE: TownManager = TownManager()
//
//        operator fun get(chunkPos: ChunkPos) = INSTANCE[chunkPos]
//
//        operator fun get(blockPos: BlockPos) = INSTANCE[ChunkPos(blockPos)]
//
//        fun queryPermission(chunkPos: ChunkPos, uuid: UUID, permission: ProtectionPermission) =
//            INSTANCE.queryPermission(chunkPos, uuid, permission)
//
//        fun queryPermission(blockPos: BlockPos, uuid: UUID, permission: ProtectionPermission) =
//            INSTANCE.queryPermission(ChunkPos(blockPos), uuid, permission)
//
//        fun queryPermission(chunkPos: ChunkPos, player: Player, permission: ProtectionPermission) =
//            INSTANCE.queryPermission(chunkPos, player.uuid, permission)
//
//        fun queryPermission(blockPos: BlockPos, player: Player, permission: ProtectionPermission) =
//            INSTANCE.queryPermission(blockPos, player.uuid, permission)
//    }

    @EventBusSubscriber(modid = ServerUtility.ID)
    object Handler {
        @SubscribeEvent
        fun onServerStarted(event: ServerStartedEvent) {
            managedTown.clear()

            val server = event.server
            val overworld = server.overworld()

            logger.info("Loading towns...")

            val saved = overworld.dataStorage.get(SavedTowns.FACTORY, SavedTowns.KEY).let { data ->
                if (data == null) {
                    logger.info("No saved towns data found.")
                    logger.info("Creating new towns data.")

                    SavedTowns.of().let {
                        overworld.dataStorage.set(SavedTowns.KEY, it)
                        it
                    }
                } else data
            }

            saved.data.forEach(::manage)
            logger.info("Loaded ${managedTown.size} towns.")
            logger.info("Loaded towns finished.")

//            testCode()
        }

        @SubscribeEvent
        fun onServerStopping(event: ServerStoppingEvent) {
            val server = event.server
            val overworld = server.overworld()

            logger.info("Saving towns...")

            val data = managedTown.values.toList()

            overworld.dataStorage.set(SavedTowns.KEY, SavedTowns(data))
            logger.info("Saved ${data.size} towns.")
            logger.info("Saving towns finished.")
        }

        @SubscribeEvent
        fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
            val player = event.entity
            val town = town(player)

            if (town != null) deapplyTown(player)
            logger.info("Player ${player.name} logged in. Town not existed, deapplied.")
        }
    }

    var wildProtection: WildProtection = WildProtection()
        private set

    private val managedTown = Long2ObjectOpenHashMap<Town>()

//    private fun testCode() {
//        if (managedTown.isEmpty()) {
//            val town = createManagedTown()
//            val territory = Territory.of() + listOf(ChunkPos(0, 0), ChunkPos(1, 0), ChunkPos(0, 1), ChunkPos(1, 1))
//            val updatedTown = town.addTerritory(territory)
//            TerritoryManager.manage(territory)
//            manage(updatedTown)
//        }
//    }

    fun queryPermission(chunkPos: ChunkPos, uuid: UUID, permission: ProtectionPermission) =
        if (uuid in tempPasses) PermissionQueryResult.ALLOW else (get(chunkPos)?.protection
            ?: wildProtection).query(uuid, permission)

    fun queryPermission(blockPos: BlockPos, uuid: UUID, permission: ProtectionPermission) =
        queryPermission(ChunkPos(blockPos), uuid, permission)

    fun queryPermission(chunkPos: ChunkPos, player: Player, permission: ProtectionPermission) =
        queryPermission(chunkPos, player.uuid, permission)

    fun queryPermission(blockPos: BlockPos, player: Player, permission: ProtectionPermission) =
        queryPermission(blockPos, player.uuid, permission)

//    fun createManagedTown(): Town {
//        val town = Town(
//            SnowID.generate(), UUID.randomUUID().toString(), emptyList(), Town.TownMemories(), Component.literal("TestTown").withStyle(
//                ChatFormatting.YELLOW
//            )
//        )
//        manage(town)
//        return town
//    }

    fun manage(town: Town) {
        managedTown[town.id.value] = town
    }

    fun unmanage(town: Town) {
        managedTown.remove(town.id.value)
    }

    operator fun get(id: SnowID): Town? = managedTown[id.value]

    operator fun get(chunkPos: ChunkPos): Town? = managedTown.values.find { town -> town.contains(chunkPos) }

    operator fun get(blockPos: BlockPos): Town? = get(ChunkPos(blockPos))

    fun findByName(name: String) = managedTown.values.find { t -> t.name == name }

    fun findByPlayer(player: Player) = managedTown.values.find { t -> player in t }

    operator fun contains(name: String) = findByName(name) != null

    private data class SavedTowns(val data: List<Town>) : SavedData() {

        companion object {

            private val codec = Town.CODEC.listOf()

            fun of() = SavedTowns(emptyList())

            private fun load(tag: CompoundTag, provider: HolderLookup.Provider): SavedTowns {
                val nbt = tag.get(KEY)

                try {
                    val list = codec.decode(NbtOps.INSTANCE, nbt).orThrow.first

                    return SavedTowns(list)
                } catch (e: IllegalStateException) {
                    logger.error("Failed to load towns.")
                    logger.error(e.stackTraceToString())
                }
                return of()
            }

            val FACTORY = Factory(::of, ::load)

            const val KEY = "towns"
        }

        init {
            markUnsaved()
        }

        override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
            try {
                val nbt = codec.encodeStart(NbtOps.INSTANCE, data).orThrow
                tag.put(KEY, nbt)
            } catch (e: IllegalStateException) {
                logger.error("Failed to save towns.")
                logger.error(e.stackTraceToString())
            }

            return tag
        }

        fun markUnsaved() {
            isDirty = true
        }
    }
}