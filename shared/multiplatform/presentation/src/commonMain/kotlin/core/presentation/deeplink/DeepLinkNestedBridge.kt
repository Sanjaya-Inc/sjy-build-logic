package core.presentation.deeplink

import core.presentation.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DeepLinkNestedBridge {
    private val _pendingNestedRoute = MutableStateFlow<Route?>(null)

    val pendingNestedRoute: StateFlow<Route?> = _pendingNestedRoute.asStateFlow()

    fun request(route: Route) {
        _pendingNestedRoute.value = route
    }

    fun consumePending() {
        _pendingNestedRoute.value = null
    }
}
