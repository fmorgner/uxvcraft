package ch.felixmorgner.klang

import ch.felixmorgner.klang.KLangEventBusSubscriber.Bus
import ch.felixmorgner.klang.KLangEventBusSubscriber.Companion.KEY_BUS
import ch.felixmorgner.klang.KLangEventBusSubscriber.Companion.KEY_MODID
import ch.felixmorgner.klang.KLangEventBusSubscriber.Companion.KEY_VALUE
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation.EnumHolder
import net.minecraftforge.forgespi.language.ModFileScanData
import org.objectweb.asm.Type

object KLangEventSubscriber {

    private val AUTO_SUBSCRIBER = Type.getType(KLangEventBusSubscriber::class.java)

    private val DEFAULT_SIDES = listOf(
        EnumHolder(null, "CLIENT"),
        EnumHolder(null, "DEDICATED_SERVER")
    )

    private val DEFAULT_BUS = EnumHolder(null, "FORGE")

    @Suppress("UNCHECKED_CAST")
    fun inject(mod: ModContainer, scanData: ModFileScanData?, loader: ClassLoader) {
        (scanData ?: return)
            .annotations
            .filter { it.annotationType == AUTO_SUBSCRIBER }
            .forEach {
                val sides = (it.annotationData.getOrDefault(KEY_VALUE, DEFAULT_SIDES) as List<EnumHolder>)
                    .map { Dist.valueOf(it.value) }
                val modId = it.annotationData.getOrDefault(KEY_MODID, mod.modId) as String
                val bus = (it.annotationData.getOrDefault(KEY_BUS, DEFAULT_BUS) as EnumHolder)
                    .run { Bus.valueOf(value) }

                if (mod.modId == modId && FMLEnvironment.dist in sides) {
                    try {
                        val modClass = Class.forName(it.classType.className, true, loader)
                        bus.supplier().register(modClass.kotlin.objectInstance ?: modClass)
                    } catch (e: ClassNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }
    }

}
