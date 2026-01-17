package core.utils

import core.utils.di.CoreUtilsModule
import core.utils.di.InitializerScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.koin.mp.KoinPlatform

actual fun startKoinPlatform(context: PlatformContext?, modules: List<Module>) {
    context?.let {
        startKoin {
            androidContext(context.appContext)
            androidLogger()
            val loadedModules = listOf(
                CoreUtilsModule.module,
            ) + modules
            modules(loadedModules)
        }
        KoinPlatform.getKoin().getOrCreateScope<InitializerScope>("Initializer")
            .get<InitializerRegistry>()
            .initialize(context)
    }
}
