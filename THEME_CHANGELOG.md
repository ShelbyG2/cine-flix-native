# CineFlix Theme Changelog

Every token **added**, **renamed**, **removed** and every file touched by the
`CINEFLIX_THEME_OVERHAUL.md` refactor (Stage 1) and the resulting composable
migration (Stage 2). Commits, in order:

| Commit | Message |
|---|---|
| `ab81b4a` | theme: phase 1 - bug fixes before the redesign |
| `980f59d` | theme: phase 2 - monochrome colour system |
| `3b1d393` | theme: phase 3 - bundled Inter and an Apple-style type scale |
| `5043a2a` | theme: phase 4 - collapse shapes to 8/16/28 and pill |
| `1e4bd50` | theme: phase 5 - one spacing grid and adaptive sizes |
| `e8fde15` | theme: phase 6 - one glass material, motion tokens and restraint |
| `202c47e` | ui: player - restyle controls and loading layer, silence UnsafeApi lint |
| `1bd2662` | ui: motion - one-shot list entrance token and fade-up helper |
| `01d5048` | ui: components - monochrome media cards, action buttons and state views |
| `a273a75` | ui: browse screens - enter-fade rows and monochrome restyle |
| `f8d372e` | ui: details - poster-tinted hero, tidy action row, hairline episodes |
| `5a55a99` | ui: onboarding - monochrome artwork on system radii |
| `b393869` | ui: app shell - background fill behind edge-to-edge glass nav |

---

## Phase 1 — Bug fixes (`ab81b4a`)

| # | Bug | Fix |
|---|-----|-----|
| 1 | Unset M3 surface roles (lavender tint risk) | `surfaceBright`, `surfaceDim`, `surfaceContainerLowest…Highest` set in **both** `lightColorScheme` / `darkColorScheme` on a pure neutral ladder (light `#FFFFFF → #E6E8E3`, dark `#131313 → #2B2B2B`) |
| 2 | Hand-computed display tracking `(-49f * 0.05f / 100f).sp` | replaced with `(-0.02).em` on display styles |
| 3 | `SplashWordmarkStyle` used `FontWeight.Black`, which the family did not ship | now **Bold**, which bundled Inter declares |
| 4 | `ColorTeal300 = #F6F8F8` off-scale dark primary | token deleted in Phase 2; no usage remained after Phase 1 |
| 5 | Skeletons mismatched production sizes (`skeletonPosterW/H` 120×180 vs 140×210 card) | independent skeleton poster/hero/chip/title dimensions deleted; skeleton sizes derive from `mediaCardWidth`, `mediaCardPosterRatio`, hero and chip tokens |
| 6 | Status-bar icons dark over full-bleed dark imagery | **added** `ForceLightSystemBarIcons(enabled)` composable in `Theme.kt` (sets + restores via `DisposableEffect`); used on Home hero, Details sheet, Player |
| 7 | Dark `inverseSurface = #844…#848484` (readable ink on gray) | now near-white `#E9EAEC` with `inverseOnSurface = #131313` |
| 8 | `rememberReduceMotion()` re-read every recomposition | wrapped in `remember(context)` |

**Files:** `ui/theme/Theme.kt`, `ui/theme/Type.kt`, `ui/theme/Color.kt`,
`ui/theme/Sizes.kt`, `feature/home/HomeScreenLoadingSkeleton.kt`,
`feature/details/components/DetailShimmerLayout.kt`, app resources touched by
the skeleton merge.

## Phase 2 — Monochrome colour system (`980f59d`)

**Removed (deleted outright):**
- The entire Tailwind ramp: `ColorRed*`, `ColorOrange*`, `ColorGreen*`, `ColorTeal*`,
  `ColorBlue*`, `ColorIndigo*`, `ColorPurple*`, `ColorPink*` (100–900).
- Legacy brand tokens: `ColorNavbar`, `ColorCover`, `ColorPrimaryBrand`,
  `ColorLightBackground`, `ColorSubText`, `ColorStroke`.
- `ColorTeal300` (Phase 1 bug 4).

