package core.utils

import core.utils.di.InitializerScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import org.koin.core.annotation.Single

@Single
@Scoped
@Scope(InitializerScope::class)
class InitializerRegistry(
    private val initializers: List<Initializer>,
    private val context: PlatformContext
) {

    private val prioritizedInitializer by lazy {
        initializers.sortedByDescending { it.priority }
    }

    fun initialize() {
        prioritizedInitializer.forEach { it.initialize(context) }
    }
}
