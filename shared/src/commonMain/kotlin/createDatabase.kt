import com.myapplication.common.db.Database

fun createDatabase(): Database {
    return Database(driver)
}

val dataBase = createDatabase()