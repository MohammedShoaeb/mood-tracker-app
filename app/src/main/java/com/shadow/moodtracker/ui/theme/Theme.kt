package com.shadow.moodtracker.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ExtendedColorScheme(
    val mint: ColorFamily,
    val lime: ColorFamily,
    val orangePale: ColorFamily,
    val salmonSoft: ColorFamily,
    val redOrange: ColorFamily,
    val purble: ColorFamily,
)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

val extendedLight = ExtendedColorScheme(
    mint = ColorFamily(
        mintLight,
        onMintLight,
        mintContainerLight,
        onMintContainerLight,
    ),
    lime = ColorFamily(
        limeLight,
        onLimeLight,
        limeContainerLight,
        onLimeContainerLight,
    ),
    orangePale = ColorFamily(
        orangePaleLight,
        onOrangePaleLight,
        orangePaleContainerLight,
        onOrangePaleContainerLight,
    ),
    salmonSoft = ColorFamily(
        salmonSoftLight,
        onSalmonSoftLight,
        salmonSoftContainerLight,
        onSalmonSoftContainerLight,
    ),
    redOrange = ColorFamily(
        redOrangeLight,
        onRedOrangeLight,
        redOrangeContainerLight,
        onRedOrangeContainerLight,
    ),
    purble = ColorFamily(
        purbleLight,
        onPurbleLight,
        purbleContainerLight,
        onPurbleContainerLight,
    ),
)

val extendedDark = ExtendedColorScheme(
    mint = ColorFamily(
        mintDark,
        onMintDark,
        mintContainerDark,
        onMintContainerDark,
    ),
    lime = ColorFamily(
        limeDark,
        onLimeDark,
        limeContainerDark,
        onLimeContainerDark,
    ),
    orangePale = ColorFamily(
        orangePaleDark,
        onOrangePaleDark,
        orangePaleContainerDark,
        onOrangePaleContainerDark,
    ),
    salmonSoft = ColorFamily(
        salmonSoftDark,
        onSalmonSoftDark,
        salmonSoftContainerDark,
        onSalmonSoftContainerDark,
    ),
    redOrange = ColorFamily(
        redOrangeDark,
        onRedOrangeDark,
        redOrangeContainerDark,
        onRedOrangeContainerDark,
    ),
    purble = ColorFamily(
        purbleDark,
        onPurbleDark,
        purbleContainerDark,
        onPurbleContainerDark,
    ),
)

val extendedLightMediumContrast = ExtendedColorScheme(
    mint = ColorFamily(
        mintLightMediumContrast,
        onMintLightMediumContrast,
        mintContainerLightMediumContrast,
        onMintContainerLightMediumContrast,
    ),
    lime = ColorFamily(
        limeLightMediumContrast,
        onLimeLightMediumContrast,
        limeContainerLightMediumContrast,
        onLimeContainerLightMediumContrast,
    ),
    orangePale = ColorFamily(
        orangePaleLightMediumContrast,
        onOrangePaleLightMediumContrast,
        orangePaleContainerLightMediumContrast,
        onOrangePaleContainerLightMediumContrast,
    ),
    salmonSoft = ColorFamily(
        salmonSoftLightMediumContrast,
        onSalmonSoftLightMediumContrast,
        salmonSoftContainerLightMediumContrast,
        onSalmonSoftContainerLightMediumContrast,
    ),
    redOrange = ColorFamily(
        redOrangeLightMediumContrast,
        onRedOrangeLightMediumContrast,
        redOrangeContainerLightMediumContrast,
        onRedOrangeContainerLightMediumContrast,
    ),
    purble = ColorFamily(
        purbleLightMediumContrast,
        onPurbleLightMediumContrast,
        purbleContainerLightMediumContrast,
        onPurbleContainerLightMediumContrast,
    ),
)

