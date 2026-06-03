package ru.practicum.shoppinglist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ColorDarkPrimary,
    onPrimary = ColorBackgroundContainerDark,
    background = DarkContainer,
    onBackground = LightText,
    secondary = RegularElementDark,
    surface = SurfaceContainerHigh,
    onSurface = SchemesOnSurface,
    tertiary = NoActiveElement,
    tertiaryFixed = MediumDarkText

)

private val LightColorScheme = lightColorScheme(
    primary = ColorLightPrimary,
    onPrimary = ColorBackgroundContainerLight,
    background = LightContainer,
    onBackground = DarkText,
    secondary = RegularElementLight,
    surface = SurfaceContainerHigh,
    onSurface = SchemesOnSurface,
    tertiary = NoActiveElement,
    tertiaryFixed = MediumLightText

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}