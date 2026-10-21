# CineFlix UI Report

Result of applying `AGENTS.md` (Stage 2) after the `CINEFLIX_THEME_OVERHAUL.md`
token refactor (Stage 1). Token changes are documented separately in
`THEME_CHANGELOG.md`; this file covers screens and components.

**Scope respected:** restyle only. `UI_INVENTORY.md` (written before Stage 2)
was used as the checklist — every screen, tab, button, element, state and
string still exists; no navigation, data, ViewModel or business-logic change.

---

## App shell — `CineflixApp.kt`

- **Before:** `NavigationSuiteScaffold`-era chrome, default M3 background,
  bar hidden on player/onboarding per route.
- **After:** edge-to-edge `NavDisplay` over an explicit
  `colorScheme.background` fill; content scrolls **behind** the glass bar with
  bottom padding equal to bar height + insets (`LocalBottomBarInset`,
  `BottomBarInsets.kt`) so nothing is hidden; tab switches crossfade at
  `crossfadeMs`; bar hidden exactly where it was before (player overlay,
  splash, onboarding).
- **Files:** `CineflixApp.kt`, `core/navigation/BottomBarInsets.kt`.

## Bottom navigation — `core/components/BottomBar.kt` (+`GlassBar.kt`)

- **Before:** filled/outline M3 nav with indicator behavior of the scaffold.
- **After:** iOS-style full-width bar, icon above label, `SpaceEvenly`,
  56dp content + `WindowInsets.navigationBars`; Haze blur 24–30dp with
  `glassSurface` tint (~70%) and 0.5dp top hairline on **API 31+**, intentional
  solid fallback (~94% `surfaceContainer` / `#111113`) below; same four
  destinations, order and labels; selection signalled by tint **+ icon size +
  label weight** (never colour alone); press scale to `navPressScale` (0.92)
  with `pressSpec` spring + light haptic, no ripple; 48dp targets per tab;
  re-tap keeps existing behaviour.
- **Files:** `BottomBar.kt`, `GlassBar.kt`.

## Top bar — `core/components/TopBar.kt`

- **Restored original scroll-gated design (user request, 2026-10-21):** over
  the hero the bar is a bare row — transparent, white wordmark, search icon on
  its dark glass pill floating on the artwork; once `isScrolledPastHero`
  (`scroll > carouselHeight`) the glass material fades in (same Haze material
  as the nav, hairline on the bottom edge) and the wordmark switches to theme
  ink. The row always reserves the same height, so no layout jump. The
  skeleton mirrors the over-hero state (`isScrolled = false`).

## Home — `feature/home/*`

- **Before:** colored hero chips/gradient chrome, fixed 140dp cards, ad-hoc
  spacing, decorative grain layer per card (`GlassMorphCard`).
- **After:** full-bleed hero with bottom scrim, headline title, rating + genre
  chips in `onImageChipSurface`/`secondaryContainer`;
- **Hero backdrop restored (user request, 2026-10-21):** the section background
  is again **driven by the focused movie** — `PaletteHelper` extracts the
  poster's vibrant / dark-muted swatches on each `currentItem` change and they
  crossfade (`longFadeMs` via motion tokens, instant under reduce motion) into
  the vertical gradient (top/mid/muted stops → screen background, alpha stops
  from `EffectSpec.heroTopAlpha/MidAlpha/MutedAlpha/posterFadeAlpha`, the
  `heroTopAlpha` token restored at its old 0.85f value). Gradient only — the
  old rim/specular/grain layers were not restored (user choice). action row restyled to
  solid primary Play + monochrome icon discs (heart stays red); sections with
  `titleLarge` headers, 8dp posters, `titleMedium` titles, `bodySmall` gray
  metadata; rows fade up once via `enterFadeUp`; hero grain drawn once, cached;
  `ForceLightSystemBarIcons` over the hero; skeleton mirrors production bounds
  from the same tokens (top bar, hero, chips, action row, two rows).
