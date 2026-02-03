# Architecture / Conventions / Design System

This document describes the project architecture, coding conventions, and UI design system usage.
It is the source of truth for edits. I will check this file before making code changes.

## Architecture overview
- Multi-module setup: `app`, `core`, `data`, `feature`, `iosApp`, plus `build-logic`.
- `core` contains shared models and the design system (themes, components, icons).
- `data` provides data sources and repositories.
- `feature` holds user-facing features and UI screens.
- Kotlin Multiplatform is used with shared code in `*src/commonMain*`.

## Module responsibilities
- `core/model`: domain models and enums used across the app.
- `core/designsystem`: reusable UI components, icons, theme primitives.
- `feature/*`: feature UI + state handling; keep feature logic local to the feature.

## Feature state patterns
- Game preparation keeps `gameSettings` as the single source of truth for UI.
- Game settings toggle buttons always include the Custom option; the current selection is driven by `gameSettings.ruleSet`.

## File and naming conventions
- Kotlin files use PascalCase and mirror their primary composable/class name.
- Composable functions are PascalCase, scoped by file (public at top, private below).
- Prefer small, focused composables and helpers; keep business logic out of UI.
- Use ASCII text for code and comments unless a file already uses accents.

## UI / Design system usage
- Use `MaterialTheme` + `YamsTheme` for colors, typography, and shapes.
- Prefer design system components (`AppInput`, `YamsPrimarySmallButton`, etc.) over raw M3 widgets.
- Use `Surface` + `RoundedCornerShape` for section containers and visual grouping.
- Keep spacing consistent: 8/12/16/20/24 dp steps.
- For editable values, use `AppInput` and numeric `KeyboardOptions` where applicable.

## Compose conventions
- Keep state hoisted when possible; use local `remember` only for UI-local state.
- Avoid heavy logic in composables; push updates through callbacks.
- Favor `Modifier.fillMaxWidth()` and `Arrangement.spacedBy()` for consistent layout.
- Previews should be lightweight and represent real usage states.

## Localization and copy
- UI copy is in French; keep labels consistent and corrected (accents included).
- Prefer short labels; use subtitles for additional context.

## Testing and verification
- If changes affect UI behavior, add/update previews to cover the new state.
- Run UI previews or local build when possible after significant UI changes.
