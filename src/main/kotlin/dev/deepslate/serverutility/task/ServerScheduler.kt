package dev.deepslate.serverutility.task

import com.github.yitter.idgen.YitIdHelper
import it.unimi.dsi.fastutil.PriorityQueue
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue
import net.neoforged.neoforge.server.ServerLifecycleHooks

class ServerScheduler : Scheduler {

    companion object {
        val INSTANCE = ServerScheduler()
    }

    private val onceTasks: PriorityQueue<SyncedTask> =
        ObjectHeapPriorityQueue(1024, Comparator.comparing { it.correctedTick })

    private val repeatedTasks = mutableListOf<SyncedTask>()

    private val currentTick: Int get() = ServerLifecycleHooks.getCurrentServer()?.tickCount ?: -1

    override fun schedule(delay: Int, runnable: ScheduledExecutable): Task {
        val task = SyncedTask(currentTick, delay, -1, runnable)
        onceTasks.enqueue(task)
        return task
    }

    override fun schedule(delay: Int, period: Int, runnable: ScheduledExecutable): Task {
        val task = SyncedTask(currentTick, delay, period, runnable)
        repeatedTasks.add(task)
        return task
    }

    override fun schedule(runnable: ScheduledExecutable): Task = schedule(1, runnable)

    override fun cancel(task: Task): Boolean {
        if (task !is SyncedTask) return false

        return task.tryCancel()
    }

    fun process() {
        val tick = this.currentTick

        if (!onceTasks.isEmpty && onceTasks.first().correctedTick < tick) {
            while (onceTasks.first().correctedTick != tick) {
                onceTasks.dequeue().let { t ->
                    t.state = TaskState.ERRORED
                }
            }
        }

        while (!onceTasks.isEmpty && onceTasks.first().correctedTick == tick) {
            onceTasks.dequeue().let { currentTask ->
                currentTask.state = TaskState.PENDING
                try {
                    currentTask.run()
                } catch (e: Exception) {
                    currentTask.state = TaskState.ERRORED
                    currentTask.exception = e
                } finally {
                    if (currentTask.state != TaskState.ERRORED) currentTask.state = TaskState.COMPLETED
                }
            }
        }

        for (task in repeatedTasks) {
            if (task.state != TaskState.UNSCHEDULED) repeatedTasks.remove(task)
            if (task.correctedTick != tick) continue

            task.state = TaskState.PENDING
            try {
                task.run()
            } catch (e: Exception) {
                task.state = TaskState.ERRORED
                task.exception = e
            } finally {
                task.runningCount++
                if (task.state == TaskState.CANCELED || task.state == TaskState.ERRORED || task.state == TaskState.COMPLETED) {
                    repeatedTasks.remove(task)
                }
            }
        }
    }

    private class SyncedTask(val originTick: Int, val delay: Int, val period: Int, val executable: ScheduledExecutable) : Task,
        Runnable {

        override var state: TaskState = TaskState.UNSCHEDULED

        var exception: Exception? = null

        var runningCount = 0

        val correctedTick get() = originTick + delay + period * runningCount

        override val taskID: Long = YitIdHelper.nextId()

        override fun tryCancel(): Boolean {
            if (state != TaskState.UNSCHEDULED) return false
            state = TaskState.CANCELED
            return true
        }

        override val synced: Boolean = true


        override fun run() = executable.execute { s -> state = s }
    }
}