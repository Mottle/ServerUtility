package dev.deepslate.serverutility.task

typealias StateSetter = (TaskState) -> Unit

fun interface ScheduledExecutable {
    fun execute(stateSetter: StateSetter)
}