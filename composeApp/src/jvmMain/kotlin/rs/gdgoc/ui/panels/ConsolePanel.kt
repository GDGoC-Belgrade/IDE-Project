package rs.gdgoc.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation. layout. Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose. runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui.graphics.Color
import androidx. compose.ui.text.style.TextAlign

@Composable
fun ConsolePanel() {
    Box(
        modifier = Modifier. fillMaxSize().background(Color. LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CONSOLE PLACEHOLDER",
            textAlign = TextAlign.Center
        )
    }
}