package rs.gdgoc.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import rs.gdgoc.ui.editor.TabScreen
import rs.gdgoc.ui.panels.ConsolePanel
import rs.gdgoc.ui.panels.HierarchyPanel
import rs.gdgoc.ui.panels.RightSidebarPanel


@Composable
fun MainLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
                .border(width = 1.dp, color = Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {}

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            VerticalEditorLayout()
        }

        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
                .border(width = 1.dp, color = Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {}
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
private fun VerticalEditorLayout() {
    val mainSplitPaneState = remember {
        SplitPaneState(
            initialPositionPercentage = 0.8f,
            moveEnabled = true
        )
    }

    VerticalSplitPane(
        splitPaneState = mainSplitPaneState
    ) {
        first {
            MainWorkspace()
        }
        second {
            ConsolePanel()
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun MainWorkspace() {
    val hierarchyEditorSplitState = remember {
        SplitPaneState(
            initialPositionPercentage = 0.2f,
            moveEnabled = true
        )
    }

    HorizontalSplitPane(splitPaneState = hierarchyEditorSplitState) {
        first {
            HierarchyPanel()
        }
        second {
            EditorAndSidebar()
        }
    }
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EditorAndSidebar() {
    val sideBarSplitPaneState = remember {
        SplitPaneState(
            initialPositionPercentage = 0.8f,
            moveEnabled = true
        )
    }

    HorizontalSplitPane(splitPaneState = sideBarSplitPaneState) {
        first {
            TabScreen()
        }
        second {
            RightSidebarPanel()
        }
    }
}