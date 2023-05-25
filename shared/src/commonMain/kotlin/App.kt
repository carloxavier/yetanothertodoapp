
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class TodoItem(val text: String = "", val done: Boolean = false)

@Composable
fun App() {
    NavigationHost(
        screens = mapOf(
            Screen.TodoList to { TodoList() },
            Screen.TodoDetails to { TodoDetails() }
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
                            modifier = Modifier.clickable { Navigator.navigateTo(Screen.TodoDetails) }
                                .align(Alignment.CenterVertically),
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
    private val _currentScreen = MutableStateFlow<Screen>(Screen.TodoList)
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.update { screen }
    }
}

@Composable
private fun TodoDetails() {
    val currentTodo = TodosState.todos.collectAsState().value.last()
    Text(currentTodo.text)
}

@Composable
fun NavigationHost(
    screens: Map<Screen, @Composable () -> Unit>
) {
    val currentScreen = Navigator.currentScreen.collectAsState().value
    screens[currentScreen]?.invoke()
}


expect fun getPlatformName(): String