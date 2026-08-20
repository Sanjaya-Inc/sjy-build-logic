package core.utils.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.annotation.Single
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

@Single(binds = [PermissionRequestStrategy::class])
internal actual class NotificationPermissionStrategy : PermissionRequestStrategy {
    actual override val permission: AppPermission = AppPermission.Notifications

    @Composable
    actual override fun rememberRequest(): () -> Unit {
        return remember {
            {
                val options = UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge
                UNUserNotificationCenter.currentNotificationCenter()
                    .requestAuthorizationWithOptions(options) { _, _ -> }
            }
        }
    }
}
