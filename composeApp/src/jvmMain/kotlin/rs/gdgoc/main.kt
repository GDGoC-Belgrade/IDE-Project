package rs.gdgoc

import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import rs.gdgoc.core.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GDGOC-IDE-PROJECT",
    ) {
        MenuBar {
            Menu("File") {
                Item("New", onClick = {})
                Item("Open", onClick = {})
                Separator()
                Item("Save", onClick = {})
                Item("Save As...", onClick = {})
                Separator()
                Item("Exit", onClick = { exitApplication() })
            }

            Menu("Edit") {
                Item("Cut", onClick = {})
                Item("Copy", onClick = {})
                Item("Paste", onClick = {})
            }

            Menu("Help") {
                Item("About", onClick = {})
            }
        }

        App()
    }
}