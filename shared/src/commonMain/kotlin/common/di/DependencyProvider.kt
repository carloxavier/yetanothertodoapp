package common.di

import app.cash.sqldelight.db.SqlDriver
import com.myapplication.common.db.Database
import data.TodoRepository
import view.TodosListViewModel
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("DependencyProvider")
object DependencyProvider {
    lateinit var driver: SqlDriver
    val todosListViewModel by lazy { TodosListViewModel(todosRepository) }
    private val todosRepository by lazy { TodoRepository(database) }
    private val database by lazy {
        Database(driver)
    }
}
