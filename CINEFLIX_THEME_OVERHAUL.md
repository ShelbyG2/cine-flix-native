# CineFlix UI Theme Overhaul: Agent Instructions

You are refactoring the Jetpack Compose theme package `tech.unrealistic.cineflix.ui.theme`
(`Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`, `Sizes.kt`, `Motion.kt`, `Theme.kt`)
and every composable that consumes it.

Goal: remove real bugs, replace the generic Tailwind/Material-default look with a
**strictly black-and-white, Apple-style premium identity**, and collapse inconsistent token
sets into tight systems. There is **no brand hue**: no teal, no gold, no blue, no purple.

## Operating rules

1. Work in the phases below, in order. Commit after each phase with a message `theme: phase N - <title>`.
2. After every phase the project must compile (`./gradlew :app:assembleDebug`) and `./gradlew lint` must not add new errors. Fix breakage before moving on.
3. Do not ask the user questions. Where a decision is needed, use the **Defaults** in the Decisions section.
4. Never leave raw literals in composables. Hex colors, `fontSize`, `.dp` sizes for icons/controls, radii and tween durations must come from `MaterialTheme.*` or `AppTheme.*`. Search for violations (see Verification).
5. When you rename or delete a token, migrate every usage in the codebase. Do not leave dead tokens "just in case". If a rename is large, add a temporary `@Deprecated` alias in one commit and remove it in the next.
6. Keep the public API shape: `CineflixTheme(...)`, `AppTheme.colors/spacing/sizes/motion`, `MaterialTheme.*`.
7. Do not change screen layout or behavior except where an issue below requires it.
8. At the end, produce `THEME_CHANGELOG.md` listing every token added, renamed, removed, and every file touched.

## Decisions (use these defaults)

- **Direction:** monochrome, Apple-like. Light and dark are both first-class. Light is ink on
  white; dark is true black (`#000000`, OLED) with white ink. Premium comes from contrast,
  whitespace, typography, hairlines and restraint, not from color.
- **Palette:** the reference implementation is the provided `Color.kt`. Use it as the base:
    - Light: white `#FFFFFF`, grouped background `#F2F2F7`, hairline `#E5E5EA`, ink `#0B0B0C`,
      secondary text `#6C6C70`, outline `#8E8E93`.
    - Dark: black `#000000`, cards `#1C1C1E`, raised `#2C2C2E`, hairline `#48484A`,
      text `#F2F2F7`, secondary text `#98989D`.
- **Primary role:** `primary` is ink in light mode and white in dark mode. Primary buttons
  are solid black (light) / solid white (dark). Selected nav icons are ink / white.
- **Allowed chromatic colors (only these):** error red, success green, warning orange, and the
  red favourite heart. Rating stars and saved state are white over imagery. Nothing else.
- **Poster color:** per-title dominant color may tint **only the details hero background
  gradient**. All chrome (buttons, nav, chips, text) stays monochrome.
