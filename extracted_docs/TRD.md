# Technical Requirements Document
# Vinyl — Music Visualization Layer for Android

**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 2026-06-08

---

## 1. Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0+ |
| UI Framework | Jetpack Compose 1.7+ |
| Design System | Material 3 (Material You) |
| Architecture | MVVM + Clean Architecture |
| State Management | StateFlow + Kotlin Coroutines |
| DI | Hilt |
| Navigation | Compose Navigation |
| Persistence | DataStore (Preferences) |
| Image Loading | Coil 3 |
| Color Extraction | AndroidX Palette |
| Build System | Gradle (Kotlin DSL) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

---

## 2. Architecture

### 2.1 Layer Overview

```
┌─────────────────────────────────────────────────┐
│                 UI Layer (Compose)               │
│  Screens · Components · ViewModels · UiState     │
└────────────────────┬────────────────────────────┘
                     │ StateFlow / Events
┌────────────────────▼────────────────────────────┐
│              Domain Layer                        │
│  UseCases · Domain Models · Repository Interfaces│
└────────────────────┬────────────────────────────┘
                     │ Interfaces / Flows
┌────────────────────▼────────────────────────────┐
│              Data Layer                          │
│  MediaSessionRepository · SettingsRepository     │
│  MediaSessionDataSource · DataStore              │
└─────────────────────────────────────────────────┘
```

### 2.2 Module Structure

```
app/
├── src/main/
│   ├── java/com/vinyl/
│   │   ├── di/                         # Hilt modules
│   │   │   ├── AppModule.kt
│   │   │   ├── MediaModule.kt
│   │   │   └── RepositoryModule.kt
│   │   │
│   │   ├── data/
│   │   │   ├── mediasession/
│   │   │   │   ├── MediaSessionDataSource.kt
│   │   │   │   ├── MediaSessionRepositoryImpl.kt
│   │   │   │   └── VinylNotificationListenerService.kt
│   │   │   ├── settings/
│   │   │   │   ├── SettingsDataSource.kt
│   │   │   │   └── SettingsRepositoryImpl.kt
│   │   │   └── model/
│   │   │       ├── TrackDataModel.kt
│   │   │       └── SettingsDataModel.kt
│   │   │
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Track.kt
│   │   │   │   ├── PlaybackState.kt
│   │   │   │   └── AppSettings.kt
│   │   │   ├── repository/
│   │   │   │   ├── MediaSessionRepository.kt
│   │   │   │   └── SettingsRepository.kt
│   │   │   └── usecase/
│   │   │       ├── ObserveCurrentTrackUseCase.kt
│   │   │       ├── ObservePlaybackStateUseCase.kt
│   │   │       ├── SendPlaybackCommandUseCase.kt
│   │   │       └── ObserveSettingsUseCase.kt
│   │   │
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── Theme.kt
│   │   │   │   ├── Color.kt
│   │   │   │   ├── Type.kt
│   │   │   │   └── VinylTheme.kt
│   │   │   ├── nowplaying/
│   │   │   │   ├── NowPlayingScreen.kt
│   │   │   │   ├── NowPlayingViewModel.kt
│   │   │   │   ├── NowPlayingUiState.kt
│   │   │   │   └── components/
│   │   │   │       ├── VinylDisc.kt
│   │   │   │       ├── Tonearm.kt
│   │   │   │       ├── TrackInfo.kt
│   │   │   │       ├── PlaybackControls.kt
│   │   │   │       ├── DynamicBackground.kt
│   │   │   │       └── SeekBar.kt
│   │   │   ├── idle/
│   │   │   │   ├── IdleScreen.kt
│   │   │   │   └── IdleViewModel.kt
│   │   │   ├── settings/
│   │   │   │   ├── SettingsScreen.kt
│   │   │   │   └── SettingsViewModel.kt
│   │   │   ├── onboarding/
│   │   │   │   └── PermissionScreen.kt
│   │   │   └── navigation/
│   │   │       └── VinylNavGraph.kt
│   │   │
│   │   ├── service/
│   │   │   └── VinylForegroundService.kt
│   │   │
│   │   ├── widget/
│   │   │   ├── VinylWidgetSmall.kt
│   │   │   └── VinylWidgetLarge.kt
│   │   │
│   │   └── VinylApplication.kt
│   │
│   └── res/
│       ├── drawable/
│       ├── layout/          # Widget layouts only (XML)
│       ├── values/
│       └── xml/
│           ├── notification_listener_service.xml
│           └── widget_info_*.xml
```

