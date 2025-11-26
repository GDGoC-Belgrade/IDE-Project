package rs.gdgoc.ui.menubar

sealed class MenuItem {
    data class Item(val label: String, val onClick: () -> Unit) : MenuItem()
    object Separator : MenuItem()

    companion object {
        operator fun invoke(label: String, onClick: () -> Unit): MenuItem = Item(label, onClick)
    }
}