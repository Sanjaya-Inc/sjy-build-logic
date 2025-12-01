package core.utils

import android.content.Context
import androidx.startup.Initializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class KoinInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(listOf(CoreUtilsModule.module))
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}
