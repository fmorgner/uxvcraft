package ch.felixmorgner.klang

import net.minecraftforge.fml.javafmlmod.FMLJavaModLanguageProvider
import net.minecraftforge.forgespi.language.ILifecycleEvent
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import java.util.function.Consumer
import java.util.function.Supplier

class KLangLanguageProvider : IModLanguageProvider {

    override fun name() = "klang"

    override fun getFileVisitor() = Consumer<ModFileScanData> {
        it.addLanguageLoader(
            it.annotations
                .filter { it.annotationType == FMLJavaModLanguageProvider.MODANNOTATION }
                .map { it.annotationData["value"] as String to KLangModLoader(it.classType.className) }
                .toMap()
        )
    }

    override fun <R : ILifecycleEvent<R>?> consumeLifecycleEvent(consumeEvent: Supplier<R>?) = Unit

}
