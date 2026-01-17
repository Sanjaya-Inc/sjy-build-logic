package core.supabase.di

import core.supabase.SupabaseClient
import core.utils.Initializer
import core.utils.PlatformContext
import core.utils.SjyDispatchers
import core.utils.di.InitializerScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import org.koin.core.annotation.Single

@Single
@Scoped
@Scope(InitializerScope::class)
class SupabaseInitializer(
    private val supabaseClient: SupabaseClient,
    private val dispatchers: SjyDispatchers
) : Initializer, CoroutineScope by CoroutineScope(dispatchers.io) {

    override fun initialize(context: PlatformContext?) {
        println("Initializer: SupabaseInitializer")
        launch { supabaseClient.auth.init() }
        launch { supabaseClient.postgrest.init() }
        launch { supabaseClient.realtime.init() }
    }
}
