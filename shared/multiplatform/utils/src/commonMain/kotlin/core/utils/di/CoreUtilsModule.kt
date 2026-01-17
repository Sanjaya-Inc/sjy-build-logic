package core.utils.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scope

@Module
@ComponentScan(
    "core.utils"
)
object CoreUtilsModule

@Scope(name = "InitializerScope")
class InitializerScope
