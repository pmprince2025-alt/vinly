# Implementation Plan
# Vinyl — Music Visualization Layer for Android

**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 2026-06-08

---

## Overview

Total estimated development time: **10–12 weeks** (solo developer) or **6–7 weeks** (2 developers).  
This plan is structured in 5 phases, each delivering a testable, runnable milestone.

---

## Phase 1 — Foundation & MediaSession Core
**Duration:** Week 1–2  
**Goal:** App runs, detects music from any app, and exposes a data stream. No UI beyond a debug screen.

---

### Step 1.1 — Project Setup

**Task:** Initialize Android project with correct configuration.

```
Actions:
- Create new Android project in Android Studio
  - Package: com.vinyl.app
  - Minimum SDK: 26
  - Language: Kotlin
  - Build system: Gradle (Kotlin DSL)
- Set up Gradle version catalog (libs.versions.toml)
- Add all dependencies from TRD §11.1
- Configure Hilt: add plugin + @HiltAndroidApp to Application class
- Set up Compose BOM and enable compose in build.gradle.kts
- Configure edge-to-edge in MainActivity:
    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)
- Set up baseline project structure (all packages, empty files)
```

**Deliverable:** App compiles and launches. Empty black screen.

---

### Step 1.2 — NotificationListenerService Setup

**Task:** Create and register the service that grants MediaSession access.

```
Files to create:
- VinylNotificationListenerService.kt (data/mediasession/)
- AndroidManifest.xml: register service with correct permission + intent-filter
- notification_listener_service.xml (res/xml/): service metadata

Key implementation:
- Extend NotificationListenerService
- Override onNotificationPosted(), onNotificationRemoved()
- Override onListenerConnected() / onListenerDisconnected()
- Expose a companion object StateFlow<Boolean> isConnected
- Add a SharedFlow<StatusBarNotification> for posting notification events
```

**Testing:** Manually grant Notification Listener permission in Settings. Log output confirms service connects and fires on Spotify notification.

---

### Step 1.3 — MediaSession Data Source

**Task:** Build the layer that extracts track data from MediaSession tokens found in notifications.

```
Files to create:
- MediaSessionDataSource.kt (interface)
- MediaSessionDataSourceImpl.kt

Implementation steps:
1. In onNotificationPosted(), extract MediaSession.Token:
   val token = notification.notification.extras
       .getParcelable(Notification.EXTRA_MEDIA_SESSION) as? MediaSession.Token
       ?: return

2. Build MediaControllerCompat from token:
   val controller = MediaControllerCompat(context, token)

3. Register MediaControllerCompat.Callback for live updates:
   controller.registerCallback(mediaCallback)

4. Expose StateFlow<TrackDataModel?> currentTrack
5. Expose StateFlow<PlaybackStateDataModel> playbackState
6. Handle multiple sessions: maintain Map<Token, Controller>
7. Priority logic: most recently active session (by lastPositionUpdateTime)
8. Expose sendCommand(PlaybackCommand) → controller.transportControls.*

Position polling:
- Launch coroutine: while(isActive) { updatePosition(); delay(500) }
- Pause coroutine when screen is off
```

**Testing:** Debug screen showing raw title / artist / playback state updated live.

---

### Step 1.4 — Repository + Domain Layer

**Task:** Wrap data source behind repository interface; create domain models; build UseCases.

```
Files to create:
- Track.kt, PlaybackState.kt, AppSettings.kt (domain/model/)
- MediaSessionRepository.kt (interface)
- MediaSessionRepositoryImpl.kt (data layer, delegates to DataSource)
- ObserveCurrentTrackUseCase.kt
- ObservePlaybackStateUseCase.kt
- SendPlaybackCommandUseCase.kt

Data → Domain mapping:
- TrackDataModel → Track (clean domain model, no Android imports)
- PlaybackStateDataModel → PlaybackState (with currentPosition() helper)

Hilt wiring:
- MediaModule.kt: bind DataSource and Repository as singletons
- RepositoryModule.kt: bind use cases
```

