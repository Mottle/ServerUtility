package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.group.DefaultGroup
import dev.deepslate.serverutility.territory.protection.group.PermissionGroup
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.world.entity.player.Player
import java.util.*

class TownProtection : Protection {
    // player uuid -> group snowID
    private val permissionRecords = emptyMap<UUID, SnowID>()

    private val fallbackGroup = DefaultGroup

    private val groups = Long2ObjectOpenHashMap<PermissionGroup>()

    operator fun get(uuid: UUID): PermissionGroup = permissionRecords[uuid]?.let { groups[it.value] } ?: fallbackGroup

    operator fun get(player: Player) = get(player.uuid)

    override fun query(uuid: UUID, action: ProtectionPermission): PermissionQueryResult = get(uuid).query(action)

    override fun queryDefault(action: ProtectionPermission) = fallbackGroup.query(action)
}