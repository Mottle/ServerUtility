package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.group.DefaultGroup
import dev.deepslate.serverutility.territory.protection.group.PermissionGroup
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.world.entity.player.Player
import java.util.*

data class TownProtection(
    // player uuid -> group snowID
    private val permissionRecords: Map<UUID, SnowID> = emptyMap(),
    private val fallbackGroup: DefaultGroup = DefaultGroup,
    private val groups: Map<Long, PermissionGroup> = HashMap<Long, PermissionGroup>(),
) : Protection {


    operator fun get(uuid: UUID): PermissionGroup = permissionRecords[uuid]?.let { groups[it.value] } ?: fallbackGroup

    operator fun get(player: Player) = get(player.uuid)

    override fun query(uuid: UUID, action: ProtectionPermission): PermissionQueryResult = get(uuid).query(action)

    override fun queryDefault(action: ProtectionPermission) = fallbackGroup.query(action)

    fun addGroup(group: PermissionGroup) = copy(groups = groups + (group.id.value to group))

    fun removeGroup(group: PermissionGroup) = copy(groups = groups - group.id.value)

    fun addPermissionRecord(uuid: UUID, group: PermissionGroup) =
        copy(permissionRecords = permissionRecords + (uuid to group.id))

    fun removePermissionRecord(uuid: UUID) = copy(permissionRecords = permissionRecords - uuid)

    fun addPermissionRecord(player: Player, group: PermissionGroup) = addPermissionRecord(player.uuid, group)

    fun removePermissionRecord(player: Player) = removePermissionRecord(player.uuid)
}