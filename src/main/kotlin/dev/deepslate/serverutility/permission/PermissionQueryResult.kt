package dev.deepslate.serverutility.permission

enum class PermissionQueryResult {
    ALLOW,
    DENY,
    UNDEFINED;

    fun asBoolean() = when (this) {
        ALLOW -> true
        else -> false
    }
}