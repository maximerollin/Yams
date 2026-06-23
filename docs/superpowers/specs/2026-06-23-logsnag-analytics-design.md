# LogSnag Analytics Mirroring Design

## Goal

Add LogSnag as a temporary second analytics destination while keeping the existing PostHog integration unchanged. Every event currently sent to PostHog on Android must also be sent to LogSnag when the application is built for the alpha/closed or production Play Store track.

## Scope

- Keep the existing `AnalyticsTracker` API and all feature call sites unchanged.
- Keep PostHog event names, properties, setup, and delivery behavior unchanged.
- Mirror the existing events, including `application installed`, through the central Android analytics tracker.
- Use `io.github.vinceglb:logsnag-kotlin:1.2.0`.
- Do not add LogSnag user identification, insights, notifications, descriptions, icons, or new product events.
- Do not send LogSnag events from local, internal, or unknown release channels.

## Configuration

The app build exposes two additional values sourced from `local.properties`:

- `LOGSNAG_TOKEN`
- `LOGSNAG_PROJECT`

Both values must be non-blank before LogSnag can be configured. The Play Store deployment workflow writes them from GitHub Actions secrets alongside the existing PostHog configuration.

LogSnag is enabled only when `ANALYTICS_RELEASE_CHANNEL` is:

| Release channel | LogSnag behavior |
| --- | --- |
| `closed` | Enabled; event name starts with `Test ` |
| `production` | Enabled; event name is unchanged |
| Any other value | Disabled |

All LogSnag events use the `analytics` channel and `notify = false`.

## Architecture

The existing Android `PlatformAnalyticsTracker` remains the single fan-out point. Its `capture` method performs two independent operations:

1. Send the existing platform-prefixed event and properties to PostHog exactly as today.
2. Forward the raw event and equivalent properties to a configured LogSnag client.

The LogSnag integration is initialized from `YamsApp` with the token, project, and release channel. Its runtime state lives in `core:analytics`, next to the existing analytics runtime configuration. A missing or unsupported configuration leaves the LogSnag client unavailable, making the mirror operation a no-op.

This design avoids changes in feature modules and guarantees that future calls through `AnalyticsTracker.capture` reach both destinations without duplicating feature-level code.

## Event Mapping

PostHog continues to receive its current Android platform prefix:

```text
android game created
```

LogSnag does not receive the `android` event-name prefix:

```text
closed:     Test game created
production: game created
```

LogSnag receives every non-null property sent to PostHog, including the derived `platform` and `release_channel` properties. Because LogSnag accepts string tags only, each non-null property value is converted with its Kotlin string representation. Property keys remain unchanged.

## Failure Isolation

PostHog and LogSnag calls are isolated from one another. A LogSnag configuration or dispatch failure must not prevent the PostHog call, propagate to feature code, or interrupt application behavior.

The library's Android implementation queues requests through WorkManager. The app does not add its own retry or persistence layer.

## Testing and Verification

Implementation follows test-driven development. Tests are added before production changes for these pure behaviors:

- `closed` maps to a `Test ` event prefix.
- `production` keeps the raw event name.
- local, internal, and unknown release channels disable LogSnag delivery.
- null properties are removed and remaining values become LogSnag string tags.
- LogSnag event names never receive the `android` prefix.

After the focused tests pass, run the complete `core:analytics` test suite and an Android compile/build task that exercises the dependency and generated build configuration.

## Acceptance Criteria

- Existing PostHog behavior and feature event call sites are unchanged.
- Alpha/closed builds mirror every PostHog event to LogSnag as `Test <raw event>` in channel `analytics`.
- Production builds mirror every PostHog event to LogSnag as `<raw event>` in channel `analytics`.
- Local, internal, unknown, or incompletely configured builds send nothing to LogSnag.
- LogSnag receives equivalent non-null analytics properties as string tags.
- LogSnag failures are invisible to application features and do not block PostHog.
- Focused tests and the Android build pass.
