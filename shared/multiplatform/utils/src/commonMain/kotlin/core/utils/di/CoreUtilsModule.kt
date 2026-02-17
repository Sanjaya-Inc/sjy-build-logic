package core.utils.di

import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Single

@Module
@ComponentScan(
    "core.utils"
)
object CoreUtilsModule {

    @Single
    fun provideJson(): Json {
        return Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
}

@Scope(name = "InitializerScope")
class InitializerScope
