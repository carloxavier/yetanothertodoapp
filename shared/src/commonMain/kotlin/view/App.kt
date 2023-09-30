package view
import androidx.compose.runtime.Composable
import domain.TodoItem
import view.navigation.NavigationHost
import view.navigation.Screen

@Composable
fun App() {
    NavigationHost(
        screens = mapOf(
            Screen.TodoList to { TodoListView() },
            Screen.TodoDetails to { param -> TodoDetails(param as TodoItem) }
        )
    )
}
