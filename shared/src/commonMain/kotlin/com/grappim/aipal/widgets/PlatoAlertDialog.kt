package com.grappim.aipal.widgets

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

const val PLATO_ALERT_DIALOG_TAG = "plato_alert_dialog_tag"

@Composable
fun PlatoAlertDialog(
    modifier: Modifier = Modifier,
    text: String,
    showAlertDialog: Boolean,
    confirmButtonText: String = "Yes",
    dismissButtonText: String? = null,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: (() -> Unit)? = null,
    onDismissButtonClicked: (() -> Unit)? = null
) {
    if (showAlertDialog) {
        val dismissButton: @Composable (() -> Unit)? =
            if (dismissButtonText != null && onDismissButtonClicked != null) {
                {
                    Button(
                        onClick = onDismissButtonClicked
                    ) {
                        Text(dismissButtonText)
                    }
                }
            } else {
                null
            }
        AlertDialog(
            modifier = modifier.testTag(PLATO_ALERT_DIALOG_TAG),
            shape = MaterialTheme.shapes.medium.copy(all = CornerSize(16.dp)),
            onDismissRequest = onDismissRequest,
            title = { Text(text = text) },
            confirmButton = {
                if (onConfirmButtonClicked != null) {
                    Button(
                        onClick = onConfirmButtonClicked
                    ) {
                        Text(confirmButtonText)
                    }
                }
            },
            dismissButton = dismissButton
        )
    }
}

@[Composable Preview]
private fun PlatoAlertDialogPreview() {

    PlatoAlertDialog(
        text = "Some text",
        showAlertDialog = true,
        onDismissRequest = {},
        onConfirmButtonClicked = {},
        onDismissButtonClicked = {}
    )
}

@[Composable Preview]
private fun PlatoAlertDialogWithoutDismissPreview() {

    PlatoAlertDialog(
        text = "Some text",
        showAlertDialog = true,
        onDismissRequest = {},
        onConfirmButtonClicked = {}
    )
}
