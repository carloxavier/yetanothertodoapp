package data

import app.cash.sqldelight.coroutines.asFlow
import com.myapplication.common.db.Database
import common.toBoolean
import common.toLong
import domain.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class TodoRepository(private val dataBase: Database) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTodos(): Flow<List<TodoItem>> = dataBase.todoQueries.selectAll()
        .asFlow()
        .mapLatest { todos ->
            todos.executeAsList().map {
                TodoItem(
                    id = it.id,
                    text = it.content ?: "",
                    done = it.complete.toBoolean(),
                    timestamp = it.TIMESTAMP?.toLong() ?: 0L
                )
            }
        }

    suspend fun addTodoItem(todoItem: TodoItem): TodoItem = withContext(Dispatchers.IO) {
        dataBase.todoQueries.transactionWithResult {
            dataBase.todoQueries.insert(content = todoItem.text)
            dataBase.todoQueries.selectLastInsertedRowId()
        }.executeAsOne().let {
            todoItem.copy(id = it)
        }
    }

    suspend fun updateTodoItem(todoItem: TodoItem) {
        requireNotNull(todoItem.id)
        withContext(Dispatchers.IO) {
            dataBase.todoQueries.update(
                content = todoItem.text,
                complete = todoItem.done.toLong(),
                id = todoItem.id
            )
        }
    }

    suspend fun deleteTodoItem(todoItem: TodoItem) {
        requireNotNull(todoItem.id)
        withContext(Dispatchers.IO) {
            dataBase.todoQueries.delete(
                id = todoItem.id
            )
        }
    }
}