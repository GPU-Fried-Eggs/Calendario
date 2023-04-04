package com.signora.calendario.ui.pager

import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

private const val LowVelocityAnimationDefaultDuration = 500

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun flingBehavior(
    state: PagerState,
    pagerSnapDistance: PagerSnapDistance = PagerSnapDistance.atMost(1),
    lowVelocityAnimationSpec: AnimationSpec<Float> = tween(
        durationMillis = LowVelocityAnimationDefaultDuration,
        easing = LinearEasing,
    ),
    highVelocityAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
): SnapFlingBehavior {
    val density = LocalDensity.current

    return remember(lowVelocityAnimationSpec, highVelocityAnimationSpec, snapAnimationSpec, pagerSnapDistance, density) {
        val snapLayoutInfoProvider = SnapLayoutInfoProvider(state, pagerSnapDistance, highVelocityAnimationSpec)
        SnapFlingBehavior(snapLayoutInfoProvider, lowVelocityAnimationSpec, highVelocityAnimationSpec, snapAnimationSpec, density)
    }
}

@Stable
interface PagerSnapDistance {
    fun calculateTargetPage(startPage: Int, suggestedTargetPage: Int): Int

    companion object {
        fun atMost(pages: Int): PagerSnapDistance = PagerSnapDistanceMaxPages(pages)
    }
}

private class PagerSnapDistanceMaxPages(private val pagesLimit: Int): PagerSnapDistance {
    override fun calculateTargetPage(startPage: Int, suggestedTargetPage: Int): Int =
        suggestedTargetPage.coerceIn(startPage - pagesLimit, startPage + pagesLimit)

    override fun equals(other: Any?): Boolean =
        if (other is PagerSnapDistanceMaxPages) this.pagesLimit == other.pagesLimit else false

    override fun hashCode(): Int = pagesLimit.hashCode()
}

@OptIn(ExperimentalFoundationApi::class)
private fun SnapLayoutInfoProvider(
    pagerState: PagerState,
    pagerSnapDistance: PagerSnapDistance,
    decayAnimationSpec: DecayAnimationSpec<Float>
): SnapLayoutInfoProvider = object : SnapLayoutInfoProvider {
    private val layoutInfo: LazyListLayoutInfo
        get() = pagerState.lazyListState.layoutInfo

    override fun Density.calculateApproachOffset(initialVelocity: Float): Float {
        val effectivePageSizePx = pagerState.pageSize + pagerState.pageSpacing
        val animationOffsetPx = decayAnimationSpec.calculateTargetValue(0f, initialVelocity)
        val startPage = pagerState.currentLayoutPageInfo?.let { if (initialVelocity < 0) it.index + 1 else it.index } ?: pagerState.currentPage

        val scrollOffset = layoutInfo.visibleItemsInfo.firstOrNull { it.index == startPage }?.offset ?: 0

        val targetOffsetPx = startPage * effectivePageSizePx + animationOffsetPx

        val targetPageValue = targetOffsetPx / effectivePageSizePx
        val targetPage = (if (initialVelocity > 0) ceil(targetPageValue) else floor(targetPageValue)).toInt().coerceIn(0, pagerState.pageCount)

        // limited by PagerSnapDistanceMaxPages. default: [startPage - 1..startPage + 1]
        val correctedTargetPage = pagerSnapDistance.calculateTargetPage(startPage, targetPage).coerceIn(0, pagerState.pageCount)

        val proposedFlingOffset = (correctedTargetPage - startPage) * effectivePageSizePx

        val flingApproachOffsetPx = (proposedFlingOffset.absoluteValue - scrollOffset.absoluteValue).coerceAtLeast(0)

        return if (flingApproachOffsetPx != 0) flingApproachOffsetPx * initialVelocity.sign else 0f
    }

    override fun Density.calculateSnappingOffsetBounds(): ClosedFloatingPointRange<Float> {
        var lowerBoundOffset = Float.NEGATIVE_INFINITY
        var upperBoundOffset = Float.POSITIVE_INFINITY

        layoutInfo.visibleItemsInfo.forEach { item ->
            val offset = item.offset.toFloat()

            // Find item that is closest to the center
            if (offset <= 0 && offset > lowerBoundOffset) lowerBoundOffset = offset

            // Find item that is closest to center, but after it
            if (offset >= 0 && offset < upperBoundOffset) upperBoundOffset = offset
        }

        return lowerBoundOffset.rangeTo(upperBoundOffset)
    }

    override fun Density.calculateSnapStepSize(): Float = with(layoutInfo) {
        if (visibleItemsInfo.isNotEmpty())
            visibleItemsInfo.sumOf { it.size } / visibleItemsInfo.size.toFloat()
        else 0f
    }
}