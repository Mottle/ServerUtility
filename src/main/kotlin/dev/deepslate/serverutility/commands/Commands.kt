package dev.deepslate.serverutility.commands

import dev.deepslate.serverutility.command.GameCommand

object Commands {
    val commands: ArrayList<GameCommand> = arrayListOf()

    init {
        if (commands.isEmpty()) {
            commands += SetHome
            commands += SetHomeDefault
            commands += ListHome
            commands += SpawnHome
            commands += SpawnHomeDefault
            commands += Suicide
            commands += Fly
        }
    }
}