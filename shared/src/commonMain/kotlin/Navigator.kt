import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

object Navigator {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val _backstack =
        MutableStateFlow<List<NavigationAction>>(listOf(NavigationAction.Navigate(Screen.TodoList)))

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentNavigationAction: StateFlow<NavigationAction> =
        _backstack.mapLatest { it.last() }.stateIn(
            scope, SharingStarted.Eagerly, NavigationAction.Navigate(Screen.TodoList)
        )

    fun navigate(screen: Screen, param: Any? = null) {
        _backstack.update { it + NavigationAction.Navigate(screen, param) }
    }

    fun goBack() {
        _backstack.update {
            if (it.size > 1) {
                it.dropLast(1)
            } else {
                it
            }
        }
    }

    @Composable
    fun processNavigation(
        navigationAction: NavigationAction,
        screens: Map<Screen, @Composable (Any?) -> Unit>
    ) {
        when (navigationAction) {
            is NavigationAction.Navigate -> {
                screens[navigationAction.screen]?.invoke(navigationAction.param)
            }

            is NavigationAction.Back -> {
                goBack()
            }
        }
    }
}