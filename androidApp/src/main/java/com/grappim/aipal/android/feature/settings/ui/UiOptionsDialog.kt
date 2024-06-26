package com.grappim.aipal.android.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.grappim.aipal.data.model.DarkThemeConfig
import com.grappim.aipal.data.model.isDark
import com.grappim.aipal.data.model.isLight
import com.grappim.aipal.data.model.isSystemDefault
import com.grappim.aipal.feature.settings.SettingsState

@Composable
fun UiOptionDialog(state: SettingsState, onDismissed: () -> Unit) {
    if (state.isUiSettingsVisible) {
        Dialog(onDismissRequest = onDismissed) {
            Card(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DarkModePreferencesContent(state = state)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onDismissed) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkModePreferencesContent(state: SettingsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Dark mode preferences"
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isSystemDefault(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.FOLLOW_SYSTEM)
                },
                text = "System Default"
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isLight(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.LIGHT)
                },
                text = "Light"
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isDark(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.DARK)
                },
                text = "Dark"
            )
        }
    }
}

@Composable
private fun PlatoRadioButton(selected: Boolean, onClick: () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = text)
    }
}
