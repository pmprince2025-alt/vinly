# Design Requirements Document
# Vinyl — Music Visualization Layer for Android

**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 2026-06-08

---

## 1. Design Philosophy

Vinyl is built around a single design principle: **the music is the interface**. Every visual decision should serve the experience of the music, not compete with it. The UI recedes into the background; the vinyl record and album art take center stage.

**Three pillars:**

1. **Premium restraint** — Dark, minimal, nothing unnecessary. The vinyl disc should feel like a physical object floating in darkness.
2. **Living materials** — Everything moves as if it has physical weight. The disc spins with inertia. The tonearm swings with damping. The background breathes with color.
3. **OLED-first** — True black backgrounds (#000000) for OLED power savings and visual depth. Nothing should feel bright or harsh.

---

## 2. Visual Style

### 2.1 Color System

#### Base Palette

| Token | Value | Usage |
|-------|-------|-------|
| `color-background` | `#000000` | App background, OLED true black |
| `color-surface` | `#0D0D0D` | Cards, bottom sheets |
| `color-surface-elevated` | `#1A1A1A` | Elevated components |
| `color-on-surface` | `#FFFFFF` | Primary text on dark |
| `color-on-surface-muted` | `#FFFFFF80` | Secondary text, 50% opacity |
| `color-on-surface-subtle` | `#FFFFFF33` | Tertiary text, 20% opacity |
| `color-divider` | `#FFFFFF14` | Dividers, borders |
| `color-accent` | Dynamic | Extracted from album art via Palette |

#### Dynamic Color
The accent color is extracted from the current album art using AndroidX Palette. It is used for:
- Tonearm needle tip highlight
- Seek bar fill
- Equalizer animation color
- Vinyl label ring glow (subtle)

Extracted color must be darkened/desaturated if luminance > 0.4 to maintain legibility against dark backgrounds.

```kotlin
// Color extraction and adjustment
fun Palette.getAccentColor(fallback: Color): Color {
    val swatch = vibrantSwatch ?: lightVibrantSwatch ?: dominantSwatch
    val raw = swatch?.rgb?.let { Color(it) } ?: fallback
    // Ensure it's not too bright for OLED context
    return raw.copy(alpha = 1f).darkenIfNeeded(maxLuminance = 0.35f)
}
```

### 2.2 Built-in Themes

#### Theme 1: Obsidian (Default)
- Background: `#000000`
- Tonearm: Brushed dark chrome (`#2C2C2C` body, `#888` highlight)
- Vinyl body: Near-black with subtle blue sheen (`#0A0A12`)
- Vinyl grooves: `#FFFFFF08` to `#FFFFFF18`
- Label ring: Dynamic accent color at 60% opacity
- Track info text: `#FFFFFF` / `#FFFFFF80`

#### Theme 2: Amber (Vintage Warm)
- Background: `#0D0900`
- Tonearm: Warm brass (`#6B4C1E` body, `#C9952A` highlight)
- Vinyl body: Deep brown-black (`#100A04`)
- Vinyl grooves: `#FFFFFF06` to `#FFFFFF14`
- Label ring: Amber/gold tint `#C9952A` at 70% opacity
- Track info text: `#FFF5E0` / `#FFF5E080`
- Background tint overlay: Warm amber `#3D200008` (very subtle)

#### Theme 3: Arctic (Cool Silver)
- Background: `#00080D`
- Tonearm: Polished silver (`#3A3F45` body, `#B0BEC5` highlight)
- Vinyl body: Dark blue-black (`#04080C`)
- Vinyl grooves: `#FFFFFF0A` to `#FFFFFF1C`
- Label ring: Ice blue `#4ECDE4` at 60% opacity
- Track info text: `#EBF5FB` / `#EBF5FB80`

---

## 3. Typography

### 3.1 Font Selection

| Role | Font | Weight | Size |
|------|------|--------|------|
| Track title | `DM Serif Display` | Regular (400) | 22sp |
| Artist name | `DM Sans` | Medium (500) | 15sp |
| Album name | `DM Sans` | Regular (400) | 13sp |
| Time elapsed / duration | `JetBrains Mono` | Regular (400) | 12sp |
| Settings labels | `DM Sans` | Regular (400) | 14sp |
| Buttons / CTAs | `DM Sans` | Medium (500) | 14sp |

**Rationale:**
- `DM Serif Display` for the track title: editorial, elegant, music-editorial feel. Serif display type on black feels expensive.
- `DM Sans` for everything else: clean, geometric, pairs with the serif without competing.
- `JetBrains Mono` for time: monospaced timestamps prevent layout shift as time ticks; technical/precise feel fits the audiophile aesthetic.

### 3.2 Typography Rules

- Track title: single line with horizontal marquee scroll when overflow; scroll starts after 1.5s idle
- Artist and album: two lines max, then ellipsis
- No all-caps anywhere — the app feels premium, not loud
- Letter spacing: track title `−0.02em` (slightly tight, editorial); body `0.01em` (slightly open for legibility on dark)
- Line height: 1.3× for display text, 1.5× for body

---

## 4. Layout System

### 4.1 Main Screen Layout (Portrait)

```
┌─────────────────────────────────────────┐
│                                         │  ← Status bar (transparent, white icons)
│                                         │
│                                         │  ← ~15% top breathing room
│                                         │
│         ┌───────────────────┐           │
│         │                   │           │
│         │   [VINYL DISC]    │           │  ← Vinyl disc: 80vw diameter, centered
│         │      + art        │           │     Positioned at ~30% from top
│         │                   │           │
│         └───────────────────┘           │
│                        \                │
│                         \[TONEARM]      │  ← Tonearm overlaps disc right edge
│                                         │
│          Track Title                    │  ← Below disc, ~24dp gap
│          Artist Name                    │
│          Album Name                     │
│                                         │
│       ━━━━━━━━━━━━━━━━━━━━━━           │  ← Seek bar
│       0:00                    3:24      │
│                                         │
│    [⏮]      [⏸/▶]      [⏭]          │  ← Playback controls
│                                         │
│                                         │  ← Nav gesture area
└─────────────────────────────────────────┘
```

### 4.2 Spacing Scale

| Token | Value |
|-------|-------|
| `space-xs` | 4dp |
| `space-sm` | 8dp |
| `space-md` | 16dp |
| `space-lg` | 24dp |
| `space-xl` | 32dp |
| `space-2xl` | 48dp |

### 4.3 Vinyl Disc Sizing

- Disc diameter: `min(screenWidth × 0.82, 360dp)` — scales with screen but caps on large phones
- On tablets (≥ 8"): disc capped at `400dp`; layout shifts to two-column (disc left, info right)
- Center of disc: horizontally centered, vertically at `screenHeight × 0.38`

### 4.4 Tonearm Positioning

- Tonearm pivot point: `(discCenter.x + discRadius × 0.95, discCenter.y − discRadius × 0.85)`
- Tonearm length: `discRadius × 1.1`
- Tonearm width: `6dp` at body, `3dp` at needle end
- Tonearm head (cartridge): `14dp × 10dp` rounded rectangle

---

## 5. Component Specifications

### 5.1 Vinyl Disc

**Layers (bottom to top):**
1. **Vinyl body** — `Canvas.drawCircle()` with radial shading from `#0A0A12` center to `#080810` edge
2. **Groove rings** — 18–22 concentric `drawArc` rings; inner rings slightly darker, outer rings slightly lighter; alpha range `0x08–0x18`
3. **Label area** — circle at 40% of disc radius; clipped bitmap of album art drawn here
4. **Spindle** — 8dp circle, chrome gradient effect via multiple concentric fills
5. **Specular highlight** — Single `drawArc` covering top-right quadrant at ~8% opacity white; gives the sense of a glossy physical disc

**Groove ring density:** Distribute rings logarithmically (denser toward outer edge, mimicking real vinyl groove spacing). Use: `radius_n = outerGrooveRadius × (n / totalRings)^0.7`

### 5.2 Tonearm

Drawn in Canvas with a `Path`:
- Arm body: rounded `drawRoundRect`, 6dp wide
- Counterweight (back): small circle at pivot end, 10dp radius
- Cartridge: small rounded rect at needle end, tilted `~15°`
- Shadow: draw arm slightly offset (2dp, 2dp) at 20% opacity before drawing the arm
- Rotation applied via `canvas.rotate(tonearmAngle, pivotX, pivotY)`

### 5.3 Dynamic Background

**Algorithm:**
1. Extract album art bitmap
2. Scale down to `80 × 80px`
3. Apply Gaussian blur: `radius = 40` (Android 12+ via `RenderEffect`; older via `Renderscript` fallback or manual stack blur)
4. Scale back up to fill screen
5. Apply dark scrim: `Color(0, 0, 0, alpha = 0.62f)` drawn on top
6. Additional: extract dominant color via Palette, tint the scrim slightly with it at `~10% opacity`

**Result:** A dark, moody, slightly-colored haze that echoes the album's palette without distracting.

### 5.4 Seek Bar

Custom Compose component, not Material Slider:
- Track height: `3dp` rounded, color `#FFFFFF20`
- Fill height: `3dp`, color = dynamic accent at `80% opacity`
- Thumb: `12dp` circle, white, visible only on drag
- No thumb when not interacting (cleaner look)
- Touch target: `48dp` tall regardless of visual thickness
- Haptic feedback on seek start (tick)

### 5.5 Playback Controls

| Button | Icon | Size | State |
|--------|------|------|-------|
| Previous | Tabler `player-skip-back` | 28dp icon, 56dp touch | Always visible |
| Play/Pause | Tabler `player-play` / `player-pause` | 40dp icon, 72dp touch | Animated morph |
| Next | Tabler `player-skip-forward` | 28dp icon, 56dp touch | Always visible |

- Control buttons use no background/container — icons float on the dark background
- Pressed state: scale `0.88` with `spring(stiffness=400)` — crisp haptic-like response
- Play/Pause morph: `AnimatedContent` with a simple `crossfade(200ms)` between icons (full morphing SVG out of scope for v1.0)

### 5.6 Track Info

```
[Track Title]     ← DM Serif Display 22sp, white, marquee if overflow
[Artist]          ← DM Sans Medium 15sp, white 80%
[Album]           ← DM Sans 13sp, white 50%
```

All three items animate in with a `slideInVertically` + `fadeIn` when track changes (staggered: title first, then artist 50ms later, then album 100ms later).

### 5.7 Equalizer Badge

Small animated badge shown next to track title when playing:
- 4 vertical bars, each 3dp wide, 1dp gap
- Each bar animates height independently (2–16dp range)
- Animation: infinite `repeatable` with random phase offset per bar
- Color: dynamic accent at 90% opacity
- Disappears on pause (replaced by pause icon badge)

---

## 6. Animation Specifications

### 6.1 Vinyl Spin-Up / Spin-Down

| Phase | Duration | Easing |
|-------|----------|--------|
| Spin-up (play) | 1200ms | `FastOutSlowInEasing` |
| Constant rotation | continuous | Linear |
| Spin-down (pause) | 800ms | `FastOutLinearInEasing` (fast start, slow end) |

### 6.2 Tonearm Movement

| Trigger | Duration | Easing |
|---------|----------|--------|
| Idle → Playing start | 1400ms | Spring (dampingRatio=0.7, stiffness=100) |
| Playing → Idle (pause) | 1000ms | Spring (dampingRatio=0.6, stiffness=80) |
| Progress update (per 500ms) | 500ms | `LinearEasing` |
| Track skip (quick reset) | 200ms | `FastOutSlowInEasing` then re-engage |

### 6.3 Track Transition

| Element | Animation | Duration |
|---------|-----------|----------|
| Background | Crossfade | 500ms |
| Album art (vinyl label) | Crossfade | 300ms |
| Track title / artist | SlideUp + FadeIn | 250ms |
| Tonearm | Snap to start, then swing in | 200ms + 1400ms |
| Vinyl rotation | Continuous (no pause) | — |

### 6.4 Screen Entry

App launch into active Now Playing state:
1. Background fades in (400ms)
2. Vinyl disc scales from 0.85→1.0 with fade (600ms)
3. Tonearm swings into position (1400ms, spring)
4. Track info slides up (300ms, 200ms delay)
5. Controls fade in (200ms, 400ms delay)

---

## 7. Idle State Design

When no music is playing, show:
- A single, ghostly vinyl disc at 50% opacity, rotating very slowly (~5 RPM)
- Tonearm resting in idle position
- Background: pure black
- Center text: `DM Sans Regular 14sp`, `#FFFFFF50`
  - Line 1: "Open any music app"
  - Line 2: "and start playing"
- A subtle pulsing glow animation on the vinyl center (very low amplitude, 3s period)

---

## 8. Micro-interactions

| Interaction | Response |
|-------------|----------|
| Tap anywhere (controls hidden) | Controls fade in, auto-hide timer resets |
| Long press on vinyl disc | Opens vinyl customization bottom sheet |
| Swipe up from bottom of disc | Opens mini track queue (v1.1) |
| Double tap disc | Toggles play/pause (haptic feedback) |
| Swipe left on track info | Skip to next track |
| Swipe right on track info | Previous track |

---

## 9. Accessibility

- All interactive elements meet 48dp × 48dp minimum touch target
- Content descriptions on all icon buttons (`contentDescription` in Compose)
- Track info readable by TalkBack in correct order: title → artist → album → elapsed/duration
- Reduce Motion: when system accessibility setting "Remove Animations" is on, disable vinyl rotation and tonearm animation; show static disc with a ▶ badge
- Color contrast: all text on background meets WCAG AA (4.5:1 for body, 3:1 for large text)
- Dynamic accent color checked for minimum contrast before applying to text

---

## 10. Onboarding / Permission Screen

Single screen, shown only on first launch:

**Design:**
- Dark background with a faint vinyl illustration (low-opacity, decorative)
- Vinyl logo / wordmark centered at top third
- Headline: `DM Serif Display 28sp` — "Your music, visualized."
- Body: `DM Sans 15sp` — Brief explanation of what Vinyl does and why Notification Listener access is needed (2–3 sentences, non-technical language)
- CTA button: "Enable Access" — full-width, rounded, dynamic accent color fill
- Privacy note below: `DM Sans 12sp, #FFFFFF50` — "Vinyl reads what's playing. Nothing else."
- Link to privacy policy

**Permission flow:**
- Tapping "Enable Access" deep-links to `Settings > Notification Access`
- On return to app, check if permission was granted
- If granted: animate transition to idle screen
- If denied: show a softer "Try Again" state with explanation

---

## 11. Widget Designs

### Small Widget (2×2)
```
┌──────────────┐
│ ○ [art]  ⏸  │
│  Track Title │
│  Artist      │
└──────────────┘
```
- Album art as circular crop, top-left
- Track title truncated to 1 line
- Play/Pause button top-right
- Background: blurred album art + 70% black scrim

### Large Widget (4×2)
```
┌──────────────────────────────────┐
│ ○      Track Title          ⏮⏸⏭│
│ [art]  Artist Name               │
│        Album                     │
│        ━━━━━━━━━━━━━━━━━        │
└──────────────────────────────────┘
```
- Album art circular crop, left side
- Track metadata centered
- Controls right-aligned
- Seek bar at bottom (display-only in widget, not interactive)
