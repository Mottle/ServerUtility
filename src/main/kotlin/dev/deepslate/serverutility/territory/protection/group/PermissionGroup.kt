package dev.deepslate.serverutility.territory.protection.group

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID

interface PermissionGroup {
    val id: SnowID

    fun query(action: ProtectionPermission): PermissionQueryResult

    operator fun get(action: ProtectionPermission) = query(action)
}