package view
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import data.TodoRepository
import domain.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TodosListViewModel(private val todosRepository: TodoRepository) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val todos = todosRepository.getTodos()

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