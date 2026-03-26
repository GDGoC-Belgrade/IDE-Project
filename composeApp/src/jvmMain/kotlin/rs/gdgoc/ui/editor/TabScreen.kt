package rs.gdgoc.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle

@Composable
fun TabScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tab 1", "Tab 2")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            selectedTabIndex = index
                        }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (selectedTabIndex == index) Color.Black else Color.Transparent
                            )
                    )
                }
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        when (selectedTabIndex) {
            0 -> TabContent1()
            1 -> TabContent2()
        }
    }
}

// Computes 1-based line and column from a TextFieldValue
fun cursorPosition(textFieldValue: TextFieldValue): Pair<Int, Int> {
    val cursorIndex = textFieldValue.selection.start
    val textBeforeCursor = textFieldValue.text.substring(0, cursorIndex)
    val line = textBeforeCursor.count { it == '\n' } + 1
    val col = cursorIndex - (textBeforeCursor.lastIndexOf('\n') + 1) + 1
    return Pair(line, col)
}

@Composable
fun TabContent1() {
    val initialCode = """
        fun main() {
            println("Hello, Kotlin!")
        }
    """.trimIndent()

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialCode,
                selection = TextRange(0) // cursor at start
            )
        )
    }

    val (line, col) = cursorPosition(textFieldValue)

    Column(modifier = Modifier.fillMaxSize()) {
        // Code editor area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.TopStart
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Status bar
        EditorStatusBar(line = line, col = col)
    }
}

@Composable
fun EditorStatusBar(line: Int, col: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8E8E8))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Ln $line, Col $col",
            fontSize = 12.sp,
            color = Color(0xFF555555),
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun TabContent2() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Jos neki kod ovde, ili prikazivanje neceg treceg.")
    }
}