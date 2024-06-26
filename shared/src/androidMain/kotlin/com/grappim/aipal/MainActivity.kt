package com.grappim.aipal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import com.grappim.aipal.feature.chat.ChatScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                AndroidApp()
            }
        }
    }
}


@Composable
fun AndroidApp() {
    Navigator(screen = ChatScreen())
}