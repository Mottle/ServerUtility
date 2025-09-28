package dev.deepslate.serverutility.configuration

import net.neoforged.neoforge.common.ModConfigSpec

object WorldFixerConfiguration {
    @JvmStatic
    private val builder = ModConfigSpec.Builder()

    @JvmStatic
    val ENABLED: ModConfigSpec.BooleanValue =
        builder.comment("enable world fixer(default false)").define("enabled", false)

    @JvmStatic
    val SPEC: ModConfigSpec = builder.build()
}