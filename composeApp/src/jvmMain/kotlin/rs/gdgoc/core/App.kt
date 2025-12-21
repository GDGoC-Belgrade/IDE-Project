package rs.gdgoc.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import rs.gdgoc.ui.layout.MainLayout
import rs.gdgoc.ui.layout.OpeningPage
import rs.gdgoc.ui.menubar.CustomToolbar
import androidx.compose.runtime.*

//all available screens
private sealed interface Screen {
    data object Opening : Screen
    data object Ide : Screen
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.Opening) }

    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            onRunClick = { println("Run clicked! ") },
            onNewFile = { println("New file") },
            onOpenFile = { println("Open file") },
            onSaveFile = { println("Save file") },
            onExit = {}
        )

        Divider(color = Color.Gray, thickness = 1.dp)

        when (screen) {
            Screen.Opening -> OpeningPage(
                onNewProject = { screen = Screen.Ide },
                onOpenProject = { println("TODO: open project") },        // placeholder
                onCloneRepository = { println("TODO: clone repo") },     // placeholder
                onSettings = { println("TODO: settings") }     // placeholder
            )

            Screen.Ide -> MainLayout()
        }
    }
}
