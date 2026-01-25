package core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * NavigationTransitions - Single Source of Truth for Navigation Animations
 *
 * Provides enterprise-grade, sophisticated screen transition animations following
 * Material Design 3 motion principles and Navigation3 best practices.
 *
 * Design Philosophy:
 * - Smooth, natural motion using custom easing curves
 * - Directional awareness (forward/backward navigation)
 * - Consistent timing across all transitions
 * - Layered animations (slide + fade) for depth perception
 * - Predictive back gesture support
 *
 * Architecture:
 * - Centralized animation specifications
 * - Immutable configuration objects
 * - Deep Module: Simple interface, complex implementation
 *
 * References:
 * - Material Design 3 Motion: https://m3.material.io/styles/motion
 * - Navigation3 Animations: https://developer.android.com/guide/navigation/navigation-3/recipes/animations
 */
object NavigationTransitions {

    /**
     * Animation Specifications
     * Single source of truth for all animation parameters
     *
     * Design Philosophy:
     * - Elegant timing: Slower, more refined animations (500ms enter, 400ms exit)
     * - Gentle easing: Smooth curves that feel natural, not aggressive
     * - Subtle parallax: Background moves at 60% speed (not too extreme)
     * - Minimal scale: Very subtle depth enhancement (98%, barely noticeable)
     * - Cohesive motion: Layers move together smoothly, not disconnected
     */
    object Specs {
        /**
         * Duration Constants
         * Slower timing for elegant, refined animations
         */
        object Duration {
            const val SCREEN_ENTER = 500  // Elegant, unhurried entrance
            const val SCREEN_EXIT = 400   // Responsive but not rushed exit
            const val MODAL = 400         // Slightly slower for modal feel
            const val FADE = 250          // Quick cross-fade for lateral movement
        }

        /**
         * Custom Easing Curves
         * Gentle, smooth curves for elegant motion
         */
        object Easing {
            // Smooth deceleration - gentle landing (inspired by iOS)
            val SmoothDecelerate = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
            // Smooth acceleration - gentle departure
            val SmoothAccelerate = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)
            val Standard = FastOutSlowInEasing
        }

        /**
         * Parallax Effect Constants
         * Subtle depth perception without extreme differential
         */
        object Parallax {
            const val FOREGROUND_OFFSET = 1.0f   // Foreground moves at full speed
            const val BACKGROUND_OFFSET = 0.6f   // Background moves at 60% speed (gentle parallax)
            const val BACKGROUND_SCALE = 0.98f   // Very subtle scale for depth (barely noticeable)
        }