**Deliverable:** UseCases tested with unit tests. `ObserveCurrentTrackUseCase` emits correctly from a fake repository.

---

### Step 1.5 — Foreground Service

**Task:** Keep the NotificationListenerService alive and declare proper service types.

```
Files to create:
- VinylForegroundService.kt (service/)
- Update AndroidManifest.xml

Implementation:
- Start foreground with FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
- Build minimal persistent notification ("Vinyl is active")
- Notification channel: IMPORTANCE_LOW (no sound, no pop-up)
- START_STICKY return value
- Auto-start from VinylApplication.onCreate() if permission granted

Screen on/off receiver:
- Register BroadcastReceiver for ACTION_SCREEN_OFF / ACTION_SCREEN_ON
- On screen off: pause position polling coroutine
- On screen on: resume polling
```

---

## Phase 2 — Core UI: Vinyl Disc & Now Playing Screen
**Duration:** Week 3–4  
**Goal:** The main Now Playing screen renders with a spinning vinyl disc, album art, tonearm, and track metadata. Controls work.

---

### Step 2.1 — Theme & Design System

**Task:** Set up the full Compose theme before building any UI.

```
Files to create:
- Color.kt: all color tokens from DRD §2.1
- Type.kt: font setup (DM Serif Display, DM Sans, JetBrains Mono)
  - Add Google Fonts dependency or bundle as assets/
  - Create TextStyle objects for each role
- Theme.kt: MaterialTheme wrapper with dark color scheme
- VinylTheme.kt: custom CompositionLocal for VinylTheme data class
  (holds tonearmColor, labelRingColor, accentColor per theme)
```

---

### Step 2.2 — Vinyl Disc Component

**Task:** Build the `VinylDisc` composable. Pure Canvas rendering.

```
File: ui/nowplaying/components/VinylDisc.kt

Parameters:
@Composable
fun VinylDisc(
    albumArt: Bitmap?,
    rotation: Float,
    modifier: Modifier = Modifier,
    style: VinylStyle = VinylStyle.CLASSIC
)

Canvas draw steps (in order):
1. drawCircle() — vinyl body fill (dark, near-black)
2. Groove rings loop:
   for (i in 0..21) {
       val r = outerGrooveRadius * (i.toFloat() / 22f).pow(0.7f)
       drawCircle(color = grooveColor, radius = r, style = Stroke(0.5.dp.toPx()))
   }
3. drawCircle() — label area (clipPath for album art)
4. withSaveLayer/clipPath → drawBitmap (album art, circular crop)
5. drawCircle() — spindle (center 8dp circle, metallic colors via concentric fills)
6. drawArc() — specular highlight (top-right arc, white ~8%)

Rotation applied via:
Modifier.graphicsLayer { rotationZ = rotation }
(NOT inside Canvas — graphicsLayer avoids recomposition)

Album art loading:
- Accept Bitmap? directly (loaded by ViewModel via Coil)
- If null: draw generated placeholder (geometric pattern from title hash)
```

**Testing:** Preview with static bitmap. Verify groove rings look correct at all zoom levels.

---

### Step 2.3 — Tonearm Component

**Task:** Build the `Tonearm` composable.

```
File: ui/nowplaying/components/Tonearm.kt

Parameters:
@Composable
fun Tonearm(
    angle: Float,           // degrees, applied around pivot point
    pivotOffset: DpOffset,  // relative to disc center
    modifier: Modifier = Modifier
)

Canvas draw steps:
1. canvas.save()
2. canvas.rotate(angle, pivotX, pivotY)
3. drawRoundRect() — arm body (6dp wide, arm length)
4. drawCircle() — counterweight end (10dp)
5. drawRoundRect() — cartridge head (14dp×10dp, tilted 15°)
6. drawCircle() — needle tip (3dp, accent color)
7. canvas.restore()

Shadow: draw same shapes offset +2dp,+2dp at 20% opacity before main draw
```

