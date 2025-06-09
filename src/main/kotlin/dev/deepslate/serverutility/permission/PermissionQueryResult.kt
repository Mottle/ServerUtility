package dev.deepslate.serverutility.permission

enum class PermissionQueryResult {
    ALLOW,
    DENY,
    UNDEFINED;

    fun asBooleanStrictly() = when (this) {
        ALLOW -> true
        else -> false
    }

    fun asBooleanWeakly() = when (this) {
        DENY -> false
        else -> true
    }
}