package rs.gdgoc.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import rs.gdgoc.ui.dialogs.ExitConfirmationDialog
import rs.gdgoc.ui.layout.MainLayout
import rs.gdgoc.ui.menubar.CustomToolbar

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun App(onExitApplication: () -> Unit) {
    var showExitDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            onRunClick = { println("Run clicked! ") },
            onNewFile = { println("New file") },
            onOpenFile = { println("Open file") },
            onSaveFile = { println("Save file") },
            onExit = { showExitDialog = true }
        )

        Divider(color = Color.Gray, thickness = 1.dp)

        MainLayout()
    }

    ExitConfirmationDialog(
        showDialog = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirm = {
            showExitDialog = false
            onExitApplication()
        }
    )
}
