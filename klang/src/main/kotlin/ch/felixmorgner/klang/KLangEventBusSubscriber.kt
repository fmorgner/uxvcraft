package ch.felixmorgner.klang

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class KLangEventBusSubscriber(
    vararg val value: Dist = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    val modid: String = "",
    val bus: Bus = Bus.FORGE
) {

    companion object {
        const val KEY_MODID = "modid"
        const val KEY_VALUE = "value"
        const val KEY_BUS = "bus"
    }

    enum class Bus constructor(val supplier: () -> IEventBus) {
        FORGE({ MinecraftForge.EVENT_BUS }),
        MOD({ KLangModLoadingContext.get().modEventBus });
    }

}
