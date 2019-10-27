package ch.felixmorgner.uxvcraft

import ch.felixmorgner.klang.KLangModLoadingContext
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager

@Mod("uxvcraft")
class UxvCraft {

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    init {
        KLangModLoadingContext.get().modEventBus.register(this)
    }

    @SubscribeEvent
    fun setup(event: FMLCommonSetupEvent) {
        LOGGER.info("uxvcraft::setup")
        LOGGER.debug("$event")
    }

    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("uxvcraft::clientSetup")
        LOGGER.debug("$event")
    }

    @SubscribeEvent
    fun serverSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("uxvcraft::serverSetup")
        LOGGER.debug("$event")
    }

    @SubscribeEvent
    fun serverStarting(event: FMLServerStartingEvent) {
        LOGGER.info("uxvcraft::serverStarting")
        LOGGER.debug("$event")
    }

}