package core.supabase.domain.usecase

import core.utils.SjyDispatchers
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class SupabaseInitUseCase(
    private val supabaseClient: SupabaseClient,
    private val dispatchers: SjyDispatchers,
) {
    private val scope = CoroutineScope(dispatchers.io + SupervisorJob())

    suspend operator fun invoke() = withContext(dispatchers.io) {
        scope.launch { supabaseClient.auth.init() }
        scope.launch { supabaseClient.postgrest.init() }
        scope.launch { supabaseClient.realtime.init() }
    }
}
