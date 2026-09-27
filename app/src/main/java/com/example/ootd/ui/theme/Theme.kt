package com.example.ootd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OotdColorScheme = lightColorScheme(
    primary = OotdBlack,
    onPrimary = OotdWhite,
    secondary = OotdGray,
    onSecondary = OotdWhite,
    background = OotdCream,
    onBackground = OotdTextPrimary,
    surface = OotdWhite,
    onSurface = OotdTextPrimary,
    surfaceVariant = OotdCardBg,
    onSurfaceVariant = OotdTextSecondary,
    outline = OotdLightGray
)

@Composable
fun OOTDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OotdColorScheme,
        typography = OotdTypography,
        content = content
    )
}
