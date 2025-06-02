package dev.deepslate.serverutility.permission.integration

import dev.deepslate.serverutility.permission.PermissionProvider
import dev.deepslate.serverutility.permission.PermissionQueryResult
import net.minecraft.server.level.ServerPlayer
import java.util.*

class SimplePermissionProvider : PermissionProvider {
    override fun query(player: ServerPlayer, key: String): PermissionQueryResult = PermissionQueryResult.ALLOW

    override fun query(uuid: UUID, key: String): PermissionQueryResult = PermissionQueryResult.ALLOW

    override fun queryAsync(uuid: UUID, key: String, callback: (PermissionQueryResult) -> Unit) =
        callback(PermissionQueryResult.ALLOW)

    override val name: String = "Simple"
}