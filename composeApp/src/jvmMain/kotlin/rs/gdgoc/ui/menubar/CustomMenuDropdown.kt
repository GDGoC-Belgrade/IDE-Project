package rs.gdgoc.ui.menubar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomMenuDropdown(
    title: String,
    items: List<MenuItem>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = title,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 14.sp
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                when (item) {
                    is MenuItem.Separator -> Divider()
                    is MenuItem.Item -> {
                        DropdownMenuItem(
                            onClick = {
                                item.onClick()
                                expanded = false
                            }
                        ) {
                            Text(item.label)
                        }
                    }
                }
            }
        }
    }
}