package dev.deepslate.serverutility.task

enum class TaskState {
    UNSCHEDULED,
    PENDING,
    ERRORED,
    COMPLETED,
    CANCELED
}