- **Font:** one family, **Inter** (closest free match to Apple's SF). Premium comes from weight
  contrast (Regular vs SemiBold/Bold) and tight display tracking. If Inter cannot be fetched
  use **DM Sans**. Do not add a second display face.
- **Radii:** 8 / 16 / 28 dp plus pill.
- **Spacing grid:** 4-point. Allowed steps: 4, 8, 12, 16, 24, 32, 48, 64.

## Phase 1: Bug fixes (do these first, they are independent of the redesign)

1. **Unset M3 surface roles.** In both `lightColorScheme` and `darkColorScheme` set `surfaceContainerLowest`, `surfaceContainerLow`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest`, `surfaceBright`, `surfaceDim`. Create a clean neutral ladder (no purple/lavender tint). Light: white to `#E6E8E3`. Dark: `#131313` stepping up to `#2B2B2B`. Check that no M3 component renders with a lavender tint.
2. **Display letter spacing.** In `Type.kt` replace `(-49f * 0.05f / 100f).sp` style expressions with `(-0.02).em` on display styles. Remove the arithmetic.
3. **`FontWeight.Black` not in the family.** Either add a real Black font file to the family (when the new fonts are bundled) or change `SplashWordmarkStyle` to a weight that exists. After Phase 3 the weight used must be one the family actually declares.
4. **`ColorTeal300 = #F6F8F8`** is off-scale and was the dark primary. Stop using it anywhere. If the raw palette still exists after Phase 2, fix or delete this value.
5. **Skeleton/production mismatch.** `mediaCardWidth` is 140dp (poster ratio 1.5, so 140x210) but `skeletonPosterW/H` are 120x180. Delete the independent skeleton poster dimensions and derive the skeleton from `mediaCardWidth` and `mediaCardPosterRatio`. Do the same for any other skeleton size that mirrors a production size (`skeletonHeroTrackH` vs the real hero height, chips, titles). Skeletons and content must be the same size.
6. **Status bar icons.** In `Theme.kt`, `isAppearanceLightStatusBars = !darkTheme` is wrong over full-bleed dark imagery. Add a small API (for example `ForceLightSystemBarIcons(enabled: Boolean)` as a composable with a `DisposableEffect` that sets and restores the previous values). Use it on hero, details and player screens so icons are light regardless of theme.
7. **`inverseSurface` in dark mode** must not be `ColorLightBackground` (`#848484`). Use a near-white (for example `#E9EAEC`) with `inverseOnSurface` `#131313`.
8. **`rememberReduceMotion()`** must be wrapped in `remember(context)`, and should read the setting once per composition entry, not on every recomposition.

## Phase 2: Color system (monochrome)

1. Replace `Color.kt` with the monochrome version provided alongside this brief (or reproduce
   its values exactly). Delete the entire Tailwind ramp (red/orange/green/teal/blue/indigo/
   purple/pink 100-900) and the legacy brand tokens (`ColorNavbar`, `ColorCover`,
   `ColorPrimaryBrand`, `ColorLightBackground`, `ColorSubText`, `ColorStroke`, `ColorTeal*`).
2. Set the full M3 surface ladder (`surfaceBright`, `surfaceDim`, `surfaceContainerLowest` to
   `Highest`) in **both** `lightColorScheme` and `darkColorScheme` in `Theme.kt`, using the
   `md_*_surface*` values from `Color.kt`. All must be pure neutral grays, no lavender tint.
3. **Elevation tint:** because `surfaceTint` is ink/white, M3 tonal elevation can shift surfaces.
   Prefer hairline borders and the surface ladder over shadows/tonal elevation. If any
   component looks tinted, set its `tonalElevation = 0.dp`.
4. Every text/background pair must meet WCAG AA (4.5:1 body, 3:1 large text and UI icons).
   Note ratios in comments; fix any that fail.
5. **Split `AppExtendedColors`** (60+ flat fields) into grouped classes exposed via `AppTheme.colors`:
   `StatusColors`, `GlassColors`, `ImageOverlayColors`, `AccentColors` (rating, favourite, saved),
   `NavColors`, `SkeletonColors`. Non-color fields (`glassNoiseDensity: Int`, alphas,
   `pulseRingAlpha*`) move out to the owning component or an `EffectSpec` class. Onboarding-only
   values (`sphereShade`, `pulseRingAlpha1/2`, `progressDotInactive`) move to an onboarding file.
   Update every call site.
6. **Monochrome enforcement.** Add a unit test (or a Gradle check script) that parses
   `Color.kt` and fails if any `Color(0x..)` literal has channel spread greater than 8 (that is,
   not a neutral gray) unless its name starts with `ColorError`, `ColorSuccess`, `ColorWarning`
   or `ColorHeart`, or it is an `*Container` / `onSuccess` / `onWarning` / `on*Container` status
   variant. The build must fail on any teal/blue/gold creeping back in.
7. Grep all composables for hard-coded teal/blue/purple/gold hexes or old token names and
   replace them with theme roles. Replace any use of `MaterialTheme.colorScheme.tertiary`
   that relied on it being a colorful accent; it is now a neutral gray.
8. **Poster-driven color:** keep the `heroTopAlpha/heroMidAlpha/heroMutedAlpha` tokens and a
   `PosterPalette` helper that clamps for readable text, but apply it only to the details hero
   gradient background (see Decisions).
9. Keep `playerBackground = Black` in both modes.

## Phase 3: Typography (Apple-style, single family)

1. **Bundle fonts, drop downloadable fonts.** Download **Inter** (OFL) as static TTFs or the
   variable font into `app/src/main/res/font/` (lowercase snake_case, for example
   `inter_regular.ttf`, `inter_medium.ttf`, `inter_semibold.ttf`, `inter_bold.ttf`). Define one
   `AppFontFamily` using `Font(R.font.x, FontWeight.Y)`. Remove `GoogleFontsProvider`, the
   `googlefonts` imports and the `com_google_android_gms_fonts_certs` dependency if nothing
   else uses them. Include the OFL license text in `licenses/`. Do not keep Lexend.
2. **Fix the scale.** Six clearly separated steps mapped to M3 slots:

   | Step | Size | Weight | Used for |
         |---|---|---|---|
   | caption | 12sp | Regular | metadata, chips |
   | body | 14sp | Regular | secondary body |
   | bodyLarge | 16sp | Regular | primary body |
   | title | 22sp | SemiBold | section and card titles |
   | headline | 32sp | Bold | screen titles, detail titles |
   | display | 48sp | Bold | hero / splash |

   Rules: `bodyLarge` 16, `bodyMedium` 14, `bodySmall` 12 (do **not** keep
   `bodyMedium = 18sp > bodyLarge = 16sp`). No two M3 slots may share an identical size and
   weight (`displaySmall` and `headlineLarge` are currently identical).
3. **Apple feel via tracking and weight, not a second font:** display and headline use
   `-0.02em` letter spacing (use `.em`, never hand-computed `.sp`), titles `-0.01em`, body
   `0em`, small caps/labels slightly positive (`+0.01em`). Line heights: headings 1.1 to 1.15x,
   body 1.4 to 1.5x. `WordmarkStyle` is Bold, wide tracking (`+0.12em`), all caps.
4. **Floor sizes:** no text below 11sp; informational text at least 12sp. Remove the 10sp
   `labelSmall`.
5. **`FontWeight.Black` bug:** `SplashWordmarkStyle` must use a weight the bundled family
   actually declares (Bold, or add Inter Black to the family).
6. **Collapse escape hatches.** Replace each use of `HeadlineStrongStyle`, `TitleStrongStyle`,
   `CaptionStrongStyle`, `LabelStrongStyle` with the correct M3 slot plus at most one
   `FontWeight`, then delete the style. Keep only `WordmarkStyle`, `SplashWordmarkStyle` and
   `BodyLineHeightBoost`.
7. Fix stale comments (for example "Light" where the code uses Normal). Remove "USER SPEC" comments.
8. Add a Compose preview of every type slot in both themes.

## Phase 4: Shapes

1. Reduce to the radius system: **8 / 16 / 28 dp plus pill**.
    - `AppShapes`: extraSmall 4 is allowed only if used by tooltips; otherwise small 8, medium 16, large 16, extraLarge 28.
    - Named shapes kept: `ShapeSmall` (8), `ShapeMedium` (16), `ShapeLarge` (28), `ShapePill`, `ShapeCircle`, `BottomSheetShape` (28 top), `FullScreenShape`.
2. Delete `ShapeButton` (14), `ShapePlay` (22), `ShapeRound` (50.dp, duplicate of pill), `ShapeAsymmetric`, and the onboarding folder shapes (`ShapeTab`, `ShapeFolderBody`). Move the folder shapes into the onboarding artwork file if still needed.
3. Resolve the naming collision: `ShapeMedium` currently (8dp) disagrees with `MaterialTheme.shapes.medium` (12dp). After this phase every named shape must equal its same-named M3 slot.
4. Migrate all usages to the survivors.

## Phase 5: Spacing and sizes

1. **Spacing:** replace the scale and the 17 `gapNN` tokens with the 4-point grid: `xs 4, sm 8, md 12, lg 16, xl 24, xxl 32, xxxl 48`, plus `screenPadding = 16`, `sectionGap = 32`, `itemGap = 8`, `inlineGap = 4`. Delete off-grid tokens (`gap2/3/5/7/22/26/28`, `multiValueHorizontal`, `multiValueVertical`). Migrate usages to the nearest on-grid token; if a visual regression of more than 2dp results, flag it in the changelog.
2. Remove tokens duplicated between `AppSpacing` and `AppSizes` (`iconSize`, `iconSizeLg`, `touchTarget`). Sizes owns icon and control sizes; Spacing owns gaps and padding.
3. **One touch-target token.** `touchTarget`, `topBarControl`, `cardAction`, `closeButton`, `chipTouchArea`, `likeButton` and `searchBarHeight` all equal 48dp. Keep `touchTarget = 48.dp` and migrate the rest to it. Keep a separate token only if its value genuinely differs.
4. **Move onboarding artwork dimensions out** of the global theme: everything under the sphere and folder sections (`sphereBox`, `sphereRing`, `sphereOrb`, `folderTabW`, `fileLineWidths`, `downloadDisc`, etc.) and splash-ring values go to an `OnboardingArt` object in the onboarding package.
5. **Make card sizes adaptive.** Remove fixed `mediaCardWidth = 140.dp` and `libraryCardW = 220.dp` from the global data class as constants. Derive them from `WindowSizeClass` (compact / medium / expanded) or use `GridCells.Adaptive(minSize = gridCellMin)`. Add `androidx.compose.material3:material3-window-size-class` if not present. Previews must cover a phone and a tablet width.
6. Skeleton sizes derive from the production tokens (see Phase 1, item 5). Delete all `skeleton*` size tokens that duplicate a production size.

## Phase 6: Glass, motion and the signature moment

1. **Glass restraint (Apple "material" feel).** Audit every glass usage. Keep glass only on the floating top bar and the hero action pill (two places maximum), as neutral white/black translucent materials with a hairline rim, no color tint. Everything else uses solid `surfaceContainer*` roles with a hairline border.
2. **Glass performance.** Render the noise grain once (a pre-built `ShaderBrush` or a cached `ImageBitmap`) and reuse it. Do not redraw hundreds of random dots per frame or per element. Provide an intentional fallback for devices below Android 12 where `RenderEffect` blur is unavailable: a more opaque solid surface and a hairline, not a broken half-blur.
3. **Motion tokens.** Extend `AppMotion` with easing and springs; keep the existing duration tokens:
    - `emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)`
    - `standard`, `decelerate`, `accelerate` curves
    - `pressSpring = spring(dampingRatio = 0.7f, stiffness = 400f)`
    - `reduceMotion` must make durations zero or swap springs for a plain `snap()`/short fade.
      Replace every raw `tween(...)`, `spring(...)` and `animate*` spec in composables with these tokens.
4. **Signature transition.** Implement a shared-element transition from poster card to details hero using `SharedTransitionLayout` / `sharedElement` (Compose 1.7+). Use the emphasized easing. Respect `reduceMotion` by falling back to a crossfade.
5. Film grain, if used, goes on hero imagery only, at low alpha, cached bitmap.

## Verification checklist (run before finishing)

Run these and fix everything they find:

```bash
# raw color literals outside the theme package
grep -rnE "Color\(0x" app/src/main --include=*.kt | grep -v "/ui/theme/"
# inline font sizes / weights outside theme
grep -rnE "fontSize\s*=\s*[0-9]+\.sp|FontWeight\.[A-Za-z]+" app/src/main --include=*.kt | grep -v "/ui/theme/"
# raw dp sizes for icons/controls
grep -rnE "\.size\([0-9.]+\.dp\)" app/src/main --include=*.kt
# raw radii
grep -rnE "RoundedCornerShape\(" app/src/main --include=*.kt | grep -v "/ui/theme/"
# raw animation specs
grep -rnE "tween\(|spring\(" app/src/main --include=*.kt | grep -v "/ui/theme/"
# leftover removed fonts / tokens
grep -rnE "GoogleFont|LexendFamily|ColorTeal|ColorBlue|ColorPurple|ColorGold|Style\b.*Strong" app/src/main --include=*.kt
```

Pass criteria:

- [ ] `assembleDebug` and `lint` pass; no new warnings from theme files.
- [ ] The app contains no chromatic color except error/success/warning and the red favourite heart (monochrome test passes).
- [ ] No lavender/purple tint in any M3 component in light or dark mode (check bottom sheet, nav bar, dialog, card, menu, snackbar).
- [ ] Every text/background pair in both schemes meets WCAG AA; ratios noted in comments.
- [ ] App renders correct fonts with airplane mode on a fresh install (bundled fonts).
- [ ] Skeleton and loaded states have identical bounds on phone and tablet previews.
- [ ] Status bar icons are readable over hero imagery in light mode.
- [ ] Only the allowed radii (8 / 16 / 28 / pill) and the 4-point spacing grid appear in the code.
- [ ] No fixed `140.dp` / `220.dp` card widths; layouts adapt on a tablet preview.
- [ ] Reduce-motion on: no decorative animation runs, transitions fall back to fades.
- [ ] `THEME_CHANGELOG.md` is written and accurate.

## Out of scope

Do not redesign screen layouts, change navigation, touch networking or data code, or add new
third-party dependencies beyond the window-size-class artifact. Report anything that needs a
product decision in the changelog under "Open questions" instead of guessing.