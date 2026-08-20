package core.utils.permission

import androidx.compose.runtime.Composable
import org.koin.core.annotation.Single

@Single(binds = [PermissionRequestStrategy::class])
internal expect class NotificationPermissionStrategy() : PermissionRequestStrategy {
    override val permission: AppPermission

    @Composable
    override fun rememberRequest(): () -> Unit
}
