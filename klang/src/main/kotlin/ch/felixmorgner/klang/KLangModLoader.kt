package ch.felixmorgner.klang

import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import kotlin.reflect.full.primaryConstructor

class KLangModLoader(val className: String) : IModLanguageProvider.IModLanguageLoader {

    @Suppress("UNCHECKED_CAST")
    override fun <T> loadMod(info: IModInfo, loader: ClassLoader, scanResults: ModFileScanData) = try {
        Class.forName("ch.felixmorgner.klang.KLangModContainer", true, Thread.currentThread().contextClassLoader)
            .kotlin
            .primaryConstructor
            ?.call(info, className, loader, scanResults) as T
    } catch (e: Exception) {
        throw e
    }

}


