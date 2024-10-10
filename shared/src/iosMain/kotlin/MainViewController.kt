import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.grappim.aipal.AiPalApp
import com.grappim.aipal.cache.databaseModule
import com.grappim.aipal.di.appModule
import com.grappim.aipal.di.mobileLocalDataStorageModule
import com.grappim.aipal.di.repoModule
import com.grappim.aipal.di.viewModelModule
import com.grappim.aipal.iosDataStoreModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(
                appModule,
                databaseModule(),
                iosDataStoreModule(),
                mobileLocalDataStorageModule(),
                repoModule(),
                viewModelModule()
            )
        }
    }
) { AiPalApp() }
