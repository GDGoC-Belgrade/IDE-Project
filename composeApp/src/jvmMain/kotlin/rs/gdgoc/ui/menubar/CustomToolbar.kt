package rs.gdgoc.ui.menubar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomToolbar(
    onRunClick: () -> Unit,
    onNewFile: () -> Unit,
    onOpenFile: () -> Unit,
    onSaveFile: () -> Unit,
    onExit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFEEEEEE))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomMenuDropdown(
            title = "File",
            items = listOf(
                MenuItem("New", onNewFile),
                MenuItem("Open", onOpenFile),
                MenuItem.Separator,
                MenuItem("Save", onSaveFile),
                MenuItem("Save As", onSaveFile),
                MenuItem.Separator,
                MenuItem("Exit", onExit)
            )
        )

        CustomMenuDropdown(
            title = "Edit",
            items = listOf(
                MenuItem("Cut", {}),
                MenuItem("Copy", {}),
                MenuItem("Paste", {})
            )
        )

        CustomMenuDropdown(
            title = "Help",
            items = listOf(
                MenuItem("About", {})
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onRunClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50)
            ),
            modifier = Modifier.height(32.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Text("Run", color = Color.White)
        }

        Spacer(modifier = Modifier. weight(1f))
    }
}