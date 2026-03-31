package rs.gdgoc.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import rs.gdgoc.core.ConsoleViewModel

@Composable
fun ConsolePanel(viewModel: ConsoleViewModel) {
    // collects terminal output lines from ViewModel
    val lines by viewModel.lines.collectAsState()
    // collects current input text (what user is typing)
    val inputText by viewModel.inputText.collectAsState()
    // used to auto-scroll to the bottom when new lines appear
    val listState = rememberLazyListState()

    // whenever new line is added, scroll terminal to bottom
    LaunchedEffect(lines) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(6.dp)
    ) {
        // render all terminal output lines
        items(lines) { line ->
            Text(
                text = line,
                color = Color(0xFFCCCCCC),
                fontFamily = FontFamily.Monospace
            )
        }

        // render current input line
        item {
            Text(
                text = "> $inputText",
                color = Color(0xFFD7BA7D),
                fontFamily = FontFamily.Monospace
            )
        }
    }

    val focusRequester = remember { FocusRequester() }

    // capture keyboard events for terminal input
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->

                if (event.type == KeyEventType.KeyDown) {

                    when (event.key) {

                        Key.Enter -> {
                            viewModel.onEnter()
                            true
                        }

                        Key.Backspace -> {
                            viewModel.onBackspace()
                            true
                        }

                        Key.DirectionUp -> {
                            viewModel.onArrowUp()
                            true
                        }

                        Key.DirectionDown -> {
                            viewModel.onArrowDown()
                            true
                        }

                        else -> {
                            if (event.key != Key.Unknown) {

                                val char = event.utf16CodePoint.toChar()

                                if (!char.isISOControl()) {
                                    viewModel.onCharTyped(char)
                                }
                            }
                            true
                        }
                    }
                } else {
                    false
                }
            }
    )

    // request focus when terminal opens so user can start typing immediately
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
