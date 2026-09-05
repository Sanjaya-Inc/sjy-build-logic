package core.utils.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

fun interface FirebaseSocialAuthNative {
    fun signIn(
        provider: FirebaseAuthProvider,
        onSuccess: (FirebaseAuthCredential) -> Unit,
        onCancel: () -> Unit,
        onError: (String) -> Unit,
    )
}

object FirebaseSocialAuthHost {
    var native: FirebaseSocialAuthNative? = null
}

@Composable
actual fun rememberFirebaseSocialAuthUseCase(): FirebaseSocialAuthUseCase {
    return remember { IosFirebaseSocialAuthUseCase() }
}

private class IosFirebaseSocialAuthUseCase : FirebaseSocialAuthUseCase {
    override val isAppleAvailable: Boolean = true

    override suspend fun invoke(provider: FirebaseAuthProvider): Result<FirebaseAuthCredential> {
        return runCatching { awaitNativeSignIn(provider) }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { error ->
                when (error) {
                    is CancellationException -> throw error
                    else -> Result.failure(error)
                }
            }
        )
    }

    private suspend fun awaitNativeSignIn(
        provider: FirebaseAuthProvider,
    ): FirebaseAuthCredential = suspendCancellableCoroutine { continuation ->
        val host = FirebaseSocialAuthHost.native
        if (host == null) {
            continuation.resumeWith(
                Result.failure(
                    IllegalStateException("FirebaseSocialAuthHost.native is not registered")
                )
            )
            return@suspendCancellableCoroutine
        }
        host.signIn(
            provider = provider,
            onSuccess = { credential ->
                if (continuation.isActive) continuation.resume(credential)
            },
            onCancel = {
                if (continuation.isActive) {
                    continuation.resumeWith(Result.failure(FirebaseAuthCancelledException()))
                }
            },
            onError = { code ->
                if (continuation.isActive) {
                    continuation.resumeWith(Result.failure(code.toSocialAuthException()))
                }
            },
        )
    }
}

private fun String.toSocialAuthException(): FirebaseSocialAuthException {
    val error = when (this) {
        "apple" -> FirebaseSocialAuthError.Apple
        "google" -> FirebaseSocialAuthError.Google
        else -> FirebaseSocialAuthError.Unknown
    }
    return FirebaseSocialAuthException(error)
}
