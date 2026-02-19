package rs.gdgoc.ui.menubar

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AboutDialogState {
    var isVisible by mutableStateOf(false)
        private set

    fun show() { isVisible = true }
    fun hide() { isVisible = false }
}

@Composable
fun AboutDialog() {
    if (AboutDialogState.isVisible) {
        AlertDialog(
            onDismissRequest = { AboutDialogState.hide() },
            title = {
                Text("About GDG IDE", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "GDG IDE\nVersion 1.0.0\n\n" +
                        "A lightweight, open-source code editor built with Kotlin and Jetpack Compose " +
                        "by the Google Developer Group on Campus community.\n\n" +
                        "Designed for learning, experimenting, and building — together.\n\n" +
                        "© 2025 GDG on Campus. All rights reserved."
                )
            },
            confirmButton = {
                TextButton(onClick = { AboutDialogState.hide() }) {
                    Text("OK")
                }
            }
        )
    }
}

