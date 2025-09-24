package dev.deepslate.serverutility.commands

import dev.deepslate.serverutility.command.GameCommand
import dev.deepslate.serverutility.commands.town.CreateTown
import dev.deepslate.serverutility.commands.town.ListTowns
import dev.deepslate.serverutility.commands.town.RemoveTown

object Commands {
    val commands: ArrayList<GameCommand> = arrayListOf()

    init {
        if (commands.isEmpty()) {
            commands += SetHome
            commands += SetHomeDefault
            commands += ListHome
            commands += ListHomeOther
            commands += SpawnHome
            commands += SpawnHomeDefault
            commands += Suicide
            commands += Fly
            commands += FlyOther
            commands += ProtectionPass
            commands += ProtectionPassOther
            commands += Feed
            commands += FeedOther
            commands += Heal
            commands += HealOther
            commands += CreateTown
            commands += ListTowns
            commands += RemoveTown
        }
    }
}