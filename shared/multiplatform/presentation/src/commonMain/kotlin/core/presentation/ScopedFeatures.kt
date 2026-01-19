package core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform

/**
 * A zero-reflection wrapper that manages a Koin Scope.
 *
 * @param scopeId The unique ID for this scope instance. Use [NavBackStackEntry.id] for best performance.
 * @param qualifier The Koin Qualifier that identifies the scope definition (e.g. TypeQualifier(CheckoutSession::class)).
 * @param content The content to render with the active scope.
 */
@Composable
fun ScopedFeatures(
    scopeId: String,
    qualifier: Qualifier,
    content: @Composable (Scope) -> Unit
) {
    val koin = KoinPlatform.getKoin()
    val scope = remember(scopeId) {
        koin.getOrCreateScope(scopeId = scopeId, qualifier = qualifier)
    }
    DisposableEffect(scopeId) {
        onDispose {
            if (!scope.closed) {
                scope.close()
            }
        }
    }
    content(scope)
}
