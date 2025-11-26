package rs.gdgoc

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import rs.gdgoc.core.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GDGOC-IDE-PROJECT",
        icon = painterResource("icons/gdg.png")
    ) {
        App()
    }
}