import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        TODO("Not yet implemented")
    }
}

actual var driverProvider: SqlDriver? = null