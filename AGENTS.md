# CineFlix: Base Agent Instructions (UI Re-imagining)

This file is the standing brief for any coding agent working on the CineFlix Android app
(Kotlin, Jetpack Compose, Material 3). It governs **how** you work on UI. The companion file
`CINEFLIX_THEME_OVERHAUL.md` governs the design-token layer (`ui/theme/*`).

**Order of work:** finish `CINEFLIX_THEME_OVERHAUL.md` first (tokens, fonts, colors), then apply
this brief to the screens and components. If a token you need does not exist, add it to the
theme package; never hard-code it in a composable.

---

## 1. Mission

Re-imagine the **look and feel** of the existing app: colors, typography, spacing, layout
rhythm, animation and the navigation bar. The target is a **black-and-white, Apple-style
premium experience**: calm, spacious, high-contrast, typographic, with iOS-style translucent
glass on the navigation bar.

## 2. Hard scope rules (never break these)

1. **Restyle only. Do not add elements.** No new screens, tabs, buttons, banners, badges,
   onboarding steps, tooltips, settings, icons, or features. No new copy except where an
   existing string must change to fit.
2. **Do not remove functionality.** Every existing interactive element, destination and state
   (loading, empty, error, offline) must still exist and work. Same actions, same navigation graph.
3. **Same information, new presentation.** You may reorder, resize, re-space, re-align and
   restyle existing elements within a screen. You may not drop information a user could
   previously see.
4. **No changes** to networking, data, repositories, ViewModels, navigation routes, analytics
   or business logic. UI layer only (composables, theme, resources).
5. **No new dependencies** except those explicitly allowed here: `material3-window-size-class`
   and **Haze** (`dev.chrisbanes.haze`) for backdrop blur (see section 5). Ask nothing; if a
   decision is needed, choose the option that is the least invasive and record it in the report.
6. **Monochrome only.** No brand hue. The only chromatic colors allowed are error, success,
   warning and the red favourite heart. Poster-derived tint is allowed only on the details hero
   background gradient.
7. If a request here conflicts with accessibility (contrast, 48dp touch targets, reduced
   motion), accessibility wins.

## 3. Working method

1. **Inventory first.** List every screen and reusable component (composable files under
   `ui/`). For each, record: purpose, elements present, states. Save as `UI_INVENTORY.md`.
   Use it as your checklist that nothing is added or lost.
2. **Work screen by screen**, smallest shared components first (chips, cards, buttons, top bar,
   nav bar), then screens. Commit after each component or screen:
   `ui: <screen/component> - <what changed>`.
3. **Build and preview after every commit:** `./gradlew :app:assembleDebug`. Add or update
   `@Preview` for light and dark, plus a tablet width for adaptive screens.
4. **Reuse before creating.** Prefer existing components. Extract a shared composable only
   when three or more places repeat the same structure.
5. **Finish with `UI_REPORT.md`:** per screen, before/after summary, files touched,
   anything deliberately left, and open questions for a human.

## 4. Design principles (decide every detail with these)

- **Content is the hero.** Posters and backdrops carry the color. Chrome (bars, buttons,
  chips, text) stays black, white and gray and gets out of the way.
- **Hierarchy through size and weight, not color.** One clear focal point per screen.
  Titles large and tight, metadata small and gray.
- **Space is premium.** Generous, consistent spacing on the 4-point grid. Prefer fewer, larger
  elements over many small ones. Do not fill empty space.
- **Hairlines over shadows.** Separate surfaces with 0.5dp borders and surface-ladder steps.
  Avoid heavy elevation, drop shadows and gradients other than image scrims.
- **Restraint.** Glass in two places, accent nowhere, one animation per interaction.
- **Consistency.** Same radius, spacing, icon size and motion for the same kind of element
  everywhere. If two similar elements differ, one of them is wrong.

## 5. Navigation bar: iOS-style with glassmorphism

Restyle the existing bottom navigation. **Same destinations, same order, same labels; do not
add or remove tabs.**

**Layout and behavior**
- Full-width bottom tab bar in the iOS style: icon above a short label, evenly distributed,
  height about 56dp content plus system navigation-bar inset (`WindowInsets.navigationBars`).
- Content must extend **behind** the bar (edge-to-edge) so it scrolls beneath the glass.
  Add bottom content padding equal to the bar height so nothing is hidden at the end of a list.
- Selected tab: filled icon variant in `navSelectedIcon` (ink in light, white in dark) with its
  label; unselected: outlined icon in `navUnselectedIcon` (gray). No pill/indicator blob.
  If only one icon variant exists, differentiate by color and weight.
