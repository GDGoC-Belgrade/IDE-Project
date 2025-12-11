package rs.gdgoc.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

//stores all terminal output
class TerminalState {
    //each string represents one printed line in terminal
    //mutableStateListOf - Compose reacts when it changes
    val lines : SnapshotStateList<String> = mutableStateListOf()

    fun appendLine(line : String) {
        lines.add(line)
    }

    fun clear(){
        lines.clear()
    }
}