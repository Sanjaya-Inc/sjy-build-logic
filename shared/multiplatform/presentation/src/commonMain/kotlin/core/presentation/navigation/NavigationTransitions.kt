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
import androidx.compose.animation.slideInVertically
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
     */
    object Specs {
        /**
         * Duration Constants
         * Based on Material Design 3 motion duration tokens
         */
        object Duration {
            const val STANDARD = 400
            const val EMPHASIZED = 500
            const val QUICK = 250
        }

        /**
         * Custom Easing Curves
         * Provides natural, sophisticated motion feel
         */
        object Easing {
            val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
            val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
            val Standard = FastOutSlowInEasing
        }

        /**
         * Offset Calculation
         * Determines slide distance based on screen dimensions
         */
        object Offset {
            const val FULL_SCREEN = 1.0f
            const val PARTIAL_SCREEN = 0.3f
        }

        /**
         * Alpha Values for Fade Animations
         */
        object Alpha {
            const val TRANSPARENT = 0f
            const val SEMI_TRANSPARENT = 0.3f
            const val OPAQUE = 1f
        }

        /**
         * Scale Values for Zoom Animations
         */
        object Scale {
            const val NORMAL = 1f
            const val SLIGHTLY_REDUCED = 0.95f
            const val REDUCED = 0.9f
        }
    }

    /**
     * Default Forward Navigation Transition
     *
     * Behavior:
     * - New screen slides in from bottom with fade
     * - Current screen slides up with fade and slight scale
     * - Creates depth perception and directional awareness
     *
     * Use Case: Primary navigation flow (e.g., Dashboard → Transaction List)
     */
    val defaultForwardTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(
            initialOffsetY = { fullHeight -> (fullHeight * Specs.Offset.FULL_SCREEN).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            initialAlpha = Specs.Alpha.SEMI_TRANSPARENT
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> -(fullHeight * Specs.Offset.PARTIAL_SCREEN).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedDecelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            targetAlpha = Specs.Alpha.SEMI_TRANSPARENT
        ) + scaleOut(
            targetScale = Specs.Scale.SLIGHTLY_REDUCED,
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            )
        )
    }

    /**
     * Default Backward Navigation Transition (Pop)
     *
     * Behavior:
     * - Previous screen slides in from top with scale up
     * - Current screen slides down with fade
     * - Reverses the forward animation for consistency
     *
     * Use Case: Back navigation (e.g., Transaction List → Dashboard)
     */
    val defaultPopTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(
            initialOffsetY = { fullHeight -> -(fullHeight * Specs.Offset.PARTIAL_SCREEN).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedAccelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            initialAlpha = Specs.Alpha.SEMI_TRANSPARENT
        ) + scaleIn(
            initialScale = Specs.Scale.SLIGHTLY_REDUCED,
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            )
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> (fullHeight * Specs.Offset.FULL_SCREEN).toInt() },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            targetAlpha = Specs.Alpha.SEMI_TRANSPARENT
        )
    }

    /**
     * Predictive Back Gesture Transition
     *
     * Behavior:
     * - Supports Android's predictive back gesture
     * - Mirrors pop transition for consistency
     *
     * Use Case: Gesture-based back navigation on Android
     */
    val defaultPredictivePopTransition: AnimatedContentTransitionScope<*>.(Int) -> ContentTransform =
        { progress ->
            slideInVertically(
                initialOffsetY = { fullHeight -> -(fullHeight * Specs.Offset.PARTIAL_SCREEN).toInt() },
                animationSpec = tween(
                    durationMillis = Specs.Duration.EMPHASIZED,
                    easing = Specs.Easing.EmphasizedAccelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = Specs.Duration.EMPHASIZED,
                    easing = Specs.Easing.Standard
                ),
                initialAlpha = Specs.Alpha.SEMI_TRANSPARENT
            ) + scaleIn(
                initialScale = Specs.Scale.SLIGHTLY_REDUCED,
                animationSpec = tween(
                    durationMillis = Specs.Duration.EMPHASIZED,
                    easing = Specs.Easing.Standard
                )
            ) togetherWith slideOutVertically(
                targetOffsetY = { fullHeight -> (fullHeight * Specs.Offset.FULL_SCREEN).toInt() },
                animationSpec = tween(
                    durationMillis = Specs.Duration.EMPHASIZED,
                    easing = Specs.Easing.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = Specs.Duration.EMPHASIZED,
                    easing = Specs.Easing.Standard
                ),
                targetAlpha = Specs.Alpha.SEMI_TRANSPARENT
            )
        }

    /**
     * Modal Transition (Vertical Slide)
     *
     * Behavior:
     * - New screen slides up from bottom
     * - Current screen stays in place with dimming
     * - Creates modal/overlay effect
     *
     * Use Case: Modal screens (e.g., Add Transaction, Add Budget)
     */
    val modalForwardTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.QUICK,
                easing = Specs.Easing.Standard
            )
        ) togetherWith scaleOut(
            targetScale = Specs.Scale.REDUCED,
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            targetAlpha = Specs.Alpha.SEMI_TRANSPARENT
        )
    }

    /**
     * Modal Pop Transition (Vertical Slide Down)
     *
     * Behavior:
     * - Current screen slides down
     * - Previous screen scales up and fades in
     * - Reverses modal forward transition
     *
     * Use Case: Closing modal screens
     */
    val modalPopTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        scaleIn(
            initialScale = Specs.Scale.REDUCED,
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.Standard
            ),
            initialAlpha = Specs.Alpha.SEMI_TRANSPARENT
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = Specs.Duration.EMPHASIZED,
                easing = Specs.Easing.EmphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.QUICK,
                easing = Specs.Easing.Standard
            )
        )
    }

    /**
     * Fade Transition (Cross-Fade)
     *
     * Behavior:
     * - Simple cross-fade between screens
     * - No directional movement
     *
     * Use Case: Tab navigation, same-level screen switches
     */
    val fadeTransition: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        fadeIn(
            animationSpec = tween(
                durationMillis = Specs.Duration.STANDARD,
                easing = Specs.Easing.Standard
            )
        ) togetherWith fadeOut(
            animationSpec = tween(
                durationMillis = Specs.Duration.STANDARD,
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
