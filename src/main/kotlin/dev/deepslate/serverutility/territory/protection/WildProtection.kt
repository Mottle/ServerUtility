package dev.deepslate.serverutility.territory.protection

import dev.deepslate.serverutility.permission.PermissionManager
import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.*
import java.util.*

class WildProtection : Protection {

    override fun query(uuid: UUID, action: ProtectionPermission): PermissionQueryResult {
        with(PermissionManager) {
            when (action) {
                BlockBreak -> return query(uuid, "serverutility.wild.break")
                BlockPlace -> return query(uuid, "serverutility.wild.place")
                Explosion -> return query(uuid, "serverutility.wild.explosion")
                Inventory -> return query(uuid, "serverutility.wild.inventory")
                InteractionBlock -> return query(uuid, "serverutility.wild.interaction.block")
            }
        }

        return PermissionQueryResult.UNDEFINED
    }

    override fun queryDefault(action: ProtectionPermission): PermissionQueryResult {
        return PermissionQueryResult.UNDEFINED
    }
}