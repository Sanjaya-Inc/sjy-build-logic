package core.utils.permission

import androidx.compose.runtime.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class PermissionRequesterRegistryTest {

    @Test
    fun `registered strategy is returned for its permission`() {
        val notifications = fakeStrategy(AppPermission.Notifications)
        val sut = PermissionRequesterRegistry(listOf(notifications))

        val result = sut.strategy(AppPermission.Notifications)

        assertSame(notifications, result)
    }

    @Test
    fun `matching strategy is chosen among several`() {
        val notifications = fakeStrategy(AppPermission.Notifications)
        val camera = fakeStrategy(AppPermission.Camera)
        val sut = PermissionRequesterRegistry(listOf(notifications, camera))

        val result = sut.strategy(AppPermission.Camera)

        assertSame(camera, result)
    }

    @Test
    fun `missing strategy fails fast`() {
        val sut = PermissionRequesterRegistry(emptyList())

        val error = assertFailsWith<IllegalArgumentException> {
            sut.strategy(AppPermission.Notifications)
        }

        assertEquals(
            "No PermissionRequestStrategy registered for ${AppPermission.Notifications}",
            error.message
        )
    }
}

private class FakePermissionStrategy(
    override val permission: AppPermission
) : PermissionRequestStrategy {
    @Composable
    override fun rememberRequest(): () -> Unit = {}
}

private fun fakeStrategy(permission: AppPermission): PermissionRequestStrategy =
    FakePermissionStrategy(permission)
