import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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

data class TodoItem(val text: String = "", val done: Boolean = false)

@Composable
fun App() {
    NavigationHost(
        screens = mapOf(
            Screen.TodoList to { TodoList() },
            Screen.TodoDetails to { param -> TodoDetails(param as TodoItem) }
        )
    )
}

sealed class Screen {
    object TodoList : Screen()
    object TodoDetails : Screen()
}

@Composable
private fun TodoList() {
    val todos = TodosState.todos.collectAsState().value
    val currentItemText = TodosState.currentTodo.collectAsState().value.text
    Box {

        Column {
            Column(Modifier.weight(1f)) {
                todos.forEachIndexed { index, todo ->
                    Row {
                        Checkbox(checked = todo.done, onCheckedChange = {
                            todos[index] = todo.copy(done = it)
                        })
                        Text(
                            todo.text,
                            modifier = Modifier.clickable {
                                Navigator.navigate(
                                    Screen.TodoDetails,
                                    todos[index]
                                )
                            }.align(Alignment.CenterVertically),
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
            val labelMinimized = remember { mutableStateOf(false) }
            TextField(
                value = currentItemText,
                onValueChange = { TodosState.setCurrentTodoItem(TodoItem(it)) },
                modifier = Modifier.weight(1f).onFocusChanged { focusState ->
                    labelMinimized.value = focusState.isFocused
                },
                label = { Text(if (labelMinimized.value) "Todo:" else "What needs to be done?") },
                singleLine = true,
            )
            IconButton(
                onClick = {
                    TodosState.addTodo(TodoItem(currentItemText, false))
                    TodosState.setCurrentTodoItem(TodoItem())
                },
                enabled = currentItemText.isNotBlank()
            ) {
                Icon(Icons.Filled.Add, null)
            }
        }
    }
}

object TodosState {
    private val _todos = MutableStateFlow(mutableListOf<TodoItem>())
    val todos: StateFlow<MutableList<TodoItem>> = _todos

    private val _currentTodo = MutableStateFlow(TodoItem())
    val currentTodo: StateFlow<TodoItem> = _currentTodo

    fun setCurrentTodoItem(todoItem: TodoItem) {
        _currentTodo.update { todoItem }
    }

    fun addTodo(todoItem: TodoItem) {
        _todos.update { todos ->
            todos.add(todoItem)
            todos
        }
    }

    fun removeTodo() {
        _todos.update { todos ->
            todos.removeAt(todos.lastIndex)
            todos
        }
    }

    fun markTodoAsDone(todoItem: TodoItem) {
        _todos.update { todos ->
            todos[todos.indexOf(todoItem)] = todoItem.copy(done = true)
            todos
        }
    }
}

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

@Composable
private fun TodoDetails(param: TodoItem? = null) {
    Column {
        TopAppBar(title = { Text("Todo Details") }, navigationIcon = {
            IconButton(onClick = { Navigator.goBack() }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        })
        Text(param?.text ?: "")
    }
}

sealed class NavigationAction {
    object Back : NavigationAction()
    data class Navigate(val screen: Screen, val param: Any? = null) : NavigationAction()
}

@Composable
fun NavigationHost(
    screens: Map<Screen, @Composable (Any?) -> Unit>
) {
    val navigationAction = Navigator.currentNavigationAction.collectAsState().value
    Navigator.processNavigation(navigationAction, screens)
}


expect fun getPlatformName(): String