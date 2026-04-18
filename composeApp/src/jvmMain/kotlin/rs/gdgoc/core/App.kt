package rs.gdgoc.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import rs.gdgoc.ui.layout.MainLayout
import rs.gdgoc.ui.menubar.CustomToolbar
import rs.gdgoc.ui.menubar.MenuDefinition
import rs.gdgoc.ui.menubar.MenuItem

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun App() {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomToolbar(
            menus = listOf(
                MenuDefinition("File", listOf(
                    MenuItem("New", { println("New file") }),
                    MenuItem("Open", { println("Open file") }),
                    MenuItem.Separator,
                    MenuItem("Save", { println("Save file") }),
                    MenuItem("Save As", { println("Save file as") }),
                    MenuItem.Separator,
                    MenuItem("Exit", {})
                )),
                MenuDefinition("Edit", listOf(
                    MenuItem("Cut", {println("cut clicked")}),
                    MenuItem("Copy", {}),
                    MenuItem("Paste", {})
                )),
                MenuDefinition("Help", listOf(
                    MenuItem("About", {})
                ))
            ),
            trailingContent = {
                Button(
                    onClick = { println("Run clicked!") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run", color = Color.White)
                }
            }
        )

        Divider(color = Color.Gray, thickness = 1.dp)

        MainLayout()
    }

}
