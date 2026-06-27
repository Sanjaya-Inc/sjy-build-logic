package core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<State : Any, SideEffect : Any>(
    initialState: State,
    vararg onCreateHandlers: ViewModelCreationCallback<State, SideEffect>
) : ContainerHost<State, SideEffect>, ViewModel() {

    override val container: Container<State, SideEffect> =
        container(
            initialState = initialState,
            onCreate = {
                onCreateHandlers.forEach {
                    it.onCreate(this@BaseViewModel, this)
                }
            },
        )

    abstract fun sendIntent(intent: Any)

    @Composable
    fun collectStateAndSideEffect(
        onSideEffect: suspend (sideEffect: SideEffect) -> Unit
    ): androidx.compose.runtime.State<State> {
        collectSideEffect(sideEffect = onSideEffect)
        return collectAsState()
    }
}
