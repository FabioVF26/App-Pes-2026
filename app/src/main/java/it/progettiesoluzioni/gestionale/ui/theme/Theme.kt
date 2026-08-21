package it.progettiesoluzioni.gestionale.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palette estratta dal logo Progetti e Soluzioni
val BrandNavy = Color(0xFF051443)
val BrandIndigo = Color(0xFF232E80)
val BrandBlue = Color(0xFF0F63BF)
val BrandCyan = Color(0xFF00C0E9)
val BrandGray = Color(0xFF878C92)
val BrandGrayLight = Color(0xFFBBC2C5)
val BrandGrayLighter = Color(0xFFE2E2E5)

private val PSLightColorScheme = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9DEEE),
    onPrimaryContainer = BrandNavy,

    secondary = BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E4F7),
    onSecondaryContainer = Color(0xFF0A3A70),

    tertiary = BrandCyan,
    onTertiary = Color(0xFF00303F),
    tertiaryContainer = Color(0xFFCDEFF7),
    onTertiaryContainer = Color(0xFF00435A),

    background = Color.White,
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = BrandGrayLighter,
    onSurfaceVariant = Color(0xFF44474A),

    outline = BrandGray,
    outlineVariant = BrandGrayLight
)

@Composable
fun PSGestionaleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PSLightColorScheme,
        content = content
    )
}
