package dev.deepslate.serverutility.configuration

import net.neoforged.neoforge.common.ModConfigSpec

object WorldProtectionConfiguration {
    @JvmStatic
    private val builder = ModConfigSpec.Builder()

    @JvmStatic
    val AUTO_FIX_ENABLED: ModConfigSpec.BooleanValue =
        builder.comment("enable world auto fix(default false)").define("enabled", false)

    @JvmStatic
    val AUTO_FIX_DELAY: ModConfigSpec.IntValue = builder
        .comment("delay between fixes in 20 tick(second, default 3600)")
        .defineInRange("fix_delay", 3600, 1, Int.MAX_VALUE)

    @JvmStatic
    val CREEPER_EXPLOSION_DESTROY_BLOCK: ModConfigSpec.BooleanValue =
        builder.comment("should creeper explosion destroy blocks(default true)")
            .define("creeper_explosion_destroy_block", true)

    @JvmStatic
    val TNT_EXPLOSION_DESTROY_BLOCK: ModConfigSpec.BooleanValue =
        builder.comment("should TNT explosion destroy blocks(default true)")
            .define("tnt_explosion_destroy_block", true)

    @JvmStatic
    val SPEC: ModConfigSpec = builder.build()
}