**Added:**
- Monochrome reference palette in `Color.kt` — light: `#FFFFFF / #F2F2F7 / #E5E5EA /
  #0B0B0C / #6C6C70 / #8E8E93`; dark: `#000000 / #1C1C1E / #2C2C2E / #48484A /
  #F2F2F7 / #98989D`, with per-role `md_*` values driving both M3 schemes.
- Grouped extended colours replacing the 60-field flat bag, exposed as
  `AppTheme.colors.<group>`:
  `StatusColors`, `GlassColors`, `ImageOverlayColors`, `AccentColors`,
  `NavColors`, `SkeletonColors`, plus `playerBackground` (Black in both modes).
- `EffectSpec` + `LocalAppEffects` for non-colour fields (`glassNoiseDensity`,
  alphas, pulse-ring alphas) that used to sit inside the colour bag.
- `MonochromePaletteTest` (`app/src/test/…/ui/theme/MonochromePaletteTest.kt`) —
  parses `Color.kt` and fails the build on any `Color(0x…)` literal whose channel
  spread > 8 unless it is an allowed status/heart name. The build now breaks if
  teal/blue/gold creeps back in.
- WCAG notes in comments next to each text/background pair (all AA: ≥4.5:1 body,
  ≥3:1 large/icons).

**Renamed / moved:**
- Old `AppExtendedColors.*` flat fields → grouped classes; every call site migrated.
- Onboarding-only values (`sphereShade`, `pulseRingAlpha1/2`, `progressDotInactive`)
  → `feature/onboarding/OnboardingArt.kt`.
- Poster clamping helpers `heroTopAlpha/heroMidAlpha/heroMutedAlpha` + `PosterPalette`
  kept, applied **only** to the details hero background gradient.
- `tertiary` re-pointed to a neutral gray (it previously implied a colourful accent).

**Files:** `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/PosterPalette.kt`,
`core/components/{ActionButtons,BottomBar,GlassBar,MediaCard,Shimmer,StateViews,TopBar,ExpandableText}.kt`,
all screens consuming the old flat fields (home, discover, favorites, details,
player, onboarding, splash), `MonochromePaletteTest.kt`.

## Phase 3 — Typography (`3b1d393`)

**Added:**
- Bundled Inter (OFL) as `res/font/inter_{regular,medium,semibold,bold}.ttf`
  and one `AppFontFamily`; `licenses/Inter-OFL.txt` included.
- Type scale (six separated steps mapped to M3 slots): caption 12 Regular,
  body 14 Regular, bodyLarge 16 Regular, title 22 SemiBold, headline 32 Bold,
  display 48 Bold. `bodySmall 12 < bodyMedium 14 < bodyLarge 16` restored;
  no two slots share size+weight.
- Tracking/line-height per spec: display/headline `-0.02em`, titles `-0.01em`,
  body `0em`, labels `+0.01em`; headings 1.1–1.15×, body 1.4–1.5×.
- `TypePreviews.kt` — every slot previewed light + dark.

**Removed:**
- Downloadable fonts: `GoogleFontsProvider`, `googlefonts` imports,
  `com_google_android_gms_fonts_certs` dependency, `font_certs.xml`,
  `preloaded_fonts.xml`, `lexend_thin.xml`, Lexend entirely.
- Escape-hatch styles: `HeadlineStrongStyle`, `TitleStrongStyle`,
  `CaptionStrongStyle`, `LabelStrongStyle` (all usages migrated to an M3 slot
  + `FontWeight`); 10sp `labelSmall` (floor is 11sp, informational 12sp).
- Stale “USER SPEC” / wrong-weight comments.

**Kept (per brief):** `WordmarkStyle` (Bold, `+0.12em`, caps),
`SplashWordmarkStyle` (Bold — bug 3 fix), `BodyLineHeightBoost`.

**Files:** `ui/theme/Type.kt`, `ui/theme/TypePreviews.kt`,
`app/build.gradle.kts`, `gradle/libs.versions.toml`, `app/src/main/res/**`,
`licenses/Inter-OFL.txt`, every composable using deleted styles
(`ExpandableText`, splash, onboarding, section headers…).

## Phase 4 — Shapes (`5043a2a`)

