
import androidx.compose.runtime.Composable

@Composable
fun App() {
    NavigationHost(
        screens = mapOf(
            Screen.TodoList to { TodoListView() },
            Screen.TodoDetails to { param -> TodoDetails(param as TodoItem) }
        )
    )
}
