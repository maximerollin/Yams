# Gameplay Celebration Animation Design

## Goal

Replace the current big-score and Yams overlay with a fluid vector celebration that feels intentionally designed and renders consistently on Android and iOS.

The new treatment keeps the selected visual direction: dice converge around a soft halo, then reveal the player, celebration title, and awarded score. A Yams remains more expressive than an ordinary big score.

## Scope

This change replaces only the celebration shown after entering a Yams or a score of at least 30 points. It introduces local Lottie assets, a Compose Multiplatform renderer, localized dynamic text, reduced-motion behavior, previews, and focused tests.

It does not change score calculation, the score threshold, score persistence, turn advancement, game-result celebrations, or the final result screen's confetti.

## User Experience

### Yams

The Yams sequence lasts approximately 1.8 seconds:

1. A light scrim leaves the score sheet visible in the background.
2. Five vector dice converge toward the center with staggered timing.
3. A soft gold halo expands behind them.
4. The localized `YAMS !` title, player name, and awarded score appear clearly at the center.
5. The scene settles briefly, then fades out as a single composition.

The animation plays once. It contains no infinite rotation and no separate confetti system.

### Big score

The big-score sequence lasts approximately 1.2 seconds and uses the same motion language at lower intensity:

- two vector dice rather than five;
- a blue-green halo rather than a gold halo;
- fewer particles and a shorter settle phase;
- the localized big-score title, player name, and awarded score.

This hierarchy keeps a Yams exceptional while making a score of at least 30 points feel rewarding.

### Reduced motion

When the platform motion-duration scale disables animation, the Lottie sequence is skipped. The overlay uses a 120 ms Compose fade, holds the title and score for 800 ms, then exits with another 120 ms fade without moving dice or particles.

## Architecture

### Rendering library

Use the `compottie-lite` artifact in the gameplay feature. Compottie provides a pure Kotlin Compose Multiplatform Lottie renderer, and the lite artifact avoids the expression engine that these assets do not need.

Pin a stable version compatible with the project's Compose Multiplatform version in `gradle/libs.versions.toml`; do not use a dynamic dependency version. Add the dependency only to the module that renders the celebration.

### Assets

Store two bundled vector-only Lottie JSON files in the gameplay feature's Compose resources:

- `files/celebration_yams.json`
- `files/celebration_big_score.json`

The assets contain only decorative motion: dice, halos, depth layers, and particles. They contain no raster images, fonts, player names, titles, or score text. This keeps the files compact and prevents localization or dynamic-data limitations.

The animation canvas uses a transparent background and a composition aspect ratio that tolerates phone and tablet widths without cropping the dice.

### Components

Move celebration presentation out of `GamePlayScreen.kt` into a focused gameplay component file. The boundary is:

- `GamePlayScreen` determines whether a score creates a celebration and owns the current celebration event.
- `GamePlayCelebrationOverlay` selects the asset, renders the scrim and vector animation, overlays localized Compose text, and reports completion.
- A small internal Lottie renderer loads a bundled composition, plays it once, and exposes loading, failure, progress, and completion states.
- A reduced-motion/failure fallback renders the same information with Compose only.

Keep this component in the gameplay feature because its trigger rules, copy, and event lifecycle are feature-specific. Do not place the overlay in the shared design system unless a second feature needs the same component contract.

Remove the current projected-die drawing helpers, infinite transitions, dice configurations, and celebration-specific Canvas geometry from `GamePlayScreen.kt`.

## State and Event Flow

1. The player selects a score option.
2. The existing pure celebration rule classifies the score as `YAMS`, `BIG_SCORE`, or no celebration. Yams takes precedence over the numeric threshold.
3. The screen creates a celebration event containing a unique identifier, type, player name, and score.
4. Score persistence and turn advancement continue through the existing callback without waiting for the overlay.
5. The overlay loads the bundled composition and plays one iteration.
6. Completion of the animation, reduced-motion fallback, or load-failure fallback clears the matching event.
7. If the score ends the game, the finish dialog waits until the celebration event is cleared.

The overlay ignores stale completion callbacks by comparing the event identifier before clearing state. A recomposition or configuration change must not create a second celebration for an already-consumed score selection.

## Loading and Failure Behavior

Assets are local, so the feature performs no network access and has no download state.

While the JSON composition is parsing, the overlay may show the light scrim for at most one frame but must not display an empty blocking card. If parsing fails, the Compose-only fallback immediately presents the title and score using the same 120/800/120 ms fade-and-hold timing as reduced motion. A malformed asset therefore degrades the visual effect without blocking score entry or the end-of-game flow.

Animation completion is derived from playback state rather than a delay matching an assumed file duration. A defensive timeout clears the event after 2.2 seconds for a Yams and 1.6 seconds for a big score if a renderer callback never arrives.

## Copy and Localization

Retain the existing localized Yams title, big-score title, and player message resources. Keep all visible text in Compose so it uses the app's typography, accessibility semantics, locale, and current player data.

The score remains formatted through the existing points resource. No user-facing text is baked into the Lottie files.

## Accessibility

- The decorative Lottie content has no accessibility description and creates no semantic nodes.
- The title, player message, and score remain readable as one concise announcement.
- Color is not the only distinction between a Yams and a big score; each uses a different localized title and motion intensity.
- System-disabled animations use the reduced-motion fallback.
- The overlay is not interactive and does not introduce a dismiss button or hidden focus target.

## Previews and Tests

Add focused previews for:

- the full Yams state;
- the full big-score state;
- the reduced-motion/failure fallback for each type.

Keep the preview API able to hold the scene at a representative progress value so visual inspection does not depend on capturing a moving frame.

Add unit tests covering:

- Yams precedence over the big-score threshold;
- scores below 30 do not celebrate;
- 30 and higher scores celebrate as big scores;
- zero-value Yams selections do not celebrate;
- stale completion identifiers do not clear a newer event, if that comparison is extracted into testable state logic.

Run the gameplay feature tests and relevant Gradle compilation tasks for Android and iOS. Visually inspect both animation assets on representative phone and tablet previews, including light and dark themes if the feature supports both.

## Acceptance Criteria

- A successful Yams displays the approved five-dice gold vector sequence once and dismisses in approximately 1.8 seconds.
- A non-Yams score of at least 30 displays the shorter two-dice blue-green sequence once and dismisses in approximately 1.2 seconds.
- Player name, title, and score use localized Compose text and remain crisp at every supported screen density.
- No celebration uses an infinite transition, projected Canvas die, or celebration confetti.
- Score persistence and turn advancement are not delayed by animation playback.
- The finish dialog appears only after an active celebration is complete or safely falls back.
- Android and iOS use the same bundled assets and common UI implementation.
- Reduced-motion and asset-failure paths cannot leave the gameplay screen blocked.
