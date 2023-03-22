package com.signora.calendario.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
class Shapes(
    var itemShape: CornerBasedShape = CircleShape
) {
    fun copy(
        itemShape: CornerBasedShape = this.itemShape,
    ): Shapes = Shapes(
        itemShape = itemShape
    )
}

internal val LocalShapes = staticCompositionLocalOf { Shapes() }