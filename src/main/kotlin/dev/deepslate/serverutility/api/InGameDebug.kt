package dev.deepslate.serverutility.api

import net.minecraft.network.chat.Component

interface InGameDebug {
    fun debugComponent(index: Int = -1): Component
}