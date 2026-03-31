package rs.gdgoc.core


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConsoleViewModel : ViewModel() {
    // holds all terminal output lines
    // each command output or user input gets appended here
    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines

    // holds all terminal output lines
    // each command output or user input gets appended here.
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    // stores command history for arrow key navigation
    private val history = mutableListOf<String>()
    private var historyIndex = -1

    // called when user presses enter
    // - adds current input to terminal output
    // - stores it in history
    // - resets input
    fun onEnter() {
        if (_inputText.value.isNotBlank()) {
            appendLine("> ${_inputText.value}")
            history.add(0, _inputText.value)
            historyIndex = -1
        }
        _inputText.value = ""
    }

    // handles backspace key
    // removes last character from current input string
    fun onBackspace() {
        _inputText.value = _inputText.value.dropLast(1)
    }

    // handles normal character input from keyboard
    // filters out control characters (like Shift, Ctrl, etc.)
    fun onCharTyped(c: Char) {
        if (!c.isISOControl()) {
            _inputText.value += c
        }
    }

    // navigates up in command history
    // shows previously executed commands
    fun onArrowUp() {
        if (historyIndex < history.lastIndex) {
            historyIndex++
            _inputText.value = history[historyIndex]
        }
    }

    // navigates down in command history
    // returns to newer commands or clears input if at latest
    fun onArrowDown() {
        if (historyIndex > 0) {
            historyIndex--
            _inputText.value = history[historyIndex]
        } else {
            historyIndex = -1
            _inputText.value = ""
        }
    }

    // helper function to append a new line to terminal output
    private fun appendLine(line: String) {
        _lines.value = _lines.value + line
    }
}