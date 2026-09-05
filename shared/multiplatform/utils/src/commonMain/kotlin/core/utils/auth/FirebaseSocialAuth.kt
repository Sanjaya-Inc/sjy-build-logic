package core.utils.auth

import androidx.compose.runtime.Composable

enum class FirebaseAuthProvider {
    Google,
    Apple,
}

data class FirebaseAuthCredential(
    val provider: FirebaseAuthProvider,
    val idToken: String,
    val authorizationCode: String? = null,
    val nonce: String? = null,
    val fullName: String? = null,
)

class FirebaseAuthCancelledException(cause: Throwable? = null) : Exception(cause)

enum class FirebaseSocialAuthError {
    Google,
    Apple,
    Unknown,
}

class FirebaseSocialAuthException(
    val error: FirebaseSocialAuthError,
    cause: Throwable? = null,
) : Exception(cause)

interface FirebaseSocialAuthUseCase {
    val isAppleAvailable: Boolean

    suspend operator fun invoke(provider: FirebaseAuthProvider): Result<FirebaseAuthCredential>
}

@Composable
expect fun rememberFirebaseSocialAuthUseCase(): FirebaseSocialAuthUseCase
