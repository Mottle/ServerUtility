package dev.deepslate.serverutility.territory

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.deepslate.serverutility.territory.protection.TownProtection
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*

data class Town(
    val id: SnowID,
    val name: String,
    val territoryIDs: List<SnowID>,
    val members: TownMemories,
    val displayName: Component = Component.literal(name).withStyle(ChatFormatting.BOLD)
        .withStyle(ChatFormatting.YELLOW),
    val protection: TownProtection = TownProtection()
) {
    companion object {
        val CODEC: Codec<Town> = RecordCodecBuilder.create { instance ->
            instance.group(
                SnowID.CODEC.fieldOf("id").forGetter(Town::id),
                Codec.STRING.fieldOf("name").forGetter(Town::name),
                SnowID.CODEC.listOf().fieldOf("territory_ids").forGetter(Town::territoryIDs),
                TownMemories.CODEC.fieldOf("members").forGetter(Town::members),
                Codec.STRING.fieldOf("display_name").forGetter { t ->
                    try {
                        Component.Serializer.toJson(
                            t.displayName, ServerLifecycleHooks.getCurrentServer()!!.registryAccess()
                        )
                    } catch (e: Exception) {
                        ""
                    }
                },
            ).apply(instance) { id, name, territories, members, nameJson ->
                val fallback = Component.literal("未命名领地")
                val nameComponent = try {
                    Component.Serializer.fromJson(nameJson, ServerLifecycleHooks.getCurrentServer()!!.registryAccess())
                } catch (e: Exception) {
                    fallback
                } ?: fallback
                Town(id, name, territories, members, nameComponent)
            }
        }

        fun of(owner: Player, townName: String) =
            Town(SnowID.generate(), townName, emptyList(), TownMemories.of(owner))
    }

    fun addMember(player: Player) = addMember(player.uuid)

    fun addMember(uuid: UUID) = copy(members = members + uuid)

    fun removeMember(player: Player) = removeMember(player.uuid)

    fun removeMember(uuid: UUID) = copy(members = members - uuid)

    fun addTerritory(territory: Territory) = addTerritory(territory.id)

    fun addTerritory(id: SnowID) = copy(territoryIDs = territoryIDs + id)

    fun removeTerritory(territory: Territory) = removeTerritory(territory.id)

    fun removeTerritory(id: SnowID) = copy(territoryIDs = territoryIDs - id)

    operator fun contains(chunkPos: ChunkPos) = getTerritoryIncludingChunk(chunkPos) != null

    operator fun contains(player: Player) = members.contains(player)

    fun isOwner(uuid: UUID) = members.owner == uuid

    fun isOwner(player: Player) = isOwner(player.uuid)

    fun getTerritoryIncludingChunk(chunkPos: ChunkPos) =
        territoryIDs.find { id -> TerritoryManager[id]?.contains(chunkPos) ?: false }

    data class TownMemories(val owner: UUID, val memories: Set<UUID>) {
        companion object {
            val CODEC: Codec<TownMemories> = RecordCodecBuilder.create { instance ->
                instance.group(
                    UUIDUtil.CODEC.fieldOf("owner").forGetter(TownMemories::owner),
                    UUIDUtil.CODEC.listOf().fieldOf("memories").forGetter { tm -> tm.memories.toList() }
                ).apply(instance) { owner, memories ->
                    TownMemories(owner, memories.toSet())
                }
            }

            fun of(owner: UUID) = TownMemories(owner, emptySet())

            fun of(owner: Player) = of(owner.uuid)
        }

        operator fun contains(uuid: UUID) = if (owner == uuid) true else memories.contains(uuid)

        operator fun contains(player: Player) = contains(player.uuid)

        fun addMemory(uuid: UUID) = copy(memories = memories + uuid)

        fun addMemory(player: Player) = addMemory(player.uuid)

        operator fun plus(uuid: UUID) = addMemory(uuid)

        operator fun plus(player: Player) = addMemory(player.uuid)

        fun removeMemory(player: Player) = removeMemory(player.uuid)

        fun removeMemory(uuid: UUID) = copy(memories = memories - uuid)

        operator fun minus(uuid: UUID) = removeMemory(uuid)

        operator fun minus(player: Player) = removeMemory(player.uuid)
    }

    fun debugComponent(index: Int = -1): Component = index.let {
        if (it < 0) "-" else it.toString()
    }.let {
        Component.literal("----------$it----------\n").withStyle(ChatFormatting.WHITE).append(
            Component.literal("name: ").append(Component.literal("$name\n").withStyle(ChatFormatting.GREEN))
        ).append(
            Component.literal("id: ").append(id.debugComponent())
        ).append(Component.literal("\n"))
    }


}