- **Files:** `HomeScreen.kt`, `HomeScreenLoadingSkeleton.kt`,
  `components/HeroSection.kt`, `components/Media Section.kt`.

## Discover — `feature/discover/DiscoverScreen.kt`

- Screen title in `headlineLarge`; search field 16dp/pill
  `surfaceContainer` fill, no border, gray hint, clear button kept;
  category/sort/genre chips pill + 48dp hit area (visual height via padding);
  results grid `GridCells.Adaptive(gridCellMin)` (tablet gets more columns);
  loading/error/empty states unchanged in content, restyled via `StateViews`;
  entrance fade-up on grid items.

## Favourites / My Library — `feature/favorites/FavoritesScreen.kt`

- Three rows kept (Favourites, Saved for Later, Liked Episodes) with existing
  subtitles; poster cards on `MediaCard`, stills 16:9 kept; heart/badge discs
  monochrome (heart red); empty/error states use `StateViews` action kept
  (`Explore Titles` / `Try Again`).

## Profile — `feature/profile/ProfileScreen.kt`

- Placeholder kept verbatim (`This is Profile`), restyled to theme text roles.

## Details sheet — `feature/details/*`

- Large hero backdrop with gradient into `background`, close button kept;
  genre chips pill; headline title; metadata row `bodySmall` gray; **one**
  tidy action row (Play solid, info/favourite/save/share as disc buttons —
  visibility rules unchanged); `ExpandableText` overview on `bodyMedium` with a
  48dp `labelLarge` Read More / Show Less toggle; TV episodes with hairline dividers and
  kept like-heart; season selector chips pill; `More Like This` row kept;
  `ForceLightSystemBarIcons`; shimmer skeleton matches final bounds;
  poster-derived tint applied **only** to the hero gradient.
- **Files:** `DetailsSheet.kt`, `DetailsHeroSection.kt`, `SeasonSelector.kt`,
  `DetailShimmerLayout.kt`.

## Player — `feature/player/*`

- Full-screen black (`playerBackground` both modes), controls restyled to
  monochrome tokens: transport, scrubber, track sheets (Quality/Subtitles/
  Audio/Video Layout) on `surfaceContainerHigh` + 28dp sheet shape; loading
  layer keeps backdrop, `Playing`, title, wavy bars, buffered %; all states
  (loading/error/web fallback) kept; `ForceLightSystemBarIcons` while playing;
  added `@OptIn(UnstableApi)` (fixes 9 pre-existing lint errors, no behavior
  change).
- **Files:** `VideoPlayerScreen.kt`, `PlayerControlsLayer.kt`,
  `PlayerLoadingLayer.kt`.

## Splash & onboarding — `feature/splash`, `feature/onboarding`

- Structure and copy untouched (verified against `UI_INVENTORY.md`); wordmarks
  use `SplashWordmarkStyle`/`WordmarkStyle`; monochrome art (sphere, folder,
  GitHub star) on the gray ladder with hairlines; progress dots, pulse rings
  and Ken Burns re-timed to motion tokens and **disabled under reduce motion**;
  one-off artwork dimensions live in `OnboardingArt`, folder corners snapped to
  system shape tokens (8/16/28/pill).
- **Files:** `SplashScreen.kt`, `OnboardingFlow.kt`, `CinematicIntroScreen.kt`,
  `FileAccessScreen.kt`, `GitHubStarScreen.kt`, `OnboardingArt.kt`.

## Shared components

- `MediaCard`: 8dp posters, 2:3 ratio, no borders, press 0.97 spring (no
  ripple), favourite/save discs (red heart), title + metadata under poster.
- `ActionButtons`: solid `primary` Play pill, `secondaryContainer`/disc
  secondaries, 48dp targets, press spring.
- `StateViews`: centered monochrome icon, `titleMedium` + `bodyMedium` gray,
  existing action only.
