package core.utils

import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan(
    "core.utils",
)
object CoreUtilsModule {
    @Single
    internal fun provideJson(): Json {
        return Json {
            prettyPrint = true
            isLenient = false
            ignoreUnknownKeys = true
        }
    }
}
