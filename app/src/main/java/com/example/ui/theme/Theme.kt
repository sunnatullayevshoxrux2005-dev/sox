package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ParkwayDarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = Navy800,
    onPrimaryContainer = BrandCyan,
    secondary = BrandCyan,
    onSecondary = Navy950,
    tertiary = BrandAmber,
    onTertiary = Navy950,
    background = Navy950,
    onBackground = Color(0xFFF8FAFC),
    surface = Navy900,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Navy800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800,
    error = BrandRed,
    onError = Color.White
)

val ParkwayShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(22.dp)
)

@Composable
fun ParkwayTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ParkwayDarkColorScheme,
        typography = ParkwayTypography,
        shapes = ParkwayShapes,
        content = content
    )
}
