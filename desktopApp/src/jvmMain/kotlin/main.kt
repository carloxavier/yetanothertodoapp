import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    driver = DriverFactory().createDriver()
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}