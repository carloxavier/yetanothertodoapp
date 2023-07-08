import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    driverProvider = DriverFactory().createDriver()
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}