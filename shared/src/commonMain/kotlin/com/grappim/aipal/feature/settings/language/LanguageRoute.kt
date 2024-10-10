package com.grappim.aipal.feature.settings.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.aipal.widgets.PlatoSelectTextField
import com.grappim.aipal.widgets.PlatoTopBar

@Composable
fun LanguageRoute(
    viewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier =
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PlatoTopBar(onBack = onBack, title = "Language")
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            LanguageChooser(state)
        }
    }
}

@Composable
private fun LanguageChooser(state: LanguageState) {
    Text(
        text = "Here you can choose the language. " +
                "Please also ensure that the TTS language matches. " +
                "If you are using an offline STT engine, check that the language is set correctly. " +
                "Language-specific prompts will be changed. ",
//                "The current active chat will be deleted. ",
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(6.dp))

    PlatoSelectTextField(
        selectedValue = state.currentLanguage.title,
        options = state.languages,
        label = "Language",
        onValueChangedEvent = state.onSetCurrentLanguage,
    )
}
