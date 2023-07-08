import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class TodoRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTodos(): StateFlow<List<TodoItem>> = dataBase.todoQueries.selectAll().asFlow()
        .mapLatest { todos ->
            todos.executeAsList().map {
                TodoItem(
                    id = it.id,
                    text = it.content ?: "",
                    done = it.complete.toBoolean(),
                    timestamp = it.TIMESTAMP?.toLong() ?: 0L
                )
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Main),
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem = withContext(Dispatchers.IO) {
        dataBase.todoQueries.transactionWithResult {
            dataBase.todoQueries.insert(content = todoItem.text)
            dataBase.todoQueries.selectLastInsertedRowId()
        }.executeAsOne().let {
            todoItem.copy(id = it)
        }
    }

    suspend fun updateTodoItem(todoItem: TodoItem) {
        withContext(Dispatchers.IO) {
            dataBase.todoQueries.update(
                content = todoItem.text,
                complete = todoItem.done.toLong(),
                id = todoItem.id!!
            )
        }
    }

    suspend fun deleteTodoItem(todoItem: TodoItem) {
        withContext(Dispatchers.IO) {
            dataBase.todoQueries.delete(
                id = todoItem.id!!
            )
        }
    }
}