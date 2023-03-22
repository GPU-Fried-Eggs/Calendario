package com.signora.calendario.ui.theme

import androidx.compose.runtime.*

@Composable
fun CalenderTheme(
    colors: Colors = CalendarioTheme.colors,
    shapes: Shapes = CalendarioTheme.shapes,
    content: @Composable () -> Unit
) {
    val rememberedColors = remember { colors.copy() }.apply { updateColorsFrom(colors) }

    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalShapes provides shapes,
        content = content
    )
}

object CalendarioTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current
}