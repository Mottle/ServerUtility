package dev.deepslate.serverutility.permission

import net.minecraft.server.level.ServerPlayer
import java.util.*

interface PermissionProvider {
    fun query(player: ServerPlayer, key: String): PermissionQueryResult = query(player.uuid, key)

    fun query(uuid: UUID, key: String): PermissionQueryResult

    fun queryAsync(uuid: UUID, key: String, callback: (PermissionQueryResult) -> Unit)

    val name: String
}