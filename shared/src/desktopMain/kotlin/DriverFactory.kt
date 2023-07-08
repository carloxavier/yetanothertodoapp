
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.util.Properties

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
       return JdbcSqliteDriver("", Properties())
    }
}

actual var driverProvider: SqlDriver? = null
