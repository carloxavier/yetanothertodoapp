import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

object TodosState {
    private val scope = CoroutineScope(Job())
    private val todosRepository = TodoRepository()
    val todos =
        todosRepository
            .getTodos()
            .stateIn(scope, SharingStarted.Eagerly, listOf())

    private val _currentTodo = mutableStateOf(TodoItem())
    val currentTodo: State<TodoItem> = _currentTodo

    fun setCurrentTodoItem(todoItem: TodoItem) {
        val oldTodoItem = _currentTodo.value
        scope.launch {
            if (oldTodoItem.id != null) {
                todosRepository.updateTodoItem(oldTodoItem)
            }
        }
        _currentTodo.value = todoItem
    }

    fun clearCurrentTodoItem() {
        setCurrentTodoItem(TodoItem())
    }

    fun addTodo(todoItem: TodoItem) {
        scope.launch {
            todosRepository.addTodoItem(todoItem)
        }
    }

    fun removeTodo(todoItem: TodoItem) {
        scope.launch {
            todosRepository.deleteTodoItem(todoItem)
        }
    }

    fun toggleTodoCompletion(todoItem: TodoItem) {
        val newTodoItem = todoItem.copy(done = !todoItem.done)
        scope.launch {
            todosRepository.updateTodoItem(newTodoItem)
        }
    }

    fun updateCurrentTodoItem(text: String) {
        _currentTodo.value = _currentTodo.value.copy(text = text)
    }
}