**Survivors:** `ShapeSmall` 8, `ShapeMedium` 16, `ShapeLarge` 28, `ShapePill`,
`ShapeCircle`, `BottomSheetShape` (28 top), `FullScreenShape` (0);
`AppShapes` slots = extraSmall 4 (tooltips only) / small 8 / medium 16 /
large 16 / extraLarge 28. Every named shape equals its same-named M3 slot
(naming collision resolved).

**Removed:** `ShapeButton` (14), `ShapePlay` (22), `ShapeRound` (50 duplicate of
pill), `ShapeAsymmetric`, `ShapeTab`, `ShapeFolderBody`.

**Later moved:** the folder artwork shapes ended up in `OnboardingArt`
(per brief) but with off-scale 18/6dp corners — in `5a55a99` they were snapped
to system tokens: `folderTabShape = BottomSheetShape` (rounded top, flat bottom,
same construction) and `folderBodyShape = ShapeMedium`, so the codebase now uses
only 8/16/28/pill/0 radii.

**Files:** `ui/theme/Shape.kt`, `feature/onboarding/OnboardingArt.kt`,
every composable using a deleted shape (buttons, play disc, onboarding folder,
sheets).

## Phase 5 — Spacing and sizes (`1e4bd50`)

**Added — `AppSpacing` (4-point grid):** `xs 4, sm 8, md 12, lg 16, xl 24,
xxl 32, xxxl 48`, plus `screenPadding 16`, `sectionGap 32`, `itemGap 8`,
`inlineGap 4` and a small set of named component paddings (chip, button, action
row, hero inset — all on-grid).

**Removed:**
- The 17 `gapNN` tokens and off-grid values `gap2/3/5/7/22/26/28`,
  `multiValueHorizontal`, `multiValueVertical` — usages migrated to the nearest
  on-grid token (see “visual regressions” note below).
- Duplicates between Spacing and Sizes (`iconSize`, `iconSizeLg`, `touchTarget`
  now owned by `Sizes` only).
- One-off touch tokens `topBarControl`, `cardAction`, `closeButton`,
  `chipTouchArea`, `likeButton`, `searchBarHeight` — all migrated to
  `touchTarget = 48.dp`.
- Global `mediaCardWidth = 140.dp` / `libraryCardW = 220.dp` constants —
  replaced by `Compact/Medium/Expanded{Media,Library}CardWidth` derived from
  `WindowSizeClass` (dependency added: `material3-window-size-class`) and
  `GridCells.Adaptive(gridCellMin)` in the discover grid.
- All `skeleton*` size tokens that duplicated a production size (Phase 1.5).
- Onboarding/splash artwork dimensions → `OnboardingArt` object
  (`sphere*`, `folder*`, `fileLine*`, `downloadDisc`, `dot*`, ring values…).

**Added later (Stage 2):** `sizes.listRise = 12.dp` for the list entrance.

**Files:** `ui/theme/Spacing.kt`, `ui/theme/Sizes.kt`, `ui/theme/Theme.kt`
(window size class wiring), `feature/onboarding/OnboardingArt.kt`,
`gradle/libs.versions.toml`, `app/build.gradle.kts`, and every composable that
consumed `gapNN` / fixed card widths / touch-target duplicates.

## Phase 6 — Glass, motion, signature (`e8fde15`)

**Glass:**
- `GlassColors` = `surface` (light white ~70%, dark `#1C1C1E` ~70%), `border`
  (0.5dp-equivalent hairline, top edge only), `fallback` (~94% solid:
  `surfaceContainer` light / `#111113` dark).
- `GlassBar.kt`: Haze (`dev.chrisbanes.haze` 1.7.3 — allowed dependency) with
  `haze`/`hazeChild`, blur radius from `sizes.glassBlurRadius` (24–30dp),
  **fallback below API 31**: intentional solid surface + same hairline.
  One blur source per screen; noise/grain rendered once to a cached bitmap,
  never per frame.
- Glass used in exactly the sanctioned places: floating top bar, bottom nav bar,
  hero action pill. `GlassMorphOverlay` (decorative per-frame grain layer in
  every card) deleted — no user-facing content.
- `EffectSpec` owns the grain/noise constants so colour stays colour.

