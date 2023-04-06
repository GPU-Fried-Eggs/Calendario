package com.signora.calendario.kanban.ui.checkbox

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.max

internal const val BoxInDuration = 50
internal const val BoxOutDuration = 100
internal const val CheckAnimationDuration = 100

private val CheckboxDefaultPadding = 2.dp
private val CheckboxSize = 20.dp
private val StrokeWidth = 2.dp
private val RadiusSize = 50.dp

@Stable
interface CheckboxColors {
    @Composable
    fun checkmarkColor(state: ToggleableState): State<Color>

    @Composable
    fun containerColor(enabled: Boolean, state: ToggleableState): State<Color>

    @Composable
    fun borderColor(enabled: Boolean, state: ToggleableState): State<Color>
}

/**
 * This component is forked from Material [Checkbox].
 * why fork? We need config some of hard coded feature and modified ugly and [rememberRipple].
 * TODO: Remove this when checkbox updated and meet the requirement
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = CheckboxSize,
    cornerRadius: Dp = RadiusSize,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    TriStateCheckbox(
        state = ToggleableState(checked),
        onClick = if (onCheckedChange != null) { { onCheckedChange(!checked) } } else null,
        interactionSource = interactionSource,
        enabled = enabled,
        colors = colors,
        size = size,
        cornerRadius = cornerRadius,
        modifier = modifier
    )
}

@Composable
fun TriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = CheckboxSize,
    cornerRadius: Dp = RadiusSize,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CheckboxColors = CheckboxDefaults.colors()
) {
    val transition = updateTransition(state, label = "transition")
    val checkDrawFraction by transition.animateFloat(
        transitionSpec = {
            when {
                initialState == ToggleableState.Off -> tween(CheckAnimationDuration)
                targetState == ToggleableState.Off -> snap(BoxOutDuration)
                else -> spring()
            }
        },
        label = "checkDrawFraction"
    ) {
        when (it) {
            ToggleableState.On -> 1f
            ToggleableState.Off -> 0f
            ToggleableState.Indeterminate -> 1f
        }
    }
    val checkCenterGravitationShiftFraction by transition.animateFloat(
        transitionSpec = {
            when {
                initialState == ToggleableState.Off -> snap()
                targetState == ToggleableState.Off -> snap(BoxOutDuration)
                else -> tween(durationMillis = CheckAnimationDuration)
            }
        },
        label = "checkCenterGravitationShiftFraction"
    ) {
        when (it) {
            ToggleableState.On -> 0f
            ToggleableState.Off -> 0f
            ToggleableState.Indeterminate -> 1f
        }
    }
    val checkCache = remember { CheckDrawingCache() }
    val checkColor by colors.checkmarkColor(state)
    val containerColor by colors.containerColor(enabled, state)
    val borderColor by colors.borderColor(enabled, state)

    Canvas(
        modifier = modifier
            .then(
                onClick?.let {
                    Modifier
                        .padding(CheckboxDefaultPadding)
                        .triStateToggleable(
                            state = state,
                            onClick = onClick,
                            enabled = enabled,
                            role = Role.Checkbox,
                            interactionSource = interactionSource,
                            indication = rememberRipple(
                                bounded = false,
                                radius = size - CheckboxDefaultPadding
                            )
                        )
                } ?: Modifier
            )
            .wrapContentSize(Alignment.Center)
            .requiredSize(size)
    ) {
        val strokeWidthPx = floor(StrokeWidth.toPx())
        drawContainer(
            boxColor = containerColor,
            borderColor = borderColor,
            radius = cornerRadius.toPx(),
            strokeWidth = strokeWidthPx
        )
        drawCheck(
            checkColor = checkColor,
            checkFraction = checkDrawFraction,
            crossCenterGravitation = checkCenterGravitationShiftFraction,
            strokeWidthPx = strokeWidthPx,
            drawingCache = checkCache
        )
    }
}

private fun DrawScope.drawContainer(
    boxColor: Color,
    borderColor: Color,
    radius: Float,
    strokeWidth: Float
) {
    val halfStrokeWidth = strokeWidth / 2.0f
    val stroke = Stroke(strokeWidth)
    val checkboxSize = size.width
    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
            cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
            style = Fill
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke
        )
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    strokeWidthPx: Float,
    drawingCache: CheckDrawingCache
) {
    val stroke = Stroke(strokeWidthPx, cap = StrokeCap.Square)
    val width = size.width
    val checkCrossX = 0.4f
    val checkCrossY = 0.7f
    val leftX = 0.2f
    val leftY = 0.5f
    val rightX = 0.8f
    val rightY = 0.3f

    fun lerp(start: Float, stop: Float, fraction: Float) = (1 - fraction) * start + fraction * stop

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.reset()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        // TODO: replace with proper declarative non-android alternative when ready (b/158188351)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.reset()
        pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path()
)

object CheckboxDefaults {
    @Composable
    fun colors(
        backgroundColor: Color = Color.Transparent,
        onBackgroundColor: Color = Color.Black.copy(0.65f)
    ): CheckboxColors =
        remember(backgroundColor, onBackgroundColor) {
            DefaultCheckboxColors(
                checkedCheckmarkColor = onBackgroundColor,
                uncheckedCheckmarkColor = onBackgroundColor.copy(alpha = 0f),
                checkedContainerColor = backgroundColor,
                uncheckedContainerColor = backgroundColor,
                disabledCheckedContainerColor = backgroundColor,
                disabledUncheckedContainerColor = backgroundColor,
                disabledIndeterminateContainerColor = backgroundColor,
                checkedBorderColor = onBackgroundColor,
                uncheckedBorderColor = onBackgroundColor,
                disabledBorderColor = onBackgroundColor,
                disabledIndeterminateBorderColor = onBackgroundColor,
            )
        }
}

@Preview
@Composable
private fun CheckboxPreview() {
    val state = remember { mutableStateOf(true) }

    Checkbox(state.value, { state.value = it })
}