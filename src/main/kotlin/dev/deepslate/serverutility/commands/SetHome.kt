package dev.deepslate.serverutility.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.deepslate.serverutility.command.GameCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.neoforge.server.ServerLifecycleHooks
import java.util.*

object SetHome : GameCommand {

    fun obtainHomeStorage() = ServerLifecycleHooks.getCurrentServer()?.overworld()?.dataStorage?.computeIfAbsent(
        SetHome.SavedHomes.FACTORY, SetHome.SavedHomes.KEY
    )

    override val source: String
        get() = "sethome %s<name>"

    override val suggestions: Map<String, SuggestionProvider<CommandSourceStack>> = emptyMap()

    override val permissionRequired: String = "serverutility.command.home.set"

    override fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return 0
        val homes = obtainHomeStorage() ?: throw CommandRuntimeException.of("Home storage is null.")

        homes.insert(
            player, HomeRecord(
                context.getArgument("name", String::class.java),
                player.level().dimension(),
                player.blockPosition()
            )
        )
        homes.setDirty()

        return Command.SINGLE_SUCCESS
    }

    data class HomeRecord(val name: String, val dimension: ResourceKey<Level>, val pos: BlockPos) {
        companion object {
            val CODEC: Codec<HomeRecord> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.STRING.fieldOf("name").forGetter(HomeRecord::name),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(HomeRecord::dimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(HomeRecord::pos)
                ).apply(instance, ::HomeRecord)
            }
        }

        override fun hashCode(): Int = name.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as HomeRecord
            return name == other.name
        }
    }

//    data class HomeRecordContainer(val map: Map<String, Set<HomeRecord>> = emptyMap()) {
//        companion object {
//            private val setCodec =
//                HomeRecord.CODEC.listOf().xmap(Iterable<HomeRecord>::toSet, Iterable<HomeRecord>::toList)
//
//            val CODEC: Codec<HomeRecordContainer> =
//                Codec.unboundedMap(Codec.STRING, setCodec).xmap(::HomeRecordContainer, HomeRecordContainer::map)
//        }
//
//        fun query(serverName: String, homeName: String) = map[serverName]?.find { it.name == homeName }
//
//        fun query(serverName: String) = map[serverName]
//
//        fun add(serverName: String, home: HomeRecord): HomeRecordContainer {
//            val container = map[serverName] ?: emptySet()
//            val updatedContainer = container + home
//            return HomeRecordContainer(map + (serverName to updatedContainer))
//        }
//    }

    class SavedHomes(private val homeMap: MutableMap<UUID, Set<HomeRecord>>) : SavedData() {

        companion object {
            private val setCodec =
                HomeRecord.CODEC.listOf().xmap(Iterable<HomeRecord>::toSet, Iterable<HomeRecord>::toList)

            private val stringBaseUUIDCodec = Codec.STRING.xmap(UUID::fromString, UUID::toString)

            private val mapCodec: Codec<Map<UUID, Set<HomeRecord>>> =
                Codec.unboundedMap(stringBaseUUIDCodec, setCodec)

            const val KEY = "homes"

            fun of() = SavedHomes(mutableMapOf()).apply { setDirty() }

            fun load(tag: CompoundTag, registries: HolderLookup.Provider): SavedHomes {
                val nbt = tag.get(KEY)

                val map = mapCodec.decode(NbtOps.INSTANCE, nbt).orThrow.first
                return SavedHomes(map.toMutableMap())
            }

            val FACTORY = Factory(::of, ::load)
        }

        override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
            val nbt = mapCodec.encodeStart(NbtOps.INSTANCE, homeMap).orThrow
            tag.put(KEY, nbt)

            return tag
        }

        fun query(uuid: UUID) = homeMap[uuid]

        fun query(player: Player) = query(player.uuid)

        fun query(uuid: UUID, homeName: String) = homeMap[uuid]?.find { it.name == homeName }

        fun query(player: Player, homeName: String) = query(player.uuid, homeName)

        fun insert(uuid: UUID, home: HomeRecord) = homeMap.compute(uuid) { _, set -> (set ?: emptySet()) + home }

        fun insert(player: Player, home: HomeRecord) = insert(player.uuid, home)
    }
}