package core.utils.di

import core.utils.PlatformContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan(
    "core.utils"
)
class CoreUtilsModule(private val platformContext: PlatformContext) {

    @Single
    fun provideJson(): Json {
        return Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    @Single
    fun providePlatformContext(): PlatformContext {
        return platformContext
    }
}

fun coreUtilsModule(platformContext: PlatformContext) = CoreUtilsModule(platformContext).module()
