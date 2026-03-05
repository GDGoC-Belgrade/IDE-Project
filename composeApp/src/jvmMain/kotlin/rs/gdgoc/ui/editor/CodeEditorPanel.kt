package rs.gdgoc.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeEditorPanel(
    content: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        BasicTextField(
            value = content,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    when (event.key) {
                        Key.Tab -> {
                            val spaces = "    "
                            val newText = content.text.substring(0, content.selection.start) +
                                    spaces +
                                    content.text.substring(content.selection.end)
                            val newCursor = content.selection.start + spaces.length
                            onValueChange(TextFieldValue(newText, TextRange(newCursor)))
                            true
                        }
                        else -> false
                    }
                }
                .drawBehind {
                    textLayoutResult?.let { layout ->
                        val cursorOffset = content.selection.start
                            .coerceAtMost(layout.layoutInput.text.length)
                        val currentLine = layout.getLineForOffset(cursorOffset)
                        val lineTop = layout.getLineTop(currentLine)
                        val lineBottom = layout.getLineBottom(currentLine)
                        drawRect(
                            color = highlightColor,
                            topLeft = Offset(0f, lineTop),
                            size = Size(size.width, lineBottom - lineTop)
                        )
                    }
                }
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            onTextLayout = { textLayoutResult = it },
            decorationBox = { innerTextField ->
                if (content.text.isEmpty()) {
                    Text(
                        text = "// Start typing...",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}
