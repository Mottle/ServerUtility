package dev.deepslate.serverutility.territory.protection.group

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission

object DefaultGroup : PermissionGroup {
    override fun query(action: ProtectionPermission): PermissionQueryResult = PermissionQueryResult.DENY
}