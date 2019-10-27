package ch.felixmorgner.klang

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext

object KLangModLoadingContext {

    fun get(): Context = ModLoadingContext.get().extension()

    class Context(private val container: KLangModContainer) {
        val modEventBus: IEventBus
            get() = container.eventBus
    }

}