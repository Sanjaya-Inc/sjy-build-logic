package core.presentation

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
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
}
