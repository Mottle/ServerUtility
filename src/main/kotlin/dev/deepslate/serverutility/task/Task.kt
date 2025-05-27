package dev.deepslate.serverutility.task

interface Task {
    val state: TaskState

    val taskID: Long

    val synced: Boolean

    fun tryCancel(): Boolean
}