package com.grappim.aipal.widgets

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PlatoSelectableText(text: String) {
    SelectionContainer {
        Text(text)
    }
}
