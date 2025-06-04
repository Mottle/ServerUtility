package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.world.entity.player.Player
import java.util.*

class Protection {
    // player uuid -> group snowID
    private val permissionRecords = emptyMap<UUID, SnowID>()

    private val groups = Long2ObjectOpenHashMap<Group>()

    operator fun get(uuid: UUID): Group? = permissionRecords[uuid]?.let { groups[it.value] }

    operator fun get(player: Player) = get(player.uuid)

    fun query(uuid: UUID, action: ProtectionPermission): PermissionQueryResult? = get(uuid)?.query(action)

    fun query(player: Player, action: ProtectionPermission) = query(player.uuid, action)

}