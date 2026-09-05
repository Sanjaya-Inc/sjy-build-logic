package core.utils.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.MessageDigest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "RallyRankSso"
private const val WEB_CLIENT_ID_LOG_SUFFIX = 20

@Composable
actual fun rememberFirebaseSocialAuthUseCase(): FirebaseSocialAuthUseCase {
    val context = LocalContext.current
    return remember(context) { AndroidFirebaseSocialAuthUseCase(context) }
}

private class AndroidFirebaseSocialAuthUseCase(
    private val context: Context,
) : FirebaseSocialAuthUseCase {
    override val isAppleAvailable: Boolean = false

    override suspend fun invoke(provider: FirebaseAuthProvider): Result<FirebaseAuthCredential> {
        Log.w(TAG, "invoke provider=$provider")
        return runCatching {
            if (provider == FirebaseAuthProvider.Apple) {
                throw UnsupportedOperationException("Sign in with Apple is not available on Android")
            }
            signInWithGoogle()
        }.fold(
            onSuccess = { credential ->
                Log.w(TAG, "invoke success tokenLen=${credential.idToken.length}")
                Result.success(credential)
            },
            onFailure = { error ->
                error.logSso("invoke failed")
                when (error) {
                    is CancellationException -> throw error
                    is GetCredentialCancellationException ->
                        Result.failure(FirebaseAuthCancelledException(error))
                    else -> Result.failure(
                        FirebaseSocialAuthException(FirebaseSocialAuthError.Google, error)
                    )
                }
            }
        )
    }

    private suspend fun signInWithGoogle(): FirebaseAuthCredential {
        val activity = context.findActivity()
        val serverClientId = googleWebClientId()
        activity.logSigningCerts()
        Log.w(
            TAG,
            "getCredential start activity=${activity.javaClass.simpleName} " +
                "pkg=${activity.packageName} webClientId=…${serverClientId.takeLast(WEB_CLIENT_ID_LOG_SUFFIX)}",
        )
        val credentialManager = CredentialManager.create(activity)
        val response = credentialManager.getGoogleCredential(activity, serverClientId)
        Log.w(
            TAG,
            "getCredential returned class=${response.credential::class.java.simpleName} " +
                "type=${response.credential.type}",
        )
        val googleIdToken = response.googleIdToken()
        Log.w(TAG, "parsed Google idToken len=${googleIdToken.length}")
        signInToFirebase(googleIdToken)
        return FirebaseAuthCredential(
            provider = FirebaseAuthProvider.Google,
            idToken = googleIdToken,
        )
    }

    private suspend fun signInToFirebase(googleIdToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        suspendCancellableCoroutine { continuation ->
            FirebaseAuth.getInstance()
                .signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (!continuation.isActive) {
                        Log.w(TAG, "firebase signInWithCredential ignored; continuation inactive")
                        return@addOnCompleteListener
                    }
                    if (task.isSuccessful) {
                        Log.w(TAG, "firebase signInWithCredential success uid=${task.result?.user?.uid}")
                        continuation.resume(Unit)
                    } else {
                        val error = task.exception ?: IllegalStateException("Firebase Auth failed")
                        error.logSso("firebase signInWithCredential failed")
                        continuation.resumeWithException(error)
                    }
                }
        }
    }
}

private fun googleWebClientId(): String {
    val serverClientId = GoogleAuthConfig.GOOGLE_CLIENT_ID.trim()
    require(serverClientId.isNotBlank()) {
        "Missing GOOGLE_CLIENT_ID in local.properties (Google web OAuth client, type 3)"
    }
    return serverClientId
}

private suspend fun CredentialManager.getGoogleCredential(
    activity: Activity,
    serverClientId: String,
): GetCredentialResponse {
    return getCredential(
        activity,
        GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(serverClientId).build())
            .build(),
    )
}

private fun GetCredentialResponse.googleIdToken(): String {
    val credential = credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
    Log.e(
        TAG,
        "unexpected credential class=${credential::class.java.name} type=${credential.type}",
    )
    error("Unexpected Google credential ${credential::class.simpleName}")
}

private fun Context.findActivity(): Activity {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    error("Google Sign-In requires an Activity")
}

private fun Context.logSigningCerts() {
    apkSignatures().forEach { signature ->
        Log.w(TAG, "apk SHA-1=${fingerprint(signature, "SHA-1")}")
        Log.w(TAG, "apk SHA-256=${fingerprint(signature, "SHA-256")}")
    }
}

private fun Context.apkSignatures(): List<android.content.pm.Signature> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val signingInfo = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES,
        ).signingInfo
        signingInfo?.apkContentsSigners?.toList().orEmpty()
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            .signatures
            ?.toList()
            .orEmpty()
    }
}

private fun fingerprint(signature: android.content.pm.Signature, algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm).digest(signature.toByteArray())
    return digest.joinToString(":") { byte -> "%02X".format(byte) }
}

private fun Throwable.logSso(step: String) {
    Log.e(TAG, "$step ${javaClass.name}: $message", this)
}
