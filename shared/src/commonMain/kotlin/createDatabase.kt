import com.myapplication.common.db.Database

fun createDatabase(): Database {
    return Database(driverProvider!!)
}

val dataBase = createDatabase()