---

### Step 2.4 — NowPlayingViewModel

**Task:** Wire domain layer to UI state.

```
File: ui/nowplaying/NowPlayingViewModel.kt

State: StateFlow<NowPlayingUiState>

ViewModel responsibilities:
1. Observe ObserveCurrentTrackUseCase → update track data
2. Observe ObservePlaybackStateUseCase → update playback status + position
3. Load album art via Coil ImageLoader → Bitmap → update state
4. Extract dominant color via Palette.from(bitmap) → update dominantColor
5. Compute vinylRotation: (positionMs / 1000f) * 200f (degrees)
6. Compute tonearmAngle: lerp(start, end, progress)
7. Compute tonearmAngle progress: position / duration (0.0–1.0)
8. Handle controlsVisible with auto-hide:
   - Set true on any touch event
   - Launch coroutine: delay(3000); set false

Commands:
fun onPlayPause()  → SendPlaybackCommandUseCase(PLAY or PAUSE)
fun onNext()       → SendPlaybackCommandUseCase(NEXT)
fun onPrevious()   → SendPlaybackCommandUseCase(PREVIOUS)
fun onSeek(pos)    → SendPlaybackCommandUseCase(SEEK, pos)
fun onTouched()    → reset controls auto-hide timer
```

---

### Step 2.5 — NowPlayingScreen Assembly

**Task:** Compose the full Now Playing screen.

```
File: ui/nowplaying/NowPlayingScreen.kt

Layout:
Box(modifier = Modifier.fillMaxSize()) {
    // Layer 1: Dynamic background
    DynamicBackground(bitmap = albumArt, dominantColor = dominantColor)
    
    // Layer 2: Main content column
    Column(horizontalAlignment = CenterHorizontally) {
        Spacer(fraction = 0.12f of screen height)
        
        Box {
            VinylDisc(albumArt, rotation, Modifier.size(discSize))
            Tonearm(angle, pivotOffset, Modifier.align(TopEnd))
        }
        
        Spacer(24.dp)
        TrackInfo(title, artist, album, isPlaying)
        Spacer(16.dp)
        SeekBar(position, duration, onSeek)
        Spacer(24.dp)
        PlaybackControls(isPlaying, onPlay, onPause, onNext, onPrevious)
    }
    
    // Controls visibility wrapper
    AnimatedVisibility(controlsVisible, enter=fadeIn, exit=fadeOut) {
        // PlaybackControls + SeekBar visible layer
    }
}

State hoisting: all state from NowPlayingViewModel via collectAsStateWithLifecycle()
```

---

### Step 2.6 — Playback Controls & Seek Bar

```
Files:
- PlaybackControls.kt
- SeekBar.kt (custom, per DRD §5.4)

PlaybackControls:
- Row with Previous, Play/Pause, Next
- Icon buttons with scale animation on press (spring 400 stiffness)
- AnimatedContent for play/pause icon swap

SeekBar:
- Box with custom Canvas draw (track + fill)
- pointerInput for drag handling
- Haptic: HapticFeedback.LongPress on drag start
- Emit onSeek(Float) where Float is 0.0–1.0 normalized position
```

---

## Phase 3 — Visual Polish & Animations
**Duration:** Week 5–6  
**Goal:** All animations are smooth and production-ready. Dynamic background works. Idle state designed. Transitions feel polished.

---

### Step 3.1 — Dynamic Background

```
File: ui/nowplaying/components/DynamicBackground.kt

Implementation:
1. Accept Bitmap? + Color
2. Scale bitmap to 80×80 (fast)
3. Android 12+: Apply RenderEffect blur via graphicsLayer:
   graphicsLayer {
       renderEffect = RenderEffect.createBlurEffect(40f, 40f, TILE_MODE_CLAMP)
           .asComposeRenderEffect()
   }
4. Android 8–11: Manual stack blur on background thread (Dispatchers.Default)
5. Animate between backgrounds with AnimatedContent crossfade(500ms)
6. Draw dark scrim on top: Box with background Color(0f, 0f, 0f, 0.62f)
```

