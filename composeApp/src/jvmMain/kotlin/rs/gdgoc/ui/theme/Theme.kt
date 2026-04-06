package rs.gdgoc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import rs.gdgoc.ui.theme.*
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = EditorBackground,
    surface = EditorSurface,
    onBackground = EditorForeground,
    onSurface = EditorForeground,
    primaryFixed = Green,

)

private val LightColorScheme = lightColorScheme(
    primary = whiteBgText,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Purple80,
    onBackground = whiteBg,
    onSurface = whiteBg,
)

@Composable
fun KotlinIDETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}