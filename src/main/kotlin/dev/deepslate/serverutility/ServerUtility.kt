package dev.deepslate.serverutility

import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import dev.deepslate.serverutility.configuration.WorldFixerConfiguration
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS


@Mod(ServerUtility.ID)
class ServerUtility(bus: IEventBus, container: ModContainer) {
    companion object {
        const val ID = "serverutility"

        val LOGGER: Logger = LoggerFactory.getLogger("Server Utility")
    }

    init {
        LOGGER.info("Hello Server!")

        val options = IdGeneratorOptions()
        YitIdHelper.setIdGenerator(options)

        ModAttachments.REGISTRY.register(MOD_BUS)

        container.registerConfig(ModConfig.Type.SERVER, WorldFixerConfiguration.SPEC)
    }
}