---

### Step 3.2 — Smooth Rotation System

```
Refine rotation in NowPlayingViewModel + VinylDisc:

Problem: naive rotation = position × speed causes visual jumps
Solution: Use Compose Animatable<Float>

In VinylDisc or parent:
val rotation = remember { Animatable(0f) }
LaunchedEffect(isPlaying, targetRotation) {
    if (isPlaying) {
        // Animate to target (accounts for time since last update)
        rotation.animateTo(targetRotation, tween(500, easing = LinearEasing))
    } else {
        // Decelerate to stop
        rotation.animateTo(
            rotation.value,
            tween(800, easing = FastOutLinearInEasing)
        )
    }
}

Spin-up on first play: initial acceleration via spring before switching to linear
```

---

### Step 3.3 — Track Transition Animations

```
Wrap track-dependent content in AnimatedContent:
AnimatedContent(
    targetState = track,
    transitionSpec = {
        (slideInVertically { it / 4 } + fadeIn(tween(250))) togetherWith
        fadeOut(tween(150))
    }
)

Tonearm on skip:
1. Animate tonearm to IDLE_ANGLE (200ms, fast)
2. 100ms pause
3. Animate to new playing position (1400ms, spring)
Use coroutine with sequential delay(200) + delay(100) between steps

Album art crossfade: already handled by AnimatedContent in VinylDisc
```

---

### Step 3.4 — Idle Screen

```
File: ui/idle/IdleScreen.kt

Components:
- Ghost vinyl (same VinylDisc composable, 50% alpha, 5 RPM = 30°/sec rotation)
  rotation driven by a ticker coroutine not linked to MediaSession
- Idle tonearm at IDLE_ANGLE (static)
- Pulsing center glow: Canvas drawCircle with animated radius (2dp oscillation)
  animateFloat: 3s period, repeat, FastOutSlowInEasing

Text:
Text("Open any music app", style = captionStyle, color = white50)
Text("and start playing", style = captionStyle, color = white50)
```

---

### Step 3.5 — Equalizer Badge

```
File: ui/nowplaying/components/EqualizerBadge.kt

4-bar animated equalizer:
- Row of 4 Canvas-drawn vertical bars
- Each bar has an independent Animatable<Float> for height
- On play: launch 4 coroutines, each with random phase:
  while(isActive) {
      bar.animateTo(randomHeight(), tween(randomDuration()))
      delay(randomDelay())
  }
- On pause: animate all bars to minimum height (2dp)
- Color: dynamic accent at 90% opacity
```

---

## Phase 4 — Settings, Themes, Widgets
**Duration:** Week 7–8  
**Goal:** Settings screen functional. 3 themes working. DataStore persistence. Home screen widgets.

---

### Step 4.1 — Settings Screen

```
File: ui/settings/SettingsScreen.kt, SettingsViewModel.kt

Sections:
1. Appearance
   - Theme picker (3 options with visual swatches)
   - Vinyl style picker (4 options)
   - Vinyl texture picker (3 options)
2. Behavior
   - Controls auto-hide timeout (3s / 5s / Never)
   - Tilt parallax toggle
   - Battery Saver mode toggle
3. About
   - App version
   - Privacy policy link
   - Rate on Play Store

SettingsViewModel:
- Observe ObserveSettingsUseCase → SettingsUiState
- fun onThemeChange(theme: VinylTheme)
- fun onVinylStyleChange(style: VinylStyle)
All changes persisted via DataStore immediately
```

---

### Step 4.2 — Theme System Integration

