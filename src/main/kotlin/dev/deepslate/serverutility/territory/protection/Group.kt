package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission

data class Group(val permissions: Map<ProtectionPermission, PermissionQueryResult>) {
    fun query(action: ProtectionPermission): PermissionQueryResult =
        permissions[action] ?: PermissionQueryResult.UNDEFINED

    fun queryRaw(action: ProtectionPermission): PermissionQueryResult? = permissions[action]

    infix operator fun plus(pair: Pair<ProtectionPermission, PermissionQueryResult>) =
        copy(permissions = permissions + pair)

    operator fun set(action: ProtectionPermission, result: PermissionQueryResult) =
        copy(permissions = permissions + (action to result))

    operator fun get(action: ProtectionPermission) = query(action)
}