        /**
         * Alpha Values for Fade Animations
         * Minimal fade to prevent white flash artifacts
         */
        object Alpha {
            const val OPAQUE = 1f
            const val SUBTLE_FADE = 0.95f  // Barely noticeable, no color artifacts
        }
    }

    /**
     * Default Forward Navigation Transition (with Gentle Parallax)
     *
     * Behavior:
     * - New screen slides in from RIGHT at full speed (foreground)
     * - Current screen slides out to LEFT at 60% speed with subtle scale (background parallax)
     * - Creates elegant depth without extreme differential
     * - Smooth, refined motion with gentle easing
     *
     * Parallax Effect:
     * - Foreground (incoming): 100% speed, no scale
     * - Background (outgoing): 60% speed, scales to 98%, subtle fade
     * - Result: Cohesive, elegant depth perception
     *
     * Timing: 500ms entrance for elegant, unhurried feel
     * Easing: Smooth curves for natural, refined motion
     *
     * Mental Model: New screen elegantly slides over old screen
     * Use Case: Primary navigation flow (e.g., Dashboard → Transaction List)
     */
    val defaultForwardTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth * Specs.Parallax.FOREGROUND_OFFSET).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_ENTER,
                easing = Specs.Easing.SmoothDecelerate
            )
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -(fullWidth * Specs.Parallax.BACKGROUND_OFFSET).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_EXIT,
                easing = Specs.Easing.SmoothAccelerate
            )
        ) + scaleOut(
            targetScale = Specs.Parallax.BACKGROUND_SCALE,
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_EXIT,
                easing = Specs.Easing.Standard
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_EXIT,
                easing = Specs.Easing.Standard
            ),
            targetAlpha = Specs.Alpha.SUBTLE_FADE
        )
    }

    /**
     * Default Backward Navigation Transition (Pop with Gentle Parallax)
     *
     * Behavior:
     * - Previous screen slides in from LEFT at 60% speed with subtle scale up (background parallax)
     * - Current screen slides out to RIGHT at full speed (foreground)
     * - Creates elegant reveal without extreme differential
     * - Smooth, refined motion with gentle easing
     *
     * Parallax Effect:
     * - Background (incoming): 60% speed, scales from 98% to 100%, fades in
     * - Foreground (outgoing): 100% speed, no scale
     * - Result: Cohesive, elegant reveal animation
     *
     * Timing: 500ms entrance, 400ms exit for refined feel
     * Easing: Smooth curves for natural, elegant motion
     *
     * Mental Model: Current screen elegantly slides away, revealing screen underneath
     * Use Case: Back navigation (e.g., Transaction List → Dashboard)
     */
    val defaultPopTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -(fullWidth * Specs.Parallax.BACKGROUND_OFFSET).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_ENTER,
                easing = Specs.Easing.SmoothDecelerate
            )
        ) + scaleIn(
            initialScale = Specs.Parallax.BACKGROUND_SCALE,
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_ENTER,
                easing = Specs.Easing.Standard
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_ENTER,
                easing = Specs.Easing.Standard
            ),
            initialAlpha = Specs.Alpha.SUBTLE_FADE
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * Specs.Parallax.FOREGROUND_OFFSET).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.SCREEN_EXIT,
                easing = Specs.Easing.SmoothAccelerate
            )
        )
    }

    /**
     * Predictive Back Gesture Transition (with Gentle Parallax)
     *
     * Behavior:
     * - Supports Android's predictive back gesture
     * - Mirrors pop transition with gentle parallax effect
     * - Smooth horizontal slide matching user's gesture
     * - Background screen scales up subtly as user drags
     *
     * Parallax Effect:
     * - Background (incoming): Gentle reveal with subtle scale up
     * - Foreground (outgoing): Follows gesture at full speed
     *
     * Timing: 500ms entrance, 400ms exit for refined feel
     * Easing: Smooth curves for elegant motion
     *
     * Mental Model: User elegantly "pulls" the previous screen back into view
     * Use Case: Gesture-based back navigation on Android
     */
    val defaultPredictivePopTransition: AnimatedContentTransitionScope<*>.(Int) -> ContentTransform =
        { _ ->
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -(fullWidth * Specs.Parallax.BACKGROUND_OFFSET).toInt() },
                animationSpec = tween(
                    durationMillis = Specs.Duration.SCREEN_ENTER,
                    easing = Specs.Easing.SmoothDecelerate
                )
            ) + scaleIn(
                initialScale = Specs.Parallax.BACKGROUND_SCALE,
                animationSpec = tween(
                    durationMillis = Specs.Duration.SCREEN_ENTER,
                    easing = Specs.Easing.Standard
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = Specs.Duration.SCREEN_ENTER,
                    easing = Specs.Easing.Standard
                ),
                initialAlpha = Specs.Alpha.SUBTLE_FADE
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> (fullWidth * Specs.Parallax.FOREGROUND_OFFSET).toInt() },
                animationSpec = tween(
                    durationMillis = Specs.Duration.SCREEN_EXIT,
                    easing = Specs.Easing.SmoothAccelerate
                )
            )
        }

    /**
     * Modal Transition (Vertical Slide Up)
     *
     * Behavior:
     * - New screen slides up from bottom (modal presentation)
     * - Current screen stays in place with subtle fade
     * - Creates clear modal/overlay mental model
     * - Vertical motion distinguishes from hierarchical navigation
     *
     * Timing: 400ms for modal feel
     * Easing: Smooth deceleration for elegant entrance
     *
     * Mental Model: Overlay appearing on top of current context
     * Use Case: Modal screens (e.g., Add Transaction, Add Budget)
     */
    val modalForwardTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = Specs.Duration.MODAL,
                easing = Specs.Easing.SmoothDecelerate
            )
        ) togetherWith fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.MODAL,
                easing = Specs.Easing.Standard
            ),
            targetAlpha = Specs.Alpha.SUBTLE_FADE
        )
    }

    /**
     * Modal Pop Transition (Vertical Slide Down)
     *
     * Behavior:
     * - Current screen slides down to bottom (dismissing modal)
     * - Previous screen fades back in from subtle fade
     * - Reverses modal forward transition
     * - Maintains vertical motion for modal mental model
     *
     * Timing: 400ms for modal feel
     * Easing: Smooth acceleration for elegant exit
     *
     * Mental Model: Overlay being dismissed, revealing underlying context
     * Use Case: Closing modal screens
     */
    val modalPopTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.MODAL,
                easing = Specs.Easing.Standard
            ),
            initialAlpha = Specs.Alpha.SUBTLE_FADE
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = Specs.Duration.MODAL,
                easing = Specs.Easing.SmoothAccelerate
            )
        )
    }

    /**
     * Fade Transition (Cross-Fade)
     *
     * Behavior:
     * - Simple cross-fade between screens
     * - No directional movement
     * - Quick and subtle
     *
     * Mental Model: Lateral movement at same hierarchy level
     * Use Case: Tab navigation, same-level screen switches
     */
    val fadeTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.FADE,
                easing = Specs.Easing.Standard
            )
        ) togetherWith fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.FADE,
                easing = Specs.Easing.Standard
            )
        )
    }

    /**
     * No Transition (Instant)
     *
     * Behavior:
     * - Instant screen change with no animation
     *
     * Use Case: Login → Dashboard (avoid animation on auth state change)
     */
    val noTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        EnterTransition.None togetherWith ExitTransition.None
    }
}

