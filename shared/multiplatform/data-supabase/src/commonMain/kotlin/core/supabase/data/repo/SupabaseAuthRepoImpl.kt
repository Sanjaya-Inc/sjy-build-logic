package core.supabase.data.repo

import core.supabase.SupabaseClient
import core.supabase.domain.model.AuthProvider
import core.supabase.domain.model.EmailCredentials
import core.supabase.domain.model.SupabaseError
import core.supabase.domain.model.SupabaseUser
import core.supabase.domain.repo.SupabaseAuthRepo
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.annotation.Single
import kotlin.time.Instant

@Single
class SupabaseAuthRepoImpl(
    private val supabaseClient: SupabaseClient
) : SupabaseAuthRepo {

    override suspend fun signInWithEmail(credentials: EmailCredentials): Result<SupabaseUser> {
        return runCatching {
            supabaseClient.auth.signInWith(Email) {
                email = credentials.email
                password = credentials.password
            }

            val userInfo = supabaseClient.auth.currentUserOrNull()
                ?: throw SupabaseError.AuthenticationError("User not found after sign in")

            userInfo.toSupabaseUser()
        }.mapToSupabaseError()
    }

    override suspend fun signUpWithEmail(credentials: EmailCredentials): Result<SupabaseUser> {
        return runCatching {
            val response = supabaseClient.auth.signUpWith(Email) {
                email = credentials.email
                password = credentials.password
                credentials.metadata?.let { metadata ->
                    data = buildJsonObject {
                        metadata.forEach { (key, value) -> put(key, value) }
                    }
                }
            }

            val userInfo = response ?: supabaseClient.auth.currentUserOrNull()
                ?: throw SupabaseError.AuthenticationError("User not found after sign up")

            userInfo.toSupabaseUser()
        }.mapToSupabaseError()
    }

    override suspend fun signInWithOAuth(provider: AuthProvider, redirectUrl: String): Result<Unit> {
        return runCatching {
            when (provider) {
                AuthProvider.Google -> supabaseClient.auth.signInWith(Google, redirectUrl = redirectUrl)
                AuthProvider.Apple -> supabaseClient.auth.signInWith(Apple, redirectUrl = redirectUrl)
                else -> throw SupabaseError.UnknownError("Unsupported OAuth provider")
            }
        }.mapToSupabaseError()
    }

    override suspend fun getCurrentUser(): SupabaseUser? {
        return supabaseClient.auth.currentUserOrNull()?.toSupabaseUser()
    }

    override fun observeAuthState(): Flow<SupabaseUser?> {
        return supabaseClient.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> status.session.user?.toSupabaseUser()
                else -> null
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return runCatching {
            supabaseClient.auth.resetPasswordForEmail(email)
        }.mapToSupabaseError()
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return runCatching {
            supabaseClient.auth.updateUser {
                password = newPassword
            }
            Unit
        }.mapToSupabaseError()
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            supabaseClient.auth.signOut()
        }.mapToSupabaseError()
    }

    private fun UserInfo.toSupabaseUser() = SupabaseUser(
        id = id,
        email = email,
        phone = phone,
        role = role,
        createdAt = createdAt?.let { Instant.fromEpochMilliseconds(it.toEpochMilliseconds()) },
        updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it.toEpochMilliseconds()) },
        lastSignInAt = lastSignInAt?.let { Instant.fromEpochMilliseconds(it.toEpochMilliseconds()) },
        emailConfirmedAt = emailConfirmedAt?.let { Instant.fromEpochMilliseconds(it.toEpochMilliseconds()) },
        phoneConfirmedAt = phoneConfirmedAt?.let { Instant.fromEpochMilliseconds(it.toEpochMilliseconds()) },
        userMetadata = userMetadata
    )

    private fun <T> Result<T>.mapToSupabaseError(): Result<T> {
        return recoverCatching { exception ->
            throw when (exception) {
                is SupabaseError -> exception
                is RestException -> when {
                    exception.message?.contains("Invalid login credentials", ignoreCase = true) == true ->
                        SupabaseError.InvalidCredentials()
                    exception.message?.contains("Email not confirmed", ignoreCase = true) == true ->
                        SupabaseError.EmailNotConfirmed()
                    exception.message?.contains("User already registered", ignoreCase = true) == true ->
                        SupabaseError.UserAlreadyExists()
                    exception.message?.contains("Password should be at least", ignoreCase = true) == true ->
                        SupabaseError.WeakPassword()
                    else -> SupabaseError.UnknownError(exception.message ?: "Unknown error")
                }
                is IOException -> SupabaseError.NetworkError()
                else -> SupabaseError.UnknownError(exception.message ?: "Unknown error")
            }
        }
    }
}
