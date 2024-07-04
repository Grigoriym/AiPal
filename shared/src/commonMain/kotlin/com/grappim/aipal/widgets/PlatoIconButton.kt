package com.grappim.aipal.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.grappim.aipal.uikit.AiPalTheme

@Composable
fun PlatoIconButton(modifier: Modifier = Modifier, icon: ImageVector, onButtonClick: () -> Unit) {
    IconButton(
        modifier = modifier
            .testTag(icon.name),
        onClick = onButtonClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = Color.Black
        )
    }
}

@[Composable]
private fun PlatoIconButtonPreview() {
    AiPalTheme {
        PlatoIconButton(
            icon = Icons.Filled.Abc,
            onButtonClick = {}
        )
    }
}
