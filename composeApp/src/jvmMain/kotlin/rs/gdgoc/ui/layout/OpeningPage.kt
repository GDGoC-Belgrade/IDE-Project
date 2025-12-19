package rs.gdgoc.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@Composable
fun OpeningPage() {

    // Box to align content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // Main content
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource("icons/gdg.png"),
                contentDescription = null,
                modifier = Modifier.height(100.dp)
            )

            Text("Welcome to IDE", fontSize = 32.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create a new project or open an existing one.",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Clone a repository to start working with version control.",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: add  icons
                SquareButton(
                    "New Project",
                    icon = null
                ) {
                    // Button functionality
                    println("New Project clicked")
                }
                SquareButton(
                    "Open",
                    icon = null
                ) {
                    // Button functionality
                    println("Open clicked")
                }
                SquareButton(
                    "Clone Repository",
                    icon = null
                ) {
                    // Button functionality
                    println("Clone Repository clicked")
                }
            }
        }

        // Settings button
        SettingsButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}


// Placeholder button UI
@Composable
fun SettingsButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(35.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE0E0E0))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { println("Settings clicked") },
        contentAlignment = Alignment.Center
    ) {
        // TODO: add icon
        Text("S", fontSize = 20.sp)
    }
}

// Placeholder button UI
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SquareButton(
    text: String,
    icon: (@Composable (() -> Unit))? = null,   // Made nullable, can be changed for simplicity
    onClick: () -> Unit
) {
    var hovered by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.width(115.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (hovered) Color(0xFFE8E8E8) else Color(0xFFE0E0E0))
                .border(
                    1.dp,
                    if (hovered) Color.DarkGray else Color.Gray,
                    RoundedCornerShape(12.dp)
                )
                .onPointerEvent(PointerEventType.Enter) { hovered = true }
                .onPointerEvent(PointerEventType.Exit) { hovered = false }
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            // Made nullable for now, can be changed for simplicity
            icon?.invoke()
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Text below button
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}
