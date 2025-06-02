package dev.deepslate.serverutility.permission.integration

import dev.deepslate.serverutility.permission.PermissionProvider
import dev.deepslate.serverutility.permission.PermissionQueryResult
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.UserManager
import net.luckperms.api.util.Tristate
import java.util.*

class LuckPermsProvider : PermissionProvider {

    override val name: String = "LuckPerms"

    companion object {
        private fun convertTripleState(state: Tristate): PermissionQueryResult = when (state) {
            Tristate.TRUE -> PermissionQueryResult.ALLOW
            Tristate.FALSE -> PermissionQueryResult.DENY
            Tristate.UNDEFINED -> PermissionQueryResult.UNDEFINED
        }
    }

    private val luckPermsHolder by lazy { LuckPermsProvider.get() }

    val userManager: UserManager
        get() = luckPermsHolder.userManager

    override fun query(uuid: UUID, key: String): PermissionQueryResult {
        val user = userManager.getUser(uuid) ?: return PermissionQueryResult.UNDEFINED

        return user.cachedData.permissionData.checkPermission(key).let(::convertTripleState)
    }

    override fun queryAsync(uuid: UUID, key: String, callback: (PermissionQueryResult) -> Unit) {
        userManager.loadUser(uuid).thenAcceptAsync { user ->
            val result = user.cachedData.permissionData.checkPermission(key).let(::convertTripleState)
            callback(result)
        }
    }
}