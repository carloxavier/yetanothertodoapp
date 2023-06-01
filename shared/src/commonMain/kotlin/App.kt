
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
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
    val currentlyEditingTodo = TodosState.currentTodo.collectAsState().value
    Column {
        Column {
            todos.forEach { todo ->
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)) {
                    Checkbox(checked = todo.done, onCheckedChange = {
                        TodosState.toggleTodoCompletion(todo)
                    })
                    if (currentlyEditingTodo == todo) {
                        OutlinedTextField(
                            value = currentlyEditingTodo.text,
                            onValueChange = { TodosState.updateCurrentTodoItem(it) },
                            modifier = Modifier.clickable {
                                Navigator.navigate(
                                    Screen.TodoDetails,
                                    todo
                                )
                            }.align(Alignment.CenterVertically)
                                .padding(start = 16.dp)
                                .weight(1f),
                        )
                    } else {
                        Text(
                            todo.text,
                            modifier = Modifier.clickable {
                                Navigator.navigate(
                                    Screen.TodoDetails,
                                    todo
                                )
                            }.align(Alignment.CenterVertically).weight(1f).padding(start = 16.dp),
                        )
                    }
                    IconButton(
                        onClick = { TodosState.removeTodo(todo) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Delete, null)
                    }
                    IconButton(
                        onClick = { TodosState.setCurrentTodoItem(todo) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Edit, null)
                    }
                }
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    TodosState.addTodo(TodoItem(currentlyEditingTodo.text, false))
                    TodosState.setCurrentTodoItem(TodoItem())
                },
                enabled = currentlyEditingTodo.text.isNotBlank()
            ) {
                Icon(Icons.Filled.Add, null)
            }
            val labelMinimized = remember { mutableStateOf(false) }
            OutlinedTextField(
                value = currentlyEditingTodo.text,
                onValueChange = { TodosState.setCurrentTodoItem(TodoItem(it)) },
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    labelMinimized.value = focusState.isFocused
                },
                label = { Text(if (labelMinimized.value) "Todo:" else "What needs to be done?") },
                singleLine = true,
            )
        }
    }
}

object TodosState {
    private val _todos = MutableStateFlow(listOf<TodoItem>())
    val todos: StateFlow<List<TodoItem>> = _todos

    private val _currentTodo = MutableStateFlow(TodoItem())
    val currentTodo: StateFlow<TodoItem> = _currentTodo

    fun setCurrentTodoItem(todoItem: TodoItem) {
        _currentTodo.update { todoItem }
    }

    fun addTodo(todoItem: TodoItem) {
        _todos.update { todos ->
            todos + todoItem
        }
    }

    fun removeTodo(todoItem: TodoItem) {
        _todos.update { todos ->
            todos - todoItem
        }
    }

    fun toggleTodoCompletion(todoItem: TodoItem) {
        _todos.update { todos ->
            todos.map {
                if (it == todoItem) {
                    TodoItem(it.text, !it.done)
                } else {
                    it
                }
            }
        }
    }

    fun updateCurrentTodoItem(text: String) {
        val newCurrentTodo = TodoItem(text, _currentTodo.value.done)
        _todos.update { todos ->
            todos.map {
                if (it == _currentTodo.value) {
                    newCurrentTodo
                } else {
                    it
                }
            }
        }
        _currentTodo.update { newCurrentTodo }
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