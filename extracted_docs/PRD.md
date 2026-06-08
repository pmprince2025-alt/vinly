# Product Requirements Document
# Vinyl — Music Visualization Layer for Android

**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 2026-06-08

---

## 1. Vision

Vinyl is a premium music visualization overlay for Android that sits on top of any music app and transforms playback into a cinematic turntable experience. It does not play music — it listens, reads, and visualizes. The app reads metadata from the system's MediaSession API and renders a high-fidelity, animated vinyl record experience with album art, tonearm animation, dynamic backgrounds, and playback controls — all in a dark OLED-optimized UI.

The north star: a user opens Spotify, starts a track, swipes over to Vinyl, and feels like they're watching their music play in a high-end audiophile lounge.

---

## 2. Target Users

### Primary
- **Audiophiles & music enthusiasts** (18–35) who care deeply about the aesthetic of music listening and want a premium visual layer on top of streaming apps.
- **Android power users** who customize their phones extensively and want a more immersive now-playing experience than stock app UIs offer.

### Secondary
- **Casual listeners** who want a beautiful always-on screen for music when their phone is docked or charging.
- **Content creators** who record their listening sessions and want a cinematic visual backdrop.

### Non-users
- iOS users (Android-only, at least v1.0)
- Users who want a music player (Vinyl does not play music)
- Users who need lyrics display (out of scope for v1.0)

---

## 3. Problem Statement

Stock music apps (Spotify, YouTube Music, Apple Music on Android) have functional but uninspiring now-playing UIs. Third-party players like Poweramp offer customization but require users to abandon their preferred streaming service. There is no dedicated premium visualization layer that works on top of any app without replacing it.

Vinyl fills that gap: a pure visualization experience that is app-agnostic, renderer-focused, and premium by design.

---

## 4. Success Metrics

| Metric | Target (3 months post-launch) |
|--------|-------------------------------|
| Play Store rating | ≥ 4.5 stars |
| Day-7 retention | ≥ 40% |
| Day-30 retention | ≥ 20% |
| Session length (avg) | ≥ 8 minutes |
| Crash-free sessions | ≥ 99.5% |
| Premium conversion | ≥ 8% of active users |
| MediaSession detection rate | ≥ 95% across top 20 music apps |

---

## 5. Features

### 5.1 Core Features (v1.0 — Must Have)

#### F-01: MediaSession Integration
- Detect active MediaSession from any foreground music app
- Read: track title, artist name, album name, album art, playback state (playing/paused/stopped), playback position, duration
- Poll for metadata changes every 500ms with fallback to MediaSession callback
- Support multiple simultaneous sessions; prioritize the most recently active one
- Handle gracefully when no session is active (idle/empty state screen)

#### F-02: Vinyl Record Visualization
- Rotating vinyl disc rendered with Jetpack Compose Canvas API
- Rotation speed synchronized to actual playback (33⅓ RPM visual equivalent)
- Vinyl stops spinning instantly on pause; resumes with smooth acceleration easing
- Album art mapped as a circular label at the center of the record (inner ~40% of disc diameter)
- Realistic vinyl groove lines rendered as concentric arcs with subtle shading
- Disc has a glossy specular highlight that shifts with a slow parallax on device tilt (optional, via accelerometer)

#### F-03: Tonearm Animation
- SVG-style tonearm rendered in Compose Canvas
- Rests at idle position (outer edge) when paused or no music playing
- Swings to the correct radial position on play, proportional to playback progress (start of track = inner edge of outer groove, end = near label)
- Smooth cubic bezier easing on all tonearm movement
- Micro-vibration shimmer on tonearm head during playback

#### F-04: Playback Controls
- Play/Pause toggle button
- Next track, Previous track buttons
- Seek bar synced to MediaSession playback position
- All controls dispatch commands back through MediaSession (MediaController)
- Controls fade out after 3s of inactivity; tap anywhere to reveal

#### F-05: Album Art Display
- Album art extracted from MediaMetadata
- Displayed as circular crop centered on the vinyl label
- Fallback: auto-generated geometric placeholder art based on track title hash
- Smooth crossfade (300ms) when track changes

#### F-06: Dynamic Background
- Dominant color extracted from album art using Palette API
- Background is a deep, blurred, darkened version of the album art (Gaussian blur, ~40px radius, overlaid with 60% black scrim)
- Background transitions smoothly (500ms crossfade) between tracks
- OLED-safe: darkest region of background never exceeds #1A1A1A luminance

#### F-07: Now Playing Info
- Track title (truncated with marquee scroll if > 30 chars)
- Artist name
- Album name
- Elapsed time / total duration
- A subtle waveform or equalizer animation badge when playing

### 5.2 Enhanced Features (v1.1 — Should Have)

#### F-08: Theme System
- 3 built-in themes: Obsidian (default dark), Amber (warm vintage), Arctic (cool silver)
- Each theme controls: tonearm color, vinyl label ring color, accent color, background tint overlay
- Theme persists across sessions via DataStore