---

## 3. MediaSession Integration

### 3.1 Permission Architecture

Vinyl uses `NotificationListenerService` as the bridge to access MediaSession data from other apps. This is the only reliable, public API that grants access to another app's MediaSession.

```kotlin
// AndroidManifest.xml declaration
<service
    android:name=".data.mediasession.VinylNotificationListenerService"
    android:label="Vinyl Music Visualizer"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
    <meta-data
        android:name="android.notificationlistener.description"
        android:resource="@string/notification_listener_description" />
</service>
```

### 3.2 MediaSession Data Flow

```
NotificationListenerService
    └── onNotificationPosted()
        └── StatusBarNotification
            └── MediaSession.Token (from notification extras)
                └── MediaControllerCompat(context, token)
                    ├── getMetadata() → MediaMetadataCompat
                    │   ├── METADATA_KEY_TITLE
                    │   ├── METADATA_KEY_ARTIST
                    │   ├── METADATA_KEY_ALBUM
                    │   ├── METADATA_KEY_ALBUM_ART / METADATA_KEY_ALBUM_ART_URI
                    │   └── METADATA_KEY_DURATION
                    └── getPlaybackState() → PlaybackStateCompat
                        ├── STATE_PLAYING / STATE_PAUSED / STATE_STOPPED
                        └── getPosition()
```

### 3.3 MediaSessionDataSource

```kotlin
interface MediaSessionDataSource {
    val currentTrack: StateFlow<TrackDataModel?>
    val playbackState: StateFlow<PlaybackStateDataModel>
    
    fun sendCommand(command: PlaybackCommand)
    fun release()
}

data class PlaybackCommand(
    val action: PlaybackAction,
    val seekPosition: Long? = null
)

enum class PlaybackAction {
    PLAY, PAUSE, NEXT, PREVIOUS, SEEK
}
```

### 3.4 Multiple Session Handling

- Maintain a `Map<MediaSession.Token, MediaControllerCompat>` of active sessions
- Priority: most recently active session wins
- Detect session activity by monitoring `playbackState.lastPositionUpdateTime`
- Dispose stale controllers when `onNotificationRemoved()` fires

### 3.5 Polling Strategy

- Primary: `MediaController.Callback` registered for real-time updates
- Fallback polling: `CoroutineScope` with `delay(500ms)` for position updates (MediaSession position is not pushed continuously)
- Stop polling when app is in background and screen is off (save battery)

---

## 4. Key Data Models

### 4.1 Domain Models

```kotlin
// domain/model/Track.kt
data class Track(
    val title: String,
    val artist: String,
    val album: String,
    val albumArtBitmap: Bitmap?,
    val albumArtUri: Uri?,
    val duration: Long,           // milliseconds
    val sourcePackage: String     // e.g. "com.spotify.music"
)

// domain/model/PlaybackState.kt
data class PlaybackState(
    val status: Status,
    val position: Long,           // milliseconds
    val playbackSpeed: Float,
    val timestamp: Long           // System.currentTimeMillis() when position was reported
) {
    enum class Status { PLAYING, PAUSED, STOPPED, BUFFERING, NONE }
    
    // Interpolate current position accounting for elapsed time since last update
    fun currentPosition(): Long {
        return if (status == Status.PLAYING) {
            val elapsed = System.currentTimeMillis() - timestamp
            position + (elapsed * playbackSpeed).toLong()
        } else {
            position
        }
    }
}
```

### 4.2 UI State

```kotlin
// ui/nowplaying/NowPlayingUiState.kt
sealed interface NowPlayingUiState {
    object Idle : NowPlayingUiState
    object PermissionRequired : NowPlayingUiState
    
    data class Playing(
        val track: Track,
        val playbackState: PlaybackState,
        val dominantColor: Color,
        val vinylRotation: Float,
        val tonearmAngle: Float,
        val controlsVisible: Boolean,
        val theme: VinylTheme,
        val vinylStyle: VinylStyle
    ) : NowPlayingUiState
}
```

