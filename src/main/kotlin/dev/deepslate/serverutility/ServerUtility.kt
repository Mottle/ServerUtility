package dev.deepslate.serverutility

import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import net.neoforged.fml.common.Mod
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS


@Mod(ServerUtility.ID)
object ServerUtility {
    const val ID = "serverutility"

    val LOGGER: Logger = LoggerFactory.getLogger(ID)

    init {
        LOGGER.info("Hello world!")

        val options = IdGeneratorOptions()
        YitIdHelper.setIdGenerator(options)

        ModAttachments.REGISTRY.register(MOD_BUS)
    }
}
