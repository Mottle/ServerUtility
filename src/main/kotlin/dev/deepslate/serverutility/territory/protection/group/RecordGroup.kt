package dev.deepslate.serverutility.territory.protection.group

import dev.deepslate.serverutility.permission.PermissionQueryResult
import dev.deepslate.serverutility.territory.protection.permission.ProtectionPermission
import dev.deepslate.serverutility.utils.SnowID
import net.minecraft.network.chat.Component

data class RecordGroup(
    override val id: SnowID,
    val displayName: Component,
    val permissions: Map<ProtectionPermission, PermissionQueryResult>
) : PermissionGroup {
//    companion object {
//        val CODEC = RecordCodecBuilder.create { instance ->
//            instance.group(
//                SnowID.CODEC.fieldOf("id").forGetter { it.id },
//                Codecs.COMPONENT_CODEC.fieldOf("display_name").forGetter { it.displayName },
//
//        }
//    }

    override fun query(action: ProtectionPermission): PermissionQueryResult =
        permissions[action] ?: PermissionQueryResult.UNDEFINED

    fun queryRaw(action: ProtectionPermission): PermissionQueryResult? = permissions[action]

    infix operator fun plus(pair: Pair<ProtectionPermission, PermissionQueryResult>) =
        copy(permissions = permissions + pair)

    operator fun set(action: ProtectionPermission, result: PermissionQueryResult) =
        copy(permissions = permissions + (action to result))
}