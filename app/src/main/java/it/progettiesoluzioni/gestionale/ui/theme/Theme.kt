package it.progettiesoluzioni.gestionale.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Identità visiva P&S
val BrandNavy = Color(0xFF071B4A)
val BrandIndigo = Color(0xFF243D8F)
val BrandBlue = Color(0xFF1565C0)
val BrandCyan = Color(0xFF00A8CC)
val BrandGray = Color(0xFF67727E)
val BrandGrayLight = Color(0xFFCBD3DA)
val AppBackground = Color(0xFFF4F7FB)

// Colori funzionali dei servizi
val HaccpGreen = Color(0xFF2E7D32)
val HaccpContainer = Color(0xFFE7F4E8)
val SafetyOrange = Color(0xFFEF6C00)
val SafetyContainer = Color(0xFFFFEEDB)
val GdprPurple = Color(0xFF6A1B9A)
val GdprContainer = Color(0xFFF1E5F5)
val DeadlineRed = Color(0xFFC62828)
val DeadlineContainer = Color(0xFFFFE7E7)

private val PSLightColorScheme = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE5FA),
    onPrimaryContainer = BrandNavy,
    secondary = BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDEBFA),
    onSecondaryContainer = Color(0xFF083B70),
    tertiary = BrandCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD8F4FA),
    onTertiaryContainer = Color(0xFF003B49),
    background = AppBackground,
    onBackground = Color(0xFF18202A),
    surface = Color.White,
    onSurface = Color(0xFF18202A),
    surfaceVariant = Color(0xFFEDF1F5),
    onSurfaceVariant = Color(0xFF4D5965),
    outline = BrandGray,
    outlineVariant = BrandGrayLight,
    error = DeadlineRed,
    onError = Color.White
)

@Composable
fun PSGestionaleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PSLightColorScheme,
        content = content
    )
}
