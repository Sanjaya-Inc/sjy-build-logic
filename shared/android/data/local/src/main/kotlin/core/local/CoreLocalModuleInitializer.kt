package core.local

import android.content.Context
import androidx.startup.Initializer
import core.utils.KoinInitializer
import org.koin.core.context.loadKoinModules

class CoreLocalModuleInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        loadKoinModules(CoreLocalModule.module())
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(KoinInitializer::class.java)
    }
}
