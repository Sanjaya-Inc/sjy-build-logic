package core.presentation

import androidx.compose.material3.SnackbarVisuals
import androidx.lifecycle.ViewModel
import core.presentation.navigation.NavigationRouter
import core.presentation.navigation.Route
import org.koin.mp.KoinPlatform.getKoin
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

sealed interface NavigationIntent {
    data class Navigate(val route: Route, val shouldClearBackStack: Boolean = false) :
        NavigationIntent

    data object ClearBackStack : NavigationIntent
    data object OnBack : NavigationIntent
}

abstract class BaseViewModel<State : Any, SideEffect : Any>(
    initialState: State,
    onCreate: (suspend BaseViewModel<State, SideEffect>.(Syntax<State, SideEffect>) -> Unit)? = null,
    private val navigationRouter: NavigationRouter = getKoin().get(),
    private val snackbarEventBus: SnackbarEventBus = getKoin().get(),
) : ContainerHost<State, SideEffect>, ViewModel() {

    override val container: Container<State, SideEffect> =
        container(
            initialState = initialState,
            onCreate = {
                onCreate?.invoke(this@BaseViewModel, this)
            },
        )

    open fun onIntent(intent: Any) = Unit

    fun sendIntent(intent: Any) {
        onIntent(intent)
        when (intent) {
            is SnackbarVisuals -> snackbarEventBus.post(intent)
            is NavigationIntent -> handleNavigationIntent(intent)
        }
    }

    private fun handleNavigationIntent(navigationIntent: NavigationIntent) {
        when (navigationIntent) {
            is NavigationIntent.Navigate -> navigationRouter.navigateTo(
                navigationIntent.route,
                navigationIntent.shouldClearBackStack
            )

            is NavigationIntent.ClearBackStack -> navigationRouter.clearBackStack()
            is NavigationIntent.OnBack -> navigationRouter.onBack()
        }
    }
}
