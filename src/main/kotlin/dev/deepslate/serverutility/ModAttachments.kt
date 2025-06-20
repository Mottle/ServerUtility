package dev.deepslate.serverutility

import dev.deepslate.serverutility.utils.TypeHelper
import dev.deepslate.serverutility.worldfixer.ChunkBlockStateChangeRecord
import net.minecraft.world.level.chunk.ChunkAccess
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val REGISTRY: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ServerUtility.ID)

    val BLOCK_STATE_CHANGE_RECORD: DeferredHolder<AttachmentType<*>, AttachmentType<ChunkBlockStateChangeRecord>> =
        REGISTRY.register("block_state_change_record") { _ ->
            AttachmentType.builder { i ->
                TypeHelper.mustBe<ChunkAccess>(i).let { ChunkBlockStateChangeRecord.empty() }
            }.serialize(
                ChunkBlockStateChangeRecord.CODEC
            ).build()
        }
//
//    val TOWN_BELONG: DeferredHolder<AttachmentType<*>, AttachmentType<SnowID>> = REGISTRY.register("town_belong") { _ ->
//        AttachmentType.builder { i -> TypeHelper.mustBe<Player>(i).let { SnowID.EMPTY } }
//            .serialize(SnowID.CODEC).copyOnDeath()
//            .build()
//    }

//    val HOMES: DeferredHolder<AttachmentType<*>, AttachmentType<SetHome.HomeRecordContainer>> =
//        REGISTRY.register("homes") { _ ->
//            AttachmentType.builder { _ -> SetHome.HomeRecordContainer() }
//                .serialize(SetHome.HomeRecordContainer.CODEC)
//                .copyOnDeath()
//                .build()
//        }
}