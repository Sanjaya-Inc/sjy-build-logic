package core.utils.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.compose.koinInject
import org.koin.core.annotation.Single

sealed interface AppPermission {
    data object Notifications : AppPermission
    data object Camera : AppPermission
}

interface PermissionRequestStrategy {
    val permission: AppPermission

    @Composable
    fun rememberRequest(): () -> Unit
}

@Single
internal class PermissionRequesterRegistry(
    private val strategies: List<PermissionRequestStrategy>
) {
    fun strategy(permission: AppPermission): PermissionRequestStrategy =
        requireNotNull(strategies.firstOrNull { it.permission == permission }) {
            "No PermissionRequestStrategy registered for $permission"
        }
}

@Composable
fun RequestPermission(permission: AppPermission) {
    if (LocalInspectionMode.current) return
    val request = rememberPermissionRequest(permission)
    LaunchedEffect(permission) {
        request()
    }
}

@Composable
fun rememberPermissionRequest(permission: AppPermission): () -> Unit {
    val registry = koinInject<PermissionRequesterRegistry>()
    val strategy = remember(permission, registry) { registry.strategy(permission) }
    return strategy.rememberRequest()
}
