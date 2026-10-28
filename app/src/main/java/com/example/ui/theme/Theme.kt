package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MatrixWarsColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFFB8F5FF),

    secondary = MatrixGreen,
    onSecondary = Color(0xFF002B13),
    secondaryContainer = Color(0xFF004D25),
    onSecondaryContainer = Color(0xFFB5FFD0),

    tertiary = SingularityGold,
    onTertiary = Color(0xFF291C00),
    tertiaryContainer = Color(0xFF543B00),
    onTertiaryContainer = Color(0xFFFFE08A),

    background = VoidBlack,
    onBackground = TextPrimary,

    surface = VoidSurface,
    onSurface = TextPrimary,
    surfaceVariant = VoidElevated,
    onSurfaceVariant = TextSecondary,

    outline = GridLineColor,
    outlineVariant = Color(0xFF182740),

    error = AlertRed,
    onError = Color.White
)

@Composable
fun MatrixWarsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MatrixWarsColorScheme,
        typography = Typography,
        content = content
    )
}

// Kept for backward compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MatrixWarsTheme(content = content)
}
