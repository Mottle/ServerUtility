package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import net.minecraft.world.entity.player.Player
import java.util.*

interface Protection {
    fun query(uuid: UUID, action: ProtectionPermission): PermissionQueryResult

    fun query(player: Player, action: ProtectionPermission): PermissionQueryResult = query(player.uuid, action)

    fun queryDefault(action: ProtectionPermission): PermissionQueryResult
}