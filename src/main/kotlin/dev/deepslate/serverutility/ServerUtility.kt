package dev.deepslate.serverutility

import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS


@Mod(ServerUtility.ID)
object ServerUtility {
    const val ID = "serverutility"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        val options = IdGeneratorOptions()
        YitIdHelper.setIdGenerator(options)

        ModAttachments.REGISTRY.register(MOD_BUS)
    }
}
