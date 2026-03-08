package rs.gdgoc.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import rs.gdgoc.file_system.openFileExplorer
import rs.gdgoc.ui.layout.MainLayout
import rs.gdgoc.ui.menubar.CustomToolbar

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun App() {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            onRunClick = { println("Run clicked! ") },
            onNewFile = { println("New file") },
            onOpenFile = {openFileExplorer()},
            onSaveFile = { println("Save file") },
            onExit = {}
        )

        Divider(color = Color.Gray, thickness = 1.dp)

        MainLayout()
    }

}