/**
 * TransitionSpecs - Pre-configured Transition Specifications
 *
 * Contains all transition specs that can be directly assigned to parameters.
 * Organized by use case for easy discovery.
 */
@Immutable
data class TransitionSpecs(
    val screenForward: AnimatedContentTransitionScope<*>.() -> ContentTransform,
    val screenBackward: AnimatedContentTransitionScope<*>.() -> ContentTransform,
    val screenPredictiveBack: AnimatedContentTransitionScope<*>.(Int) -> ContentTransform,
    val modalForward: AnimatedContentTransitionScope<*>.() -> ContentTransform,
    val modalBackward: AnimatedContentTransitionScope<*>.() -> ContentTransform,
    val fade: AnimatedContentTransitionScope<*>.() -> ContentTransform,
    val none: AnimatedContentTransitionScope<*>.() -> ContentTransform
)

/**
 * Default Transition Specifications
 *
 * Provides default implementations from NavigationTransitions and BottomNavAnimations.
 */
val DefaultTransitionSpecs = TransitionSpecs(
    screenForward = NavigationTransitions.defaultForwardTransition,
    screenBackward = NavigationTransitions.defaultPopTransition,
    screenPredictiveBack = NavigationTransitions.defaultPredictivePopTransition,
    modalForward = NavigationTransitions.modalForwardTransition,
    modalBackward = NavigationTransitions.modalPopTransition,
    fade = NavigationTransitions.fadeTransition,
    none = NavigationTransitions.noTransition
)

/**
 * CompositionLocal for TransitionSpecs
 *
 * Allows overriding transition specs at any level of the composition tree.
 */
val LocalTransitionSpecs = staticCompositionLocalOf { DefaultTransitionSpecs }