- `Shimmer`: token-driven sweep (`shimmerMs`), static under reduce motion.
- `Entrance.kt` (`enterFadeUp`): once-per-item 12dp fade-up, ≤30ms stagger,
  8-item cap, saveable (no replay on scroll-back), instant under reduce motion.
- `ComponentPreviews.kt` / `TypePreviews.kt`: light + dark + tablet-width
  previews for shared components and every type slot.

---

## Decisions (judgment calls, least-intrusive option chosen)

1. **Selection signal on the tab bar** uses tint + icon size + label size
   instead of a filled/outlined icon variant pair — the repo ships only one
   icon set per destination (AGENTS §5 allows weight/colour differentiation;
   colour is reinforced with size so it never relies on hue).
2. **Folder artwork radii** (was 18/6dp, off-system) reuse `BottomSheetShape`
   (round top, flat bottom — visually identical once clamped) and
   `ShapeMedium`, instead of adding artwork-only shapes to the theme.
3. **Glass count:** per Phase 6 the material appears on top bar, bottom nav and
   the hero action pill; the decorative `GlassMorphOverlay` per-card layer was
   deleted (no user-facing content) rather than migrated.
4. **`WindowSizeClass` tiers** feed `mediaCardWidth`/`libraryCardW`
   (Compact/Medium/Expanded) with `GridCells.Adaptive` for the discover grid —
   no fixed 140dp/220dp anywhere; previews cover a tablet width.
5. **Status bar icons** forced light on hero/details/player via
   `ForceLightSystemBarIcons` (restores previous values on dispose), because
   those screens are always dark full-bleed.
6. **Chromatic exceptions** limited to error/success/warning containers and the
   red favourite heart; rating stars and saved state are white over imagery.
7. **User-directed revert (2026-10-21, overrides 1–6 where they conflict):**
   the hero backdrop takes the focused poster's palette colours again (the
   original signature design) and the top bar is scroll-gated (invisible as a
   bar over the hero, glass past it). This supersedes the monochrome-chrome
   rule for the hero backdrop only and AGENTS.md's "hiding on scroll is not
   added" note — both were explicit user instructions to restore the original
   design. Chrome (chips, buttons, text, bars) stays monochrome; only the hero
   gradient carries the poster tint, matching the original implementation.

## Deliberately left

- `ProfileScreen` remains the placeholder it was (restyle only — no new UI).
- WebView player fallback unchanged (no restyle needed; no new chrome).
- Existing icon drawables reused as-is (single variant per destination).

## Open questions (for a human)

1. **Shared-element poster→details hero** (Phase 6.4 / AGENTS §9): details open
   in a `ModalBottomSheet`, which renders in a separate dialog window — shared
   elements cannot cross it. Implementing it requires replacing the modal sheet
   with an in-layout overlay (behavior change), which scope rules forbid.
   Currently: sheet keeps the spec’d spring settle, poster gets the press
   spring. Confirm or schedule a follow-up.
2. **Real-device checks** pending: 60fps scroll under the Haze bar on a
   mid-range phone; 130% font scale and TalkBack order on device; light/dark
   AA over one bright and one dark poster (previews cover both themes).
3. Hiding the bottom bar **on scroll** was not added (would be a new behavior).

## Verification

- `./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest` — pass
  (0 lint errors; unit tests green incl. `MonochromePaletteTest`).
- All verification greps from `AGENTS.md` and `CINEFLIX_THEME_OVERHAUL.md` —
  zero findings outside `ui/theme/`.
- Every commit in this effort was build+lint verified on its own tree.
- `UI_INVENTORY.md` cross-checked against the final app: destinations, order,
  labels, states and copy strings unchanged; `GlassMorphOverlay` recorded as a
  deleted decorative-only element (no content lost).
- Monochrome: only black/white/grays + error/success/warning/heart appear
  (enforced at test level by `MonochromePaletteTest`).
