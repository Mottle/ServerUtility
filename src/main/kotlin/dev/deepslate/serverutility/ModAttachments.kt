package dev.deepslate.serverutility

import dev.deepslate.serverutility.worldfixer.ChunkBlockStateChangeRecord
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val REGISTRY: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ServerUtility.ID)

    val BLOCK_STATE_CHANGE_RECORD: DeferredHolder<AttachmentType<*>, AttachmentType<ChunkBlockStateChangeRecord>> =
        REGISTRY.register("block_state_change_record") { _ ->
            AttachmentType.builder { _ -> ChunkBlockStateChangeRecord.empty() }.serialize(
                ChunkBlockStateChangeRecord.CODEC
            ).build()
        }
}