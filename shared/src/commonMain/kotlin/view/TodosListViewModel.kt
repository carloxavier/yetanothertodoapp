package view

import androidx.compose.runtime.mutableStateOf
import data.TodoRepository
import domain.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodosListViewModel(private val todosRepository: TodoRepository) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val todos = todosRepository.getTodos().map { it ->
        it.sortedByDescending { todo -> todo.timestamp }
            .map {
                TodoListViewItemState(
                    id = it.id,
                    text = it.text,
                    done = it.done,
                    isEditing = _currentTodo.value.id == it.id
                )
            }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private val _currentTodo = mutableStateOf(TodoItem())

    fun setCurrentTodoItem(todoItem: TodoListViewItemState = TodoListViewItemState()) {
        val oldTodoItem = _currentTodo.value
        scope.launch {
            if (oldTodoItem.id != null) {
                todosRepository.updateTodoItem(oldTodoItem)
            }
        }
        _currentTodo.value = todoItem.toTodoItem()
    }

    fun clearCurrentTodoItem() {
        setCurrentTodoItem()
    }

    fun addTodo() {
        scope.launch {
            todosRepository.addTodoItem(TodoItem())
        }
    }

    fun removeTodo(todoItem: TodoListViewItemState) {
        scope.launch {
            todosRepository.deleteTodoItem(todoItem.toTodoItem())
        }
    }

    fun toggleTodoCompletion(todoItem: TodoListViewItemState) {
        val newTodoItem = todoItem.copy(done = !todoItem.done)
        scope.launch {
            todosRepository.updateTodoItem(newTodoItem.toTodoItem())
        }
    }

    fun updateCurrentTodoItem(text: String) {
        _currentTodo.value = _currentTodo.value.copy(text = text)
    }
}

data class TodoListViewItemState(
    val id: Long? = null,
    val text: String = "",
    val done: Boolean = false,
    val isEditing: Boolean = false
)

private inline fun TodoListViewItemState.toTodoItem(): TodoItem {
    return TodoItem(
        id = id,
        text = text,
        done = done
    )
}