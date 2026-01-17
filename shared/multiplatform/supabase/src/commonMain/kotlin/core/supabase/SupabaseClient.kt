package core.supabase

import Adadisini.core.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.core.annotation.Single

@Single
class SupabaseClient : SupabaseClient by createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY,
    builder = {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    },
)
