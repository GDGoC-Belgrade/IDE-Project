package rs.gdgoc.ui.menubar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Represents a top-level menu in the toolbar (e.g. "File", "Edit").
 */
data class MenuDefinition(
    val title: String,
    val items: List<MenuItem>
)

/**
 * A generic, reusable menu bar. Pass in any list of [MenuDefinition]s
 * and optional trailing content (e.g. a Run button).
 */
@Composable
fun CustomToolbar(
    menus: List<MenuDefinition>,
    modifier: Modifier = Modifier,
    trailingContent: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFEEEEEE))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        menus.forEach { menu ->
            CustomMenuDropdown(
                title = menu.title,
                items = menu.items
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        trailingContent()

        Spacer(modifier = Modifier.weight(1f))
    }
}