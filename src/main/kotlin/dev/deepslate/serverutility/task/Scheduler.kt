package dev.deepslate.serverutility.task

interface Scheduler {
    fun schedule(delay: Int, runnable: ScheduledExecutable): Task

    fun schedule(delay: Int, period: Int, runnable: ScheduledExecutable): Task

    fun schedule(runnable: ScheduledExecutable): Task

    fun cancel(task: Task): Boolean
}