---

## 5. Animation System

### 5.1 Vinyl Rotation

The vinyl disc rotates as a Compose `Animatable<Float>`. Rotation is not driven by a fixed ticker but by playback position to keep sync.

```kotlin
// Rotation calculation
// 33⅓ RPM = 0.555 rotations per second = 200° per second
const val VINYL_DEGREES_PER_SECOND = 200f

// In ViewModel, update target rotation every 500ms
val targetRotation = (playbackPosition / 1000f) * VINYL_DEGREES_PER_SECOND

// Animate to target with spring spec to avoid jumps
val rotation = remember { Animatable(0f) }
LaunchedEffect(targetRotation) {
    rotation.animateTo(
        targetValue = targetRotation % 360f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
}
```

**Pause animation:** On pause, use `animateTo` with `tween(800, easing = FastOutSlowInEasing)` from current speed to 0.

### 5.2 Tonearm

Tonearm pivots on a fixed point. Angle ranges from `IDLE_ANGLE` (outer rest, ~−15°) to `PLAYING_START_ANGLE` (~25°) through `PLAYING_END_ANGLE` (~55°).

```kotlin
// Tonearm angle based on progress
fun tonearmAngle(progress: Float): Float {
    // progress: 0.0 (start of track) → 1.0 (end of track)
    return lerp(PLAYING_START_ANGLE, PLAYING_END_ANGLE, progress)
}

// Animate with spring on play/pause
val tonearmAngle = animateFloatAsState(
    targetValue = when {
        isPlaying -> tonearmAngle(progress)
        else -> IDLE_ANGLE
    },
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)
```

### 5.3 Background Transition

Use `AnimatedContent` with a custom `fadeIn + fadeOut` spec for background crossfade:

```kotlin
AnimatedContent(
    targetState = dominantColor,
    transitionSpec = {
        fadeIn(tween(500)) togetherWith fadeOut(tween(500))
    }
)
```

### 5.4 Control Visibility

Controls fade in/out with `AnimatedVisibility` using `fadeIn + slideInVertically` from bottom. Auto-hide timer implemented as a `LaunchedEffect` that cancels and restarts on any touch event.

### 5.5 Performance Targets

| Animation | Target FPS | Strategy |
|-----------|-----------|----------|
| Vinyl rotation | 60 FPS | Canvas drawArc, no recomposition |
| Tonearm | 60 FPS | Canvas rotate transform |
| Background blur | 30 FPS | Static blur, only re-render on track change |
| Controls fade | 60 FPS | `AnimatedVisibility` |

---

## 6. Rendering Architecture

### 6.1 VinylDisc Composable

The vinyl disc is drawn entirely in `Canvas { }` — no XML, no image assets for the record itself. This allows dynamic color application for themed variants.

**Draw order:**
1. Outer vinyl body (dark circle with subtle radial gradient for depth)
2. Groove rings (concentric arcs, ~20 rings, varying alpha for realism)
3. Label area (inner circle, ~40% of disc radius)
4. Album art (circular clip of bitmap)
5. Center spindle (small circle, metallic sheen)
6. Specular highlight arc (top-right quadrant, low-alpha white arc)

### 6.2 Dynamic Background

```
Album art bitmap
    ↓ Palette API → dominantColor
    ↓ Render to offscreen bitmap at 1/4 resolution
    ↓ Apply RenderEffect Gaussian blur (Android 12+)
    │  Fallback: manual bitmap blur (Android 8–11)
    ↓ Overlay 60% black scrim
    ↓ Display as background behind all UI
```

For Android 12+, use `RenderEffect.createBlurEffect(40f, 40f, TILE_MODE_CLAMP)` applied to an `ImageView` (interop) or a `GraphicsLayer` in Compose.

### 6.3 Compose Canvas vs RecyclerView

All animations are Canvas-based inside Composables. No `SurfaceView` or `TextureView` is used — pure Compose rendering pipeline for consistent theming and lifecycle management.

---

## 7. Foreground Service

Vinyl must maintain a foreground service to keep the `NotificationListenerService` alive and update widgets.

