package core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

@Stable
interface Route

@Stable
class NavigationRouter(
    startRoute: Route,
    val key: Any? = null,
    val parent: NavigationRouter? = null
) {

    private val _backStack = MutableStateFlow(listOf(startRoute))
    val backStack = _backStack.asStateFlow()

    private val lastActionTimes = mutableMapOf<Any, Long>()

    private val debounceThreshold = 300L

    private fun shouldProcessAction(key: Any): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val lastTime = lastActionTimes[key] ?: 0L

        if (currentTime - lastTime < debounceThreshold) {
            return false
        }

        lastActionTimes[key] = currentTime
        return true
    }

    fun findSibling(key: Any?): NavigationRouter? {
        return when {
            key == null -> null
            this.key == key -> this
            else -> parent?.findSibling(key)
        }
    }

    operator fun invoke(key: Any?): NavigationRouter {
        if (key == null) return this
        return findSibling(key) ?: this
    }

    fun navigateTo(
        route: Route,
        clearBackStack: Boolean = false,
        allowDuplicates: Boolean = false
    ) {
        if (!shouldProcessAction(route)) return

        _backStack.update { currentStack ->
            when {
                clearBackStack -> listOf(route)

                !allowDuplicates && currentStack.contains(route) -> {
                    val index = currentStack.indexOf(route)
                    currentStack.subList(0, index + 1)
                }

                else -> currentStack + route
            }
        }
    }

    fun replaceTop(route: Route) {
        if (!shouldProcessAction(route)) return

        _backStack.update { stack ->
            when {
                stack.isEmpty() -> listOf(route)
                else -> stack.dropLast(1) + route
            }
        }
    }

    fun onBack() {
        if (!shouldProcessAction(ACTION_BACK)) return

        _backStack.update {
            if (it.size > 1) it.dropLast(1) else it
        }
    }

    fun clearBackStack() {
        if (!shouldProcessAction(ACTION_CLEAR)) return

        _backStack.update { emptyList() }
    }

    companion object {
        private const val ACTION_BACK = "ACTION_BACK"
        private const val ACTION_CLEAR = "ACTION_CLEAR"
    }
}

@Suppress("SwallowedException")
@Composable
fun NavigationRouter(
    router: NavigationRouter,
    entryProvider: EntryProviderScope<Route>.() -> Unit,
    modifier: Modifier = Modifier,
    transitionSpecs: TransitionSpecs = DefaultTransitionSpecs,
) {
    val backStack by router.backStack.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalNavigationRouter provides router,
        LocalParentNavigationRouter provides router
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = router::onBack,
            modifier = modifier,
            transitionSpec = transitionSpecs.screenForward,
            popTransitionSpec = transitionSpecs.screenBackward,
            predictivePopTransitionSpec = transitionSpecs.screenPredictiveBack,
            entryProvider = entryProvider {
                entryProvider()
            }
        )
    }
}

val LocalNavigationRouter = compositionLocalOf<NavigationRouter> { error("Not Provided yet") }

internal val LocalParentNavigationRouter = compositionLocalOf<NavigationRouter?> { null }

@Composable
fun rememberNavigationRouter(startRoute: Route, key: Any? = null): NavigationRouter {
    val parent = LocalParentNavigationRouter.current
    return retain(startRoute, key, parent) { NavigationRouter(startRoute, key, parent) }
}
