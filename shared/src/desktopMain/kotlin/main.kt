import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.grappim.aipal.AiPalApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "AiPal") {
        AiPalApp()
    }
}
