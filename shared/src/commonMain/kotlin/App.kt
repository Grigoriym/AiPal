import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules()
    }) {
        MaterialTheme {

        }
    }
}
