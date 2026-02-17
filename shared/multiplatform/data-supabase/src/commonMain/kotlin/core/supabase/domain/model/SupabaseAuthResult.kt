package core.supabase.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.time.Instant

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String?,
    val phone: String?,
    val role: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val lastSignInAt: Instant?,
    val emailConfirmedAt: Instant?,
    val phoneConfirmedAt: Instant?,
    val userMetadata: JsonObject?
)

sealed interface AuthProvider {
    data object Email : AuthProvider
    data object Google : AuthProvider
    data object Apple : AuthProvider
}

data class EmailCredentials(
    val email: String,
    val password: String,
    val metadata: Map<String, String>? = null
)

sealed class SupabaseError(override val message: String) : Exception(message) {
    class NetworkError(message: String = "Network connection failed") : SupabaseError(message)
    class AuthenticationError(message: String) : SupabaseError(message)
    class InvalidCredentials(message: String = "Invalid credentials") : SupabaseError(message)
    class EmailNotConfirmed(message: String = "Email not confirmed") : SupabaseError(message)
    class UserAlreadyExists(message: String = "User already exists") : SupabaseError(message)
    class WeakPassword(message: String = "Password too weak") : SupabaseError(message)
    class UnknownError(message: String = "Unknown error occurred") : SupabaseError(message)
}
