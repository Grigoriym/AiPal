import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.grappim.aipal.AiPalApp
import com.grappim.aipal.di.appModule
import com.grappim.aipal.di.repoModule
import com.grappim.aipal.di.viewModelModule
import com.grappim.aipal.localDataStorage
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(
            appModule,
            localDataStorage(),
            repoModule(),
            viewModelModule()
        )
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
