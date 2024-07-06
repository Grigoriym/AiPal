import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.grappim.aipal.AiPalApp
import com.grappim.aipal.di.appModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "AiPal"
        ) {
            AiPalApp()
        }
    }
}
