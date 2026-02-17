package core.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.core.annotation.Single

@Single
internal class SupabaseClient : SupabaseClient by createSupabaseClient(
    supabaseUrl = SupabaseConfig.SUPABASE_URL,
    supabaseKey = SupabaseConfig.SUPABASE_KEY,
    builder = {
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = SupabaseConfig.GOOGLE_CLIENT_ID)
            appleNativeLogin()
        }
    },
)
