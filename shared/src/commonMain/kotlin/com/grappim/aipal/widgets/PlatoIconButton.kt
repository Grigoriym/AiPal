package com.grappim.aipal.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.grappim.aipal.uikit.AiPalTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * https://stackoverflow.com/a/76004755
 */
@Composable
fun PlatoIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onButtonClick: () -> Unit,
    tint: Color = LocalContentColor.current,
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier
            .testTag(icon.name),
        onClick = onButtonClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            tint = tint.copy(alpha = LocalContentColor.current.alpha),
            contentDescription = ""
        )
    }
}

@[Preview Composable]
private fun PlatoIconButtonPreview() {
    AiPalTheme {
        PlatoIconButton(
            icon = Icons.Filled.Abc,
            onButtonClick = {}
        )
    }
}
