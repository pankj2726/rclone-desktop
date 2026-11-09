package com.pankxu.arenacompanion.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LayoutInfo
import androidx.compose.ui.unit.IntOffset

/** Standard easing for app animations. */
object AppEasing {
    val Standard: Easing = Easing.bezier(0.2f, 0.0f, 0.0f, 1.0f)
    val Emphasized: Easing = Easing.bezier(0.3f, 0.0f, 0.0f, 1.0f)
    val Decelerate: Easing = Easing.bezier(0.0f, 0.0f, 0.2f, 1.0f)
}

/** Spring animation configuration. */
object AppSpring {
    val Standard = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    val Gentle = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    val Snappy = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumHigh)
}

/** Fade in animation for screen content. */
@Composable
fun AnimatedFadeIn(
    visible: Boolean,
    duration: Int = 300,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(durationMillis = duration),
        exit = fadeOut(durationMillis = duration / 2),
    ) { content() }
}

/** Slide up animation for new content (e.g., new messages, log entries). */
@Composable
fun AnimatedSlideUp(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) { content() }
}

/** Scale in animation for dialogs and cards. */
@Composable
fun AnimatedScaleIn(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.9f) + fadeIn(),
        exit = scaleOut(targetScale = 0.9f) + fadeOut(),
    ) { content() }
}

/** Progress animation for loading indicators. */
@Composable
fun AnimatedProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = AppSpring.Gentle,
        label = "progress",
    )
    // Caller uses animatedProgress in their drawing code
    // This composable just provides the animated value
}

/** Shimmer effect for loading states. */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    if (!enabled) return
    val shimmerAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = AppSpring.Standard,
        label = "shimmer",
    )
    // The actual shimmer rendering is done by the caller using shimmerAnim
}

/** Typewriter effect for streaming text. */
@Composable
fun TypewriterText(
    text: String,
    visibleChars: Int,
    modifier: Modifier = Modifier,
    content: @Composable (String) -> Unit,
) {
    val displayText = text.take(visibleChars)
    AnimatedFadeIn(visible = displayText.isNotEmpty()) {
        content(displayText)
    }
}
