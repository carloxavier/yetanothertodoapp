
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import common.di.DependencyProvider.driver
import common.di.DriverFactory

fun main() = application {
    driver = DriverFactory().createDriver()
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}