val extendedLightHighContrast = ExtendedColorScheme(
    mint = ColorFamily(
        mintLightHighContrast,
        onMintLightHighContrast,
        mintContainerLightHighContrast,
        onMintContainerLightHighContrast,
    ),
    lime = ColorFamily(
        limeLightHighContrast,
        onLimeLightHighContrast,
        limeContainerLightHighContrast,
        onLimeContainerLightHighContrast,
    ),
    orangePale = ColorFamily(
        orangePaleLightHighContrast,
        onOrangePaleLightHighContrast,
        orangePaleContainerLightHighContrast,
        onOrangePaleContainerLightHighContrast,
    ),
    salmonSoft = ColorFamily(
        salmonSoftLightHighContrast,
        onSalmonSoftLightHighContrast,
        salmonSoftContainerLightHighContrast,
        onSalmonSoftContainerLightHighContrast,
    ),
    redOrange = ColorFamily(
        redOrangeLightHighContrast,
        onRedOrangeLightHighContrast,
        redOrangeContainerLightHighContrast,
        onRedOrangeContainerLightHighContrast,
    ),
    purble = ColorFamily(
        purbleLightHighContrast,
        onPurbleLightHighContrast,
        purbleContainerLightHighContrast,
        onPurbleContainerLightHighContrast,
    ),
)

val extendedDarkMediumContrast = ExtendedColorScheme(
    mint = ColorFamily(
        mintDarkMediumContrast,
        onMintDarkMediumContrast,
        mintContainerDarkMediumContrast,
        onMintContainerDarkMediumContrast,
    ),
    lime = ColorFamily(
        limeDarkMediumContrast,
        onLimeDarkMediumContrast,
        limeContainerDarkMediumContrast,
        onLimeContainerDarkMediumContrast,
    ),
    orangePale = ColorFamily(
        orangePaleDarkMediumContrast,
        onOrangePaleDarkMediumContrast,
        orangePaleContainerDarkMediumContrast,
        onOrangePaleContainerDarkMediumContrast,
    ),
    salmonSoft = ColorFamily(
        salmonSoftDarkMediumContrast,
        onSalmonSoftDarkMediumContrast,
        salmonSoftContainerDarkMediumContrast,
        onSalmonSoftContainerDarkMediumContrast,
    ),
    redOrange = ColorFamily(
        redOrangeDarkMediumContrast,
        onRedOrangeDarkMediumContrast,
        redOrangeContainerDarkMediumContrast,
        onRedOrangeContainerDarkMediumContrast,
    ),
    purble = ColorFamily(
        purbleDarkMediumContrast,
        onPurbleDarkMediumContrast,
        purbleContainerDarkMediumContrast,
        onPurbleContainerDarkMediumContrast,
    ),
)

val extendedDarkHighContrast = ExtendedColorScheme(
    mint = ColorFamily(
        mintDarkHighContrast,
        onMintDarkHighContrast,
        mintContainerDarkHighContrast,
        onMintContainerDarkHighContrast,
    ),
    lime = ColorFamily(
        limeDarkHighContrast,
        onLimeDarkHighContrast,
        limeContainerDarkHighContrast,
        onLimeContainerDarkHighContrast,
    ),
    orangePale = ColorFamily(
        orangePaleDarkHighContrast,
        onOrangePaleDarkHighContrast,
        orangePaleContainerDarkHighContrast,
        onOrangePaleContainerDarkHighContrast,
    ),
    salmonSoft = ColorFamily(
        salmonSoftDarkHighContrast,
        onSalmonSoftDarkHighContrast,
        salmonSoftContainerDarkHighContrast,
        onSalmonSoftContainerDarkHighContrast,
    ),
    redOrange = ColorFamily(
        redOrangeDarkHighContrast,
        onRedOrangeDarkHighContrast,
        redOrangeContainerDarkHighContrast,
        onRedOrangeContainerDarkHighContrast,
    ),
    purble = ColorFamily(
        purbleDarkHighContrast,
        onPurbleDarkHighContrast,
        purbleContainerDarkHighContrast,
        onPurbleContainerDarkHighContrast,
    ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)
val MaterialTheme.extendedColors: ExtendedColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColorScheme.current

val LocalExtendedColorScheme = staticCompositionLocalOf<ExtendedColorScheme> {
    error("No ExtendedColorScheme provided")
}

@Composable
fun MoodTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val extendedColorScheme = when {
        darkTheme -> extendedDarkHighContrast
        else -> extendedLightHighContrast
    }

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

