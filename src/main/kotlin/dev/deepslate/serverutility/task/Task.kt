package dev.deepslate.serverutility.task

import dev.deepslate.serverutility.utils.SnowID

interface Task {
    val state: TaskState

    val taskID: SnowID

    val synced: Boolean

    fun tryCancel(): Boolean
}