**Motion — `AppMotion`:**
- Durations: `shortMs 150`, `mediumMs 300`, `longMs 500`, `longFadeMs 800`,
  `shimmerMs 1200`, `crossfadeMs 180`, `staggerMs 30`.
- Scales: `pressScale 0.97` (cards/buttons), `navPressScale 0.92` (tabs).
- Easings: `emphasized (0.2, 0, 0, 1)`, `standard`, `decelerate`, `accelerate`.
- `pressSpring = spring(0.7f, 400f)` + derived `pressSpec`.
- `reduceMotion`: durations snap to 0, springs swap to snap/short fade;
  `rememberReduceMotion()` reads the system setting once per composition entry
  (Phase 1.8). 10 composable files honour it (Ken Burns, pulses, stagger,
  shimmer, entrance).
- Every raw `tween(...)`/`spring(...)`/animate spec in composables replaced with
  these tokens (verified by grep).

**Signature transition:** see “Open questions” — a `SharedTransitionLayout`
poster→hero element was **not** applied because details open in a
`ModalBottomSheet` (separate dialog window; shared elements cannot cross it
without replacing the sheet, which would change behavior). Sheet keeps the
spec’d spring settle; tab switches crossfade at `crossfadeMs`.

**Files:** `ui/theme/Motion.kt`, `ui/theme/Color.kt` (Glass/Effect groups),
`core/components/{GlassBar,BottomBar,TopBar,Shimmer,HeroSection}.kt`,
`CineflixApp.kt`, `gradle/libs.versions.toml`, `app/build.gradle.kts`.

---

## Stage 2 commits (consumers, per AGENTS.md)

| Commit | Scope |
|---|---|
| `202c47e` | Player: `VideoPlayerScreen`, `PlayerControlsLayer`, `PlayerLoadingLayer` (+ `@OptIn(UnstableApi)`, fixing 9 pre-existing lint errors) |
| `1bd2662` | `sizes.listRise` + `core/components/Entrance.kt` (`enterFadeUp`) |
| `01d5048` | `MediaCard`, `ActionButtons`, `StateViews`, `ComponentPreviews` |
| `a273a75` | `Media Section`, `DiscoverScreen`, `FavoritesScreen`, `ProfileScreen` |
| `f8d372e` | `DetailsSheet`, `DetailsHeroSection`, `SeasonSelector` |
| `5a55a99` | `CinematicIntroScreen`, `FileAccessScreen`, `GitHubStarScreen`, `OnboardingArt` |
| `b393869` | `CineflixApp` background fill |

Pre-existing from the checkpoint era and kept: `TopBar`, `BottomBar`, `Shimmer`,
`HeroSection`, `HomeScreen(+skeleton)`, `SplashScreen`, `OnboardingFlow`,
`BottomBarInsets`, `ComponentPreviews` previews.

## Verification (final)

- `./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest` — **pass**
  (lint: 0 errors; `MonochromePaletteTest` green).
- All six greps from `CINEFLIX_THEME_OVERHAUL.md` and all three from `AGENTS.md`
  (raw hex, inline type, raw dp size/radius/anim specs, leftover
  GoogleFont/Lexend/teal/blue/gold/Strong styles) — **zero findings** outside
  `ui/theme/`.
- Every commit above was verified to build and lint green on its own tree
  (`assembleDebug + lintDebug` run against each committed state).

## Open questions (for a human)

1. **Shared-element poster→hero** (Phase 6.4): not applicable across the
   `ModalBottomSheet` window without replacing the sheet with an in-layout
   overlay — a behavior change. Decide whether to pursue.
2. **Tablet/foldable card columns** are driven by `WindowSizeClass` tiers +
   `GridCells.Adaptive`; confirm the expanded tier’s card widths feel right on a
   real device (previews only cover a tablet width in composition).
3. **Jank check under the glass bar** needs a mid-range physical device
   (60fps while scrolling beneath Haze); not verifiable in CI here.
4. Off-grid spacing migration moved a handful of values by up to 2dp
   (e.g. old `gap22/26/28` → `xl 24` / `xxl 32`); no visual regression beyond
   that was found in previews.