- Labels: `labelSmall`-equivalent (11 to 12sp), Medium weight, one line, always visible.
- Tap feedback: brief scale to 0.92 with the press spring, then a light haptic
  (`HapticFeedbackType.TextHandleMove` or `LongPress` per what is available). No ripple.
- Re-tapping the selected tab keeps existing behavior (for example scroll to top).
- Respect 48dp minimum touch target per tab.

**Glass material (reuse for the floating top bar so there is one material)**
- Implementation on API 31+: real backdrop blur with **Haze**
  (`Modifier.haze(...)` on the scrolling content and `Modifier.hazeChild(...)` on the bar),
  blur radius about 24 to 30dp, with a tint.
- Tint: light mode white at about 70% (`glassSurface`); dark mode `#1C1C1E` at about 70%.
  Add a subtle saturation boost only if Haze supports it without cost.
- Top edge hairline: 0.5dp, `glassBorder`. No side or bottom border.
- **Fallback below API 31 (no blur):** solid surface at about 94% opacity
  (`surfaceContainer` light / `#111113` dark) with the same hairline. It must look intentional,
  not broken.
- **Performance:** one blur source per screen, do not nest blurs, never blur inside a
  `LazyColumn` item. Do not redraw grain per frame; if noise texture is used, cache a single
  bitmap. Verify no jank on a mid-range device (target 60fps while scrolling under the bar).
- Hide the bar on full-screen player and onboarding exactly where it is hidden today.
- Hiding on scroll is **not** added (that would be a new behavior).

## 6. Typography rules (using the token set from the theme brief)

- Use only `MaterialTheme.typography.*` and the few named styles that remain. **No** inline
  `fontSize`, `fontWeight`, `letterSpacing` or `fontFamily` in composables.
- Screen titles: `headlineLarge`/`headlineMedium`. Section headers: `titleLarge`.
  Card titles: `titleMedium`. Body: `bodyLarge`/`bodyMedium`. Metadata: `bodySmall`.
  Chips and buttons: `labelLarge`.
- Max two weights visible per screen section. Titles Bold/SemiBold, everything else Regular/Medium.
- Metadata uses `onSurfaceVariant` (gray); titles use `onSurface`/`onBackground`. Never gray
  text on imagery; use `onImage` / `onImageMuted` with a scrim.
- Long text: line length 45 to 75 characters on tablets; use `maxLines` + ellipsis on cards.
- Respect system font scale up to 1.3x without clipping; no fixed-height text containers.

## 7. Color usage rules

- Only `MaterialTheme.colorScheme.*` and `AppTheme.colors.*`. No raw hex in composables.
- Backgrounds: `background` for screens, `surfaceContainer*` ladder for cards, sheets,
  dialogs. Do not use more than three surface levels on one screen.
- Primary buttons: solid `primary`/`onPrimary` (black in light, white in dark). Secondary:
  `secondaryContainer`. Tertiary/text buttons: `onSurface`, no fill.
- Dividers and card borders: `outlineVariant` at hairline width.
- Over imagery: scrims from `ImageOverlayColors` so white text passes AA (4.5:1) on any
  poster. Test against one very bright and one very dark poster.
- Contrast in both themes: body text 4.5:1, large text and icons 3:1.

## 8. Layout rules

- Spacing: `AppTheme.spacing.*` only, on the 4-point grid. Screen horizontal padding 16dp,
  gap between sections 32dp, between items 8 to 12dp.
- Radii: 8 / 16 / 28 / pill only. Posters 8dp; cards 16dp; sheets 28dp top; chips and
  action pills fully rounded.
- Posters keep the 2:3 ratio. Grids use adaptive cells (`GridCells.Adaptive`) so tablets and
  foldables get more columns rather than stretched cards.
- Use `WindowSizeClass` for compact, medium and expanded layouts; the expanded layout may widen
  and add columns but must not introduce new elements.
- Edge-to-edge everywhere; handle insets (`statusBars`, `navigationBars`, `displayCutout`).
- Skeleton loaders must match final layout dimensions exactly (derive from the same tokens).

## 9. Motion rules

All motion comes from `AppTheme.motion` tokens (durations, easing, springs). No raw `tween(...)`
or `spring(...)` in composables.

- **Feel:** fast, springy, physical. Nothing longer than 500ms except hero crossfades.
- **Press feedback:** scale to 0.96 to 0.98 with the press spring on cards and buttons. No ripples
  on cards; keep a standard ripple only where accessibility services need it.
