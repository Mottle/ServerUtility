package dev.deepslate.serverutility.territory

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.player.Player
import java.util.*

data class Town(val id: SnowID, val territoryIDs: List<SnowID>, val members: List<UUID>) {
    companion object {
        val CODEC: Codec<Town> = RecordCodecBuilder.create { instance ->
            instance.group(
                SnowID.CODEC.fieldOf("id").forGetter(Town::id),
                SnowID.CODEC.listOf().fieldOf("territory_ids").forGetter(Town::territoryIDs),
                UUIDUtil.CODEC.listOf().fieldOf("members").forGetter(Town::members)
            ).apply(instance, ::Town)
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
}