import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object TodosState {
    private val scope = CoroutineScope(Job())
    private val todosRepository = TodoRepository()
    val todos =
        todosRepository
            .getTodos()
            .stateIn(scope, SharingStarted.Eagerly, listOf())

    private val _currentTodo = MutableStateFlow(TodoItem())
    val currentTodo: StateFlow<TodoItem> = _currentTodo

    fun setCurrentTodoItem(todoItem: TodoItem) {
        _currentTodo.update { todoItem }
    }

    fun addTodo(todoItem: TodoItem) {
        scope.launch {
            val insertedTodo = todosRepository.addTodoItem(todoItem)
            _currentTodo.update { insertedTodo }
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
        val newCurrentTodo = _currentTodo.value.copy(text = text)
        dataBase.todoQueries.update(
            newCurrentTodo.done.toLong(),
            newCurrentTodo.text,
            newCurrentTodo.id!!
        )
        _currentTodo.update { newCurrentTodo }
    }
}