#### F-09: Vinyl Customization
- 4 vinyl label styles: Classic, Minimal, Picture Disc, Colored Vinyl
- 3 vinyl body textures: Standard Black, Carbon Fiber, Colored (accent tint)
- Customization screen accessible via long-press on the vinyl disc

#### F-10: Home Screen Widget
- 2×2 widget: album art circle + play/pause + track title
- 4×2 widget: full mini-player with album art, title, artist, prev/play/next, seek bar
- Widgets update via RemoteViews bound to a MediaSession-aware Service

#### F-11: Lock Screen Integration
- Custom lock screen media notification with full album art background
- Dismissible, respects system notification permissions
- Android 13+ media controls on lock screen with Vinyl's visual style applied to notification

### 5.3 Aspirational Features (v2.0 — Nice to Have)

#### F-12: Advanced Visual Effects
- Real-time audio-reactive EQ visualizer ring around the outer vinyl edge (requires audio permission + accessibility workaround or amplitude estimation from playback metadata)
- Particle dust system floating in background during playback
- Cassette tape mode (alternate visualization skin)
- Reel-to-reel mode

#### F-13: Sleep Timer
- Set a timer to pause playback after N minutes
- Visual countdown integrated into the UI

#### F-14: Scrobbling / Last.fm Integration
- Optional Last.fm scrobble via API when track is 50% played

---

## 6. User Flows

### 6.1 First Launch
```
App opens → Permission request screen (Notification Listener / MediaSession access)
→ User grants → Idle screen shown ("Play music in any app to begin")
→ User opens Spotify, plays a track → Vinyl detects MediaSession
→ Vinyl animates in: vinyl spins up, tonearm swings to position, background fades in
→ User is in the main Now Playing view
```

### 6.2 Track Change
```
Song ends or user skips → MediaSession fires metadata change
→ Album art crossfades (300ms) → Background transitions (500ms)
→ Vinyl disc texture/label updates → Tonearm sweeps back to start position
→ New track info appears with slide-up animation
```

### 6.3 Pause / Resume
```
User taps Pause (in Vinyl or source app) → Vinyl receives playback state change
→ Disc deceleration animation (~800ms ease-out) → Tonearm micro-lifts slightly
→ Controls remain visible → Background dims slightly
Resume: Disc accelerates back to full speed → Tonearm re-engages
```

### 6.4 App Not Playing
```
No active MediaSession → Idle screen shown
→ Animated idle vinyl (slow, ghostly spin at 50% opacity)
→ "Open your music app and play a track" message
→ Auto-detects when a session begins and transitions to Now Playing
```

---

## 7. Platform & Compatibility

| Requirement | Value |
|-------------|-------|
| Minimum Android version | Android 8.0 (API 26) |
| Target Android version | Android 15 (API 35) |
| Compiled SDK | 35 |
| Device support | Phones (primary), Tablets (adaptive layout) |
| Orientation | Portrait primary; landscape supported with adapted layout |
| Screen sizes | 5" – 7" phones optimal; tablet layout for ≥ 8" |
| OLED optimization | True black backgrounds (#000000) for OLED power savings |
| Edge-to-edge | Full edge-to-edge display support with WindowInsets handling |

---

## 8. Permissions

| Permission | Purpose | Required? |
|------------|---------|-----------|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Required to access MediaSession data from other apps | Yes — core feature |
| `FOREGROUND_SERVICE` | Keep Vinyl's MediaSession observer alive | Yes |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Android 14+ foreground service type | Yes |
| `POST_NOTIFICATIONS` | Show persistent "Vinyl is active" notification | Yes (Android 13+) |
| `RECEIVE_BOOT_COMPLETED` | Re-start observer service on boot | Optional |
| `BODY_SENSORS` (Accelerometer) | Vinyl tilt parallax effect | Optional |
| `INTERNET` | Last.fm scrobbling (v2.0) | Optional |

---

## 9. Monetization

| Tier | Features | Price |
|------|----------|-------|
| Free | Core visualization (F-01 to F-07), 1 theme, 1 vinyl style | Free |
| Premium (one-time IAP) | All themes, all vinyl styles, widgets, lock screen, advanced effects | $3.99 |
| No subscription model in v1.0 | | |

---

## 10. Out of Scope (v1.0)

- Playing music directly (Vinyl is a visualizer only)
- Lyrics display
- Crossfade / audio effects
- Social sharing
- iOS version
- Wear OS / Android Auto
- Tablet-first layout (tablets supported but phone is primary)
- Car mode

---

## 11. Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| MediaSession API changes in future Android | Medium | High | Abstract MediaSession layer, monitor Android releases |
| Some apps block MediaSession metadata (album art) | Medium | Medium | Graceful fallback to generated art; document known incompatible apps |
| Notification Listener permission scary for users | High | High | Clear onboarding explanation; link to privacy policy |
| 60 FPS vinyl animation draining battery | Medium | High | Reduce to 30 FPS when screen is idle >5s; configurable in settings |
| Play Store policy on Notification Listener | Low | High | Declare legitimate use clearly in app description and permission rationale |
