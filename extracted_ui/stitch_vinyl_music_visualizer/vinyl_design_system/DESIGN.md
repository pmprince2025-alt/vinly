---
name: Vinyl Design System
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1b1b1b'
  surface-container: '#1f1f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353535'
  on-surface: '#e2e2e2'
  on-surface-variant: '#d6c4ac'
  inverse-surface: '#e2e2e2'
  inverse-on-surface: '#303030'
  outline: '#9e8e78'
  outline-variant: '#514532'
  surface-tint: '#ffba38'
  primary: '#ffd79b'
  on-primary: '#432c00'
  primary-container: '#ffb300'
  on-primary-container: '#6b4900'
  inverse-primary: '#7e5700'
  secondary: '#c9c6c5'
  on-secondary: '#313030'
  secondary-container: '#4a4949'
  on-secondary-container: '#bab8b7'
  tertiary: '#dfdddc'
  on-tertiary: '#313030'
  tertiary-container: '#c3c1c0'
  on-tertiary-container: '#504f4f'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdeac'
  primary-fixed-dim: '#ffba38'
  on-primary-fixed: '#281900'
  on-primary-fixed-variant: '#604100'
  secondary-fixed: '#e5e2e1'
  secondary-fixed-dim: '#c9c6c5'
  on-secondary-fixed: '#1c1b1b'
  on-secondary-fixed-variant: '#474646'
  tertiary-fixed: '#e5e2e1'
  tertiary-fixed-dim: '#c8c6c5'
  on-tertiary-fixed: '#1c1b1b'
  on-tertiary-fixed-variant: '#474746'
  background: '#131313'
  on-background: '#e2e2e2'
  surface-variant: '#353535'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md-mobile:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  metadata:
    fontFamily: Geist
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  gutter: 24px
  margin-mobile: 20px
  margin-desktop: 64px
  section-gap: 48px
---

## Brand & Style
The design system is engineered for a premium, high-fidelity music visualization experience. It targets audiophiles and enthusiasts who value both technical precision and aesthetic elegance. The personality is "Quiet Luxury"—it does not scream for attention but commands it through exquisite typography and perfect spacing.

The style is **Minimalist Editorial**. It leverages the infinite contrast ratio of OLED displays by using a true black base, allowing content and subtle depth cues to emerge from the darkness. It avoids trendy glassmorphism in favor of solid, architectural layering and high-contrast typography, creating a digital environment that feels like a luxury physical product.

## Colors
This design system utilizes an "OLED-first" palette. The foundation is absolute zero (#000000), which eliminates light bleed on modern displays and conserves energy. 

- **Foundation:** True Black (#000000) for the main canvas.
- **Surface (Sheet):** A deep charcoal (#0D0D0D) for modal sheets and persistent sidebars.
- **Surface (Elevated):** A slightly lighter gray (#1A1A1A) for cards, popovers, and floating elements.
- **Accent:** Deep Amber (#FFB300) serves as the singular point of focus for interactive states, playback progress, and brand-critical moments.
- **Typography:** Pure white at varying opacities ensures legibility while maintaining a soft, sophisticated hierarchy that doesn't strain the eyes in dark environments.

## Typography
The typographic system is a study in contrast between the classical and the technical.

- **Headlines & Track Titles:** Use **Playfair Display**. This serif typeface adds an editorial, high-end feel reminiscent of premium vinyl jacket art.
- **Interface & Body:** Use **Inter**. A clean, geometric sans-serif that ensures maximum legibility for settings, descriptions, and lists.
- **Technical Metadata:** Use **Geist**. This monospaced font is used for timestamps, bitrates, and technical data, reinforcing the precision of the music visualizer.

Maintain generous line heights to avoid a cramped "digital" feel. Display titles should use slight negative letter spacing to feel more cohesive.

## Layout & Spacing
The layout follows a strict 8px grid system to maintain mathematical harmony.

- **Mobile:** A 4-column fluid grid with 20px side margins and 16px gutters.
- **Desktop:** A 12-column fixed grid (max-width 1440px) with 64px side margins and 24px gutters.

The spacing philosophy emphasizes "negative space as luxury." Avoid filling every corner of the screen. Group related elements tightly (8-16px) but leave large gaps (48px+) between major sections to let the OLED black "breathe."

## Elevation & Depth
In a true black environment, traditional drop shadows are invisible. This design system uses **Tonal Layering** and **Subtle Radial Depth**.

- **Level 0:** Base (#000000) - The infinite background.
- **Level 1:** Sheets (#0D0D0D) - Used for persistent containers. Defined by a 1px solid border of #1A1A1A to create separation.
- **Level 2:** Elevated (#1A1A1A) - Used for active cards or floating controls. 
- **Depth Cue:** On Level 2 surfaces, a very subtle, large-radius radial gradient (Center: #222222 at 5% opacity, Edge: Transparent) can be used to simulate a faint light source hitting the center of the element.

## Shapes
The shape language is sophisticated and deliberate.

- **Large Containers/Sheets:** 16px corner radius (`rounded-xl`) creates a modern, architectural feel for major UI blocks.
- **Buttons & Interactive Elements:** 8px corner radius (`rounded-md`) balances the softness of the sheets with a more functional, tactile precision.
- **Album Art:** Should maintain a slight 4px radius to avoid harsh digital clipping while appearing mostly square.

## Components
Consistent component styling ensures the audiophile aesthetic remains cohesive.

- **Buttons:** Primary buttons use a solid Deep Amber (#FFB300) with Black (#000000) text. Secondary buttons use a 1px white outline (30% opacity) with white text.
- **Icons:** Use thin-stroke (1.5px) minimalist outline icons. Icons should be sized at 24px within a 32px touch target.
- **Input Fields:** Bottom-border only or very subtle #1A1A1A background. Focus state is a 1px Deep Amber bottom border.
- **Playback Scrubber:** A 2px thin line. The "played" portion is Deep Amber; the "unplayed" portion is #1A1A1A. The handle is a small 8px solid white circle.
- **Cards:** No shadows. Use #0D0D0D background with a 1px #1A1A1A border. 
- **Lists:** Clean rows with 1px #0D0D0D dividers. Metadata should be right-aligned in Geist Mono.