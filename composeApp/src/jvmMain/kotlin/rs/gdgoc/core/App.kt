package rs.gdgoc.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun App() {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
                .border(width=1.dp, color= Color.Black),
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
                .border(width=1.dp, color= Color.Black),
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
            Box(
                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CONSOLE PLACEHOLDER",
                    textAlign = TextAlign.Center
                )
            }
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
            Box(
                modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "HIERARCHY PLACEHOLDER",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
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
            initialPositionPercentage = 1f,
            moveEnabled = true
        )
    }

    HorizontalSplitPane(splitPaneState = sideBarSplitPaneState) {
        first {
            TabScreen()
        }
        second {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RIGHT SIDEBAR PLACEHOLDER",
                    color = Color.White
                )
            }
        }
    }
}

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
                        color = if (selectedTabIndex == index) Color.Black
                        else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (selectedTabIndex == index) Color.Black
                                else Color.Transparent
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

@Composable
fun TabContent1() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = """
                fun main() {
                    println("Hello, Kotlin!")
                }
            """.trimIndent(),
            fontSize = 16.sp,
            lineHeight = 24.sp
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