```kotlin
// VinylForegroundService.kt
@AndroidEntryPoint
class VinylForegroundService : Service() {
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            buildPersistentNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        return START_STICKY
    }
    
    private fun buildPersistentNotification(): Notification {
        // Minimal "Vinyl is active" notification
        // Not dismissible while service running
        // Tapping opens the main Vinyl activity
    }
}
```

---

## 8. Performance Requirements

### 8.1 Targets

| Metric | Target |
|--------|--------|
| UI frame rate | 60 FPS sustained during playback |
| Memory (RSS) | < 150 MB |
| Battery drain | < 3% per hour (screen on, active playback) |
| App cold start | < 800ms to first rendered frame |
| MediaSession detection latency | < 1 second after music starts |
| Album art load time | < 300ms from metadata received |

### 8.2 Strategies

- **Lazy recomposition:** Vinyl disc rotation uses `graphicsLayer { rotationZ = ... }` on a single composable — does not trigger recomposition, only re-renders the render node.
- **Background blur:** Computed once per track change, not per frame. Cached as a `Bitmap`.
- **Album art:** Loaded with Coil, cached in memory + disk cache. Crosshair-decoded to exact target size (no oversized bitmaps).
- **Polling:** Position polling pauses when app goes to background (screen off). Resumes on `onResume`.
- **Coroutine scope:** All media work runs on `Dispatchers.IO`; only UI state updates dispatched to `Dispatchers.Main`.

### 8.3 Battery Optimizations

- Register a `BroadcastReceiver` for `ACTION_SCREEN_OFF` / `ACTION_SCREEN_ON` to pause/resume polling and animation ticker.
- When screen is off > 30s, drop to 0 FPS (freeze last frame state) and reduce polling to every 5s.
- Expose a "Battery Saver" mode in settings that caps animations to 30 FPS.

---

## 9. Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    
    @Provides
    @Singleton
    fun provideMediaSessionDataSource(
        @ApplicationContext context: Context
    ): MediaSessionDataSource = MediaSessionDataSourceImpl(context)
    
    @Provides
    @Singleton
    fun provideMediaSessionRepository(
        dataSource: MediaSessionDataSource
    ): MediaSessionRepository = MediaSessionRepositoryImpl(dataSource)
}
```

---

## 10. Settings & Persistence

All settings stored via DataStore (Preferences):

```kotlin
object SettingsKeys {
    val THEME = stringPreferencesKey("theme")               // "OBSIDIAN" | "AMBER" | "ARCTIC"
    val VINYL_STYLE = stringPreferencesKey("vinyl_style")   // "CLASSIC" | "MINIMAL" | "PICTURE" | "COLORED"
    val VINYL_TEXTURE = stringPreferencesKey("vinyl_texture")
    val BATTERY_SAVER = booleanPreferencesKey("battery_saver")
    val TILT_PARALLAX = booleanPreferencesKey("tilt_parallax")
    val CONTROLS_TIMEOUT_SECONDS = intPreferencesKey("controls_timeout")
    val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
}
```

---

## 11. Build Configuration

```kotlin
// build.gradle.kts (app)
android {
    compileSdk = 35
    defaultConfig {
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}
```

### 11.1 Key Dependencies

```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")
    
    // Architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // DI
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Media
    implementation("androidx.media:media:1.7.0")
    
    // Image loading
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    
    // Color extraction
    implementation("androidx.palette:palette-ktx:1.0.0")
    
    // Persistence
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
```

---

## 12. Testing Requirements

| Test Type | Coverage Target | Tools |
|-----------|----------------|-------|
| Unit tests (domain/data) | ≥ 80% | JUnit5, MockK, Turbine |
| ViewModel tests | ≥ 70% | JUnit5, MockK, Turbine |
| Compose UI tests | Key screens | Compose Test, Espresso |
| Integration tests | MediaSession flow | Robolectric |

### Key Test Cases

- `ObserveCurrentTrackUseCase`: verify emission when MediaSession metadata changes
- `PlaybackState.currentPosition()`: verify interpolation is accurate
- `NowPlayingViewModel`: verify state transitions from Idle → Playing → Paused
- `VinylDisc`: snapshot test for rendering consistency
- `TonearmAngle`: unit test angle calculation at 0%, 50%, 100% progress