```
1. VinylTheme enum: OBSIDIAN, AMBER, ARCTIC
2. VinylThemeData data class: all color tokens per theme (from DRD §2.2)
3. CompositionLocal<VinylThemeData> provided at app root:
   CompositionLocalProvider(LocalVinylTheme provides currentTheme) { ... }
4. Components consume: val theme = LocalVinylTheme.current
5. VinylDisc: apply theme.vinylBodyColor, theme.grooveColor
6. Tonearm: apply theme.tonearmBodyColor, theme.tonearmHighlightColor
7. Seek bar: theme.accentColor (overrides Palette-derived if theme forces it)
```

---

### Step 4.3 — Home Screen Widgets

```
Files: widget/VinylWidgetSmall.kt, VinylWidgetLarge.kt

Android Widgets use RemoteViews (XML-based, not Compose):

res/layout/widget_small.xml:
- ImageView (album art, circular via clipToOutline)
- TextView (track title)
- ImageButton (play/pause)

res/layout/widget_large.xml:
- ImageView (album art)
- TextView × 3 (title, artist, album)
- ImageButton × 3 (prev, play/pause, next)
- ProgressBar (seek, display-only)

Widget update mechanism:
- VinylForegroundService observes track/playback state
- On change: AppWidgetManager.updateAppWidget() with new RemoteViews
- Background image: blurred bitmap set via RemoteViews.setImageViewBitmap()

Widget pending intents:
- Play/Pause → broadcast intent handled by VinylForegroundService
- Opens app → PendingIntent.getActivity() to MainActivity
```

---

## Phase 5 — Permissions, Onboarding, Polish, & Release Prep
**Duration:** Week 9–10  
**Goal:** App is complete, stable, and ready for internal testing / Play Store submission.

---

### Step 5.1 — Permission Onboarding Screen

```
File: ui/onboarding/PermissionScreen.kt

Logic:
- Show on first launch (check FirstLaunch key in DataStore)
- Check if NotificationListenerService is enabled:
  NotificationManagerCompat.getEnabledListenerPackages(context)
      .contains(packageName)
- If not enabled: show full onboarding screen
- If enabled: skip directly to main screen

CTA action:
val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
context.startActivity(intent)

On resume (onResume in MainActivity):
Re-check permission; if granted → navigate to NowPlaying/Idle
If denied → show softer "Try Again" variant
```

---

### Step 5.2 — Navigation Graph

```
File: ui/navigation/VinylNavGraph.kt

Destinations:
- permission (shown if NLS not granted)
- idle (NLS granted, no active session)
- nowPlaying (active session detected)
- settings (from nowPlaying via gear icon)

Transitions:
- permission → idle: slideInHorizontally
- idle → nowPlaying: custom animated transition (disc materializes)
- nowPlaying → settings: slideInVertically (sheet-like)

Auto-navigation:
ViewModel StateFlow drives nav:
LaunchedEffect(uiState) {
    when (uiState) {
        is PermissionRequired → navController.navigate("permission")
        is Idle → navController.navigate("idle")
        is Playing → navController.navigate("nowPlaying")
    }
}
```

---

### Step 5.3 — Performance Audit

```
Checklist:
□ Profile with Android Studio Profiler: CPU, RAM, GPU
□ Verify VinylDisc runs at 60 FPS on mid-range device (e.g. Pixel 6a)
□ Confirm no unnecessary recompositions (use Layout Inspector > Recomposition counts)
□ Bitmap memory: Coil cache size set to 20% of available RAM
□ Background blur: confirm only recomputes on track change, not per frame
□ Verify battery drain: use Battery Historian; target < 3%/hr
□ Test with screen off: confirm polling pauses, service stays alive
□ Test cold start: < 800ms to first rendered frame

Optimization targets if over budget:
- Switch vinyl rotation to SurfaceView/Canvas thread if GPU bound
- Reduce groove ring count from 22 to 14
- Defer album art blur to background thread with lower priority
```

---

### Step 5.4 — Device & Compatibility Testing

