package rs.gdgoc.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import rs.gdgoc.core.TerminalState

@Composable
fun ConsolePanel(terminalState: TerminalState) {
    //ui automatically updates when state changes

    val listState = rememberLazyListState()

    //runs whenever the number of terminal lines changes, it scrolls to the bottom
    LaunchedEffect(terminalState.lines.size) {
        if (terminalState.lines.isNotEmpty()) {
            listState.animateScrollToItem(terminalState.lines.size)
        }
    }

    //current text the user is typing
    var inputText by remember { mutableStateOf("") }

    //command history and which command is currently browsed
    val history = remember { mutableStateListOf<String>() }
    var historyIndex by remember { mutableStateOf(-1) }

    //blinking cursor
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorVisible = !cursorVisible
            delay(500)
        }
    }

    //automatically focuses the terminal
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .border(1.dp, Color(0xFF3C3C3C))
    ) {
        //scrollable list of text, it displays all lines from terminal state and user input
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(6.dp),
            verticalArrangement = Arrangement.Top
        ) {
            //output lines
            itemsIndexed(terminalState.lines) { _, line ->
                Text(
                    text = line,
                    color = Color(0xFFCCCCCC),
                    style = TextStyle(fontFamily = FontFamily.Monospace)
                )
            }

            //input line
            item {
                val display =
                    "> " + inputText + if (cursorVisible) "|" else " "

                Text(
                    text = display,
                    color = Color(0xFFD7BA7D),
                    style = TextStyle(fontFamily = FontFamily.Monospace)
                )
            }
        }

        //key input handler
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->

                    if (event.type == KeyEventType.KeyDown) {

                        when (event.key) {

                            //ENTER - execute
                            Key.Enter -> {
                                if (inputText.isNotBlank()) {
                                    terminalState.appendLine("> $inputText")
                                    history.add(0, inputText) // newest first
                                    historyIndex = -1
                                }
                                inputText = ""
                                true
                            }

                            // BACKSPACE
                            Key.Backspace -> {
                                if (inputText.isNotEmpty()) {
                                    inputText = inputText.dropLast(1)
                                }
                                true
                            }

                            //ARROW UP - previous command
                            Key.DirectionUp -> {
                                if (history.isNotEmpty()) {
                                    if (historyIndex < history.lastIndex) {
                                        historyIndex++
                                        inputText = history[historyIndex]
                                    }
                                }
                                true
                            }

                            //ARROW DOWN - next command
                            Key.DirectionDown -> {
                                if (historyIndex > 0) {
                                    historyIndex--
                                    inputText = history[historyIndex]
                                } else {
                                    historyIndex = -1
                                    inputText = ""
                                }
                                true
                            }

                            else -> {
                                val c = event.utf16CodePoint.toChar()
                                if (!c.isISOControl()) {
                                    inputText += c
                                }
                                true
                            }
                        }
                    }

                    false
                }
        )
    }
}
