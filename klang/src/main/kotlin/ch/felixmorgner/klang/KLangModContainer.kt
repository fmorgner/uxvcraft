package ch.felixmorgner.klang

import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.IEventListener
import net.minecraftforge.fml.LifecycleEventProvider
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

class KLangModContainer(
    info: IModInfo,
    private val className: String,
    private val loader: ClassLoader,
    private val scanResults: ModFileScanData
) : ModContainer(info) {

    val eventBus: IEventBus
    private var mod: Any? = null
    private val modClass: Class<*>

    init {
        triggerMap[ModLoadingStage.CONSTRUCT] = Consumer(::constructMod)
        triggerMap[ModLoadingStage.CREATE_REGISTRIES] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.LOAD_REGISTRIES] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.COMMON_SETUP] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.SIDED_SETUP] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.ENQUEUE_IMC] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.PROCESS_IMC] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.COMPLETE] = Consumer(::fireEvent)
        triggerMap[ModLoadingStage.GATHERDATA] = Consumer(::fireEvent)

        eventBus = BusBuilder.builder().setExceptionHandler(::onEventFailed).setTrackPhases(false).build()
        configHandler = Optional.of(Consumer { event -> eventBus.post(event) })
        contextExtension = Supplier { KLangModLoadingContext.Context(this) }

        try {
            modClass = Class.forName(className, false, loader)
        } catch (e: Throwable) {
            throw ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e)
        }
    }

    override fun matches(mod: Any?) = mod === this.mod

    override fun getMod() = mod

    override fun acceptEvent(e: Event?) {
        eventBus.post(e)
    }

    private fun constructMod(event: LifecycleEventProvider.LifecycleEvent) {
        try {
            Class.forName(className, true, loader)
            mod = modClass.kotlin.objectInstance ?: modClass.getConstructor().newInstance()
            KLangEventSubscriber.inject(this, scanResults, modClass.classLoader)
        } catch (e: Throwable) {
            throw ModLoadingException(modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", e, modClass)
        }
    }

    private fun fireEvent(event: LifecycleEventProvider.LifecycleEvent) {
        try {
            eventBus.post(event.getOrBuildEvent(this))
        } catch (e: Throwable) {
            throw ModLoadingException(modInfo, event.fromStage(), "fml.modloading.errorduringevent", e)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onEventFailed(
        bus: IEventBus,
        event: Event,
        listener: Array<IEventListener>,
        i: Int,
        throwable: Throwable
    ) = Unit

}