```
Test matrix:
Device type          | Android version | Expected behavior
---------------------|-----------------|------------------
Pixel 8 Pro          | Android 15      | Full feature set
Pixel 6a             | Android 13      | Full feature set
Samsung S24          | Android 15/OneUI| NLS + Samsung media compat
OnePlus 12           | Android 14/OxygenOS | OEM NLS restrictions check
Budget device (≤4GB) | Android 10      | Reduced blur fallback
Tablet (10")         | Android 13      | Two-column layout check

Music app compatibility test:
□ Spotify
□ YouTube Music
□ Apple Music (Android)
□ Tidal
□ Amazon Music
□ Poweramp
□ VLC
□ Default local player (varies by OEM)

Known issues to document:
- Some Samsung OneUI versions restrict NLS for battery; user must whitelist Vinyl
- YouTube Music sometimes doesn't expose album art in metadata (use placeholder)
```

---

### Step 5.5 — Testing Suite

```
Unit tests:
- PlaybackState.currentPosition() — verify interpolation at various elapsed times
- tonearmAngle() — verify 0%, 50%, 100% positions
- Color extraction fallback — when bitmap is null
- ObserveCurrentTrackUseCase — fake repository, verify emissions
- NowPlayingViewModel state transitions: Idle → Playing → Paused → Track change

UI tests:
- NowPlayingScreen: album art displayed when bitmap loaded
- Playback controls: tap play/pause → command sent
- SeekBar: drag → onSeek called with correct normalized value
- Permission screen: CTA triggers settings intent

Integration test:
- MediaSession mock: inject fake MediaControllerCompat, verify ViewModel state
```

---

### Step 5.6 — Play Store Prep

```
Required assets:
- App icon: 512×512 PNG (vinyl record design, dark background)
- Feature graphic: 1024×500 PNG
- Screenshots: 6 phone screenshots (dark, vivid, showing animations with multiple genres)
- Short description: ≤80 chars
- Full description: explain NLS permission clearly, link privacy policy

Privacy policy: required for NLS permission apps
- Host at vinylapp.dev/privacy (or similar)
- Clearly state: reads notification metadata only, no data stored or transmitted

Content rating: Everyone
Category: Music & Audio

Release track: Internal → Closed testing (100 users) → Open testing → Production
```

---

## Milestone Summary

| Milestone | End of Week | Deliverable |
|-----------|-------------|-------------|
| M1: MediaSession Core | Week 2 | Music detected, data streams to debug screen |
| M2: Visual Core | Week 4 | Spinning vinyl, tonearm, controls working |
| M3: Polished Animations | Week 6 | All transitions smooth, idle state complete |
| M4: Settings & Widgets | Week 8 | Themes, customization, home screen widgets |
| M5: Release Ready | Week 10 | Onboarding, full test suite, Play Store assets |

---

## Risk Register

| Risk | Mitigation |
|------|-----------|
| OEM (Samsung, OnePlus) killing VinylForegroundService | Document battery whitelist steps in onboarding; link to dontkillmyapp.com |
| NLS permission revoked on Android update | Re-check on every app resume; show soft re-grant prompt |
| Coil 3 album art loading slow on track change | Pre-fetch next track art when queue info available (v1.1) |
| Compose Canvas performance on low-end devices | Benchmark threshold; fall back to 30 FPS cap below 3GB RAM |
| Play Store rejection for NLS permission | Prepare detailed justification; have privacy policy ready; be ready to appeal |

---

## OpenCode Prompt Template

When using OpenCode to generate individual files, use this structure for each step:

```
Context: Building Vinyl, an Android music visualization app.
Architecture: MVVM + Clean Architecture, Hilt DI, Jetpack Compose, Kotlin Coroutines.
Current step: [STEP NAME]

Existing files relevant to this step:
- [List files already created]

Task: Create [FILENAME] that [DESCRIPTION].

Requirements:
- [Specific requirement 1]
- [Specific requirement 2]

Domain models available: Track.kt, PlaybackState.kt, AppSettings.kt
The package name is: com.vinyl.app
```
