package dev.deepslate.serverutility.territory

import com.mojang.serialization.Codec
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.saveddata.SavedData
import java.util.*

class SavedPlayerTowns(val townMap: HashMap<UUID, SnowID> = HashMap()) : SavedData() {

    companion object {
        const val KEY = "player_towns"

        val FACTORY = SavedData.Factory(::SavedPlayerTowns, ::load)

        private val stringBaseUUIDCodec = Codec.STRING.xmap(UUID::fromString, UUID::toString)

        private val mapCodec = Codec.unboundedMap(stringBaseUUIDCodec, SnowID.CODEC)

        private fun load(tag: CompoundTag, provider: HolderLookup.Provider): SavedPlayerTowns {
            try {
                val nbt = tag.get(KEY)
                val data = mapCodec.decode(NbtOps.INSTANCE, nbt).orThrow.first
                return SavedPlayerTowns(HashMap(data))
            } catch (e: IllegalStateException) {
                TownManager.logger.error("Failed to load player towns data", e)
                return SavedPlayerTowns()
            }
        }
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        try {
            val nbt = mapCodec.encodeStart(NbtOps.INSTANCE, townMap).orThrow
            tag.put(KEY, nbt)
        } catch (e: IllegalStateException) {
            TownManager.logger.error("Failed to save player towns data", e)
        }

        return tag
    }

    init {
        setDirty()
    }

    operator fun get(uuid: UUID): SnowID? {
        return townMap[uuid]
    }

    operator fun get(player: Player): SnowID? {
        return townMap[player.uuid]
    }

    fun add(uuid: UUID, town: SnowID) {
        townMap[uuid] = town
    }

    fun add(player: Player, town: Town) {
        townMap[player.uuid] = town.id
    }

    fun remove(uuid: UUID) {
        townMap.remove(uuid)
    }

    fun remove(player: Player) {
        townMap.remove(player.uuid)
    }
}