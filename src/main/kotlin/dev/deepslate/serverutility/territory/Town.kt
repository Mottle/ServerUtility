package dev.deepslate.serverutility.territory

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.deepslate.serverutility.territory.protection.Protection
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*

data class Town(
    val id: SnowID,
    val territoryIDs: List<SnowID>,
    val members: List<UUID>,
    val name: Component,
    val protection: Protection = Protection()
) {
    companion object {
        val CODEC: Codec<Town> = RecordCodecBuilder.create { instance ->
            instance.group(
                SnowID.CODEC.fieldOf("id").forGetter(Town::id),
                SnowID.CODEC.listOf().fieldOf("territory_ids").forGetter(Town::territoryIDs),
                UUIDUtil.CODEC.listOf().fieldOf("members").forGetter(Town::members),
                Codec.STRING.fieldOf("name").forGetter { t ->
                    try {
                        Component.Serializer.toJson(
                            t.name, ServerLifecycleHooks.getCurrentServer()!!.registryAccess()
                        )
                    } catch (e: Exception) {
                        ""
                    }
                },
            ).apply(instance) { id, territories, members, nameJson ->
                val fallback = Component.literal("未命名领地")
                val nameComponent = try {
                    Component.Serializer.fromJson(nameJson, ServerLifecycleHooks.getCurrentServer()!!.registryAccess())
                } catch (e: Exception) {
                    fallback
                } ?: fallback
                Town(id, territories, members, nameComponent)
            }
        }
    }

    fun addMember(player: Player) = addMember(player.uuid)

    fun addMember(uuid: UUID) = copy(members = members + uuid)

    fun removeMember(player: Player) = removeMember(player.uuid)

    fun removeMember(uuid: UUID) = copy(members = members - uuid)

    fun addTerritory(territory: Territory) = addTerritory(territory.id)

    fun addTerritory(id: SnowID) = copy(territoryIDs = territoryIDs + id)

    fun removeTerritory(territory: Territory) = removeTerritory(territory.id)

    fun removeTerritory(id: SnowID) = copy(territoryIDs = territoryIDs - id)

    fun contains(chunkPos: ChunkPos) = getTerritoryIncludingChunk(chunkPos) != null

    fun getTerritoryIncludingChunk(chunkPos: ChunkPos) =
        territoryIDs.find { id -> TerritoryManager[id]?.contains(chunkPos) ?: false }
}