- **Navigation transitions:** tab switches crossfade (150 to 200ms). Push transitions slide
  about 30% of width with fade, emphasized easing. Poster to details uses a shared-element
  transition (`SharedTransitionLayout`).
- **Lists:** items fade up (translateY 12dp to 0, alpha 0 to 1) once on first appearance, with
  at most a 30ms stagger and a cap of 8 items. Do not re-animate on recomposition or scroll back.
- **Bottom sheets:** spring settle, scrim fades in with the sheet.
- **Skeleton shimmer:** slow, low contrast sweep (see `shimmerMs`).
- **Reduce motion:** when `AppTheme.motion.reduceMotion` is true, replace all movement with a
  short crossfade or no animation; disable Ken Burns, pulses and stagger.
- Never animate layout size on every frame in lists (use `graphicsLayer`/`Modifier.offset`
  lambdas to avoid recomposition).

## 10. Component restyle checklist

Restyle each existing component using the rules above. Do not add any that do not exist today.

- **Top bar:** same glass material as the nav bar, hairline bottom edge, wordmark in `WordmarkStyle`,
  existing controls only.
- **Bottom nav:** section 5.
- **Hero carousel:** full-bleed poster, bottom scrim, large title, existing buttons restyled to
  solid white (dark) / solid black (light) primary and glass secondary; no new controls.
- **Media rows and cards:** posters with 8dp radius, no borders, title and metadata under the
  poster in `titleMedium` and `bodySmall`; favourite and save keep position but use the white/red
  monochrome icons on a subtle dark disc.
- **Chips (genre, rating, filter):** pill shape, `secondaryContainer` or `onImageChipSurface`
  over imagery, `labelLarge`/`CaptionStyle`, 48dp hit area with visual height smaller via padding.
- **Details sheet / screen:** large hero with gradient to `background`, title in headline,
  metadata row in gray, existing actions in a single tidy row, episode list with hairline
  dividers.
- **Search:** same field, 16dp radius or pill, `surfaceContainer` fill, no border, gray hint.
- **Dialogs and sheets:** 28dp radius, `surfaceContainerHigh`, centered or left aligned title,
  clear primary action, spacing on the grid.
- **State views (empty, error, offline):** centered icon (monochrome), `titleMedium` + `bodyMedium`
  gray, existing action only.
- **Onboarding and splash:** keep structure and copy; restyle to monochrome; wordmark in
  `SplashWordmarkStyle`; keep existing animations but re-time them to the motion tokens.
  Move one-off artwork dimensions into an onboarding-local object, not the global theme.

## 11. Accessibility (non-negotiable)

- Touch targets at least 48dp. Provide `contentDescription` for every icon that conveys
  meaning, `null` only for decorative icons.
- Contrast per section 7. Do not rely on color alone for state (selected tab also changes icon
  fill and label weight).
- Support TalkBack order, font scale to 130%, display size large, and dark/light switching
  without restart.
- Respect reduced motion (section 9).

## 12. Verification before you finish

Run and fix all findings:

```bash
./gradlew :app:assembleDebug lint
# raw hex colors, inline type, raw dp radii/sizes, raw animation specs outside the theme
grep -rnE "Color\(0x" app/src/main --include=*.kt | grep -v "/ui/theme/"
grep -rnE "fontSize\s*=|FontWeight\.|letterSpacing\s*=|fontFamily\s*=" app/src/main --include=*.kt | grep -v "/ui/theme/"
grep -rnE "RoundedCornerShape\(|\.size\([0-9.]+\.dp\)|tween\(|spring\(" app/src/main --include=*.kt | grep -v "/ui/theme/"
```

Checklist:

- [ ] `UI_INVENTORY.md` matches the final app: **no element added, none removed**.
- [ ] All navigation destinations, order and labels unchanged.
- [ ] Bottom bar: glass blur on API 31+, intentional solid fallback below, content scrolls
  beneath it, correct insets, selected/unselected states clear in both themes.
- [ ] Only black, white, grays and the allowed status colors appear anywhere in the UI.
- [ ] Both themes reviewed on phone and tablet previews; text passes AA over imagery.
- [ ] Animations use tokens only; reduce-motion verified; no jank scrolling under the glass.
- [ ] Skeletons and loaded content have identical bounds.
- [ ] Build, lint and existing tests pass.
- [ ] `UI_REPORT.md` written.

## 13. When unsure

Choose the more restrained option (less color, less motion, fewer decorations, more space).
Never invent a feature to solve a design problem. Record any judgment call in `UI_REPORT.md`
under "Decisions" and any blocked item under "Open questions".