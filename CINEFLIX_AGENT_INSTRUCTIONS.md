# CineFlix — Coding Agent UI/UX Redesign Instructions

> **Stack**: Kotlin · Jetpack Compose · Material 3 · Lexend typography  
> **Design system**: `Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`, `Theme.kt`  
> (generated from Tokens Studio / Figma export — do not modify these files unless explicitly told to)

---

## 0. Ground Rules

- **Read before you write.** For every file you are about to modify, read it in full first.
- **Verify before you commit.** After every major change, run `./gradlew assembleDebug` (or the project's build command). Only commit on a **clean build**.
- **One commit per concern.** Fix a group of related issues, build, pass → commit. Do not batch unrelated changes.
- **Never hardcode colours or dimensions.** Every colour must come from `MaterialTheme.colorScheme.*`. Every dimension should reference the project's `Spacing` object or a named constant — never a raw `.dp` literal you invented.
- **Never suppress a null pointer with `!!`.** Use `?.let`, `?: return`, or `requireNotNull` with a descriptive message.
- **Preserve existing architecture.** Do not restructure ViewModels, repositories, or data models unless a UX fix strictly requires it.

---

## 1. Design Language (Premium Look & Feel)

The CineFlix visual identity is **dark-first glassmorphism on a Material 3 base**. Every screen must feel like a premium streaming app.

### Colour usage
| Role | Token |
|---|---|
| Backgrounds | `colorScheme.background` / `colorScheme.surface` |
| Cards & sheets | `colorScheme.surfaceContainer` |
| Subtle borders / dividers | `colorScheme.outlineVariant` |
| Body text | `colorScheme.onSurface` |
| De-emphasised text | `colorScheme.onSurfaceVariant` |
| Accent / interactive | `colorScheme.primary` |
| Destructive / error | `colorScheme.error` |
| **Hardcoded `Color.Gray`, `Color.LightGray`, `Color.White`, `Color.Black` are forbidden** | — |

### Typography
- Font family: **Lexend** at the weights defined in `Type.kt`.
- Titles: `titleLarge` / `titleMedium` bold.
- Body text: `bodyMedium`; captions / metadata: `bodySmall`.
- Labels on chips: `labelSmall`.

### Elevation & depth
- Use `Modifier.shadow()` or `Surface(tonalElevation = …)` for cards. Never fake depth with a hardcoded colour shift.

### Motion
- `AnimatedContent` for state transitions (Loading → Success → Error).
- `animateContentSize()` on expanding sections.
- `AnimatedVisibility` for show/hide interactions.

---

## 2. Shared Component Standards

### 2.1 Touch targets
Every interactive element must have a minimum touch target of **48 × 48 dp**. When the visual element is smaller (e.g., a 32 dp icon button), expand the touch area with `Modifier.size(48.dp)` as the outer bound and keep the visual element centred inside.

### 2.2 Loading skeleton
Create a `ShimmerBox` composable to be reused across all loading states:

```kotlin
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
    // Infinite animated horizontal gradient sweep
    // Colors: colorScheme.surfaceVariant → colorScheme.surface → colorScheme.surfaceVariant
    // Use rememberInfiniteTransition + animateFloat
    Box(modifier = modifier.clip(shape).background(brush = shimmerBrush))
}
```

Use `ShimmerBox` anywhere content is loading. Match the shimmer shape and approximate size to the real content it represents.

### 2.3 Empty state
Create an `EmptyState` composable:

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    action: Pair<String, () -> Unit>? = null
)
```

Centred vertically, icon tinted `colorScheme.onSurfaceVariant`, action rendered as a `TextButton`.

### 2.4 Error state
Create a `ErrorState` composable with a message and a **Retry** button. The retry callback must be passed down from the caller. Error text uses `colorScheme.error`; the message must be human-readable (no raw exception class names).

---

## 3. Screen-by-Screen Fixes & Standards

### 3.1 `MediaDetailsBottomSheet`

#### Drag handle
`dragHandle = {}` removes the sheet's core dismissal affordance. Replace it with a visible custom handle:

```kotlin
dragHandle = {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(40.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    )
}
```

#### Close button touch target
Current size is 32 dp — below the 48 dp minimum. Fix:

```kotlin
Box(
    modifier = Modifier
        .size(48.dp)                  // touch target
        .align(Alignment.TopEnd)
        .padding(top = 8.dp, end = 8.dp),
    contentAlignment = Alignment.Center
) {
    IconButton(
        onClick = onDismissRequest,
        modifier = Modifier
            .size(32.dp)              // visual size only
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
            .border(0.8.dp, MaterialTheme.colorScheme.primary.copy(0.25f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close details",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}
```

#### Poster vs. backdrop
`posterUrl` is currently built from `backdropPath`. These are different images with different aspect ratios. Name the variable to match what it actually uses — `backdropUrl` — and verify with the design which image is intended. If a tall hero image is needed, use `posterPath` with `w500` and a portrait aspect ratio container.

#### Hero image aspect ratio
Replace `Modifier.height(300.dp)` with `Modifier.aspectRatio(16f / 9f)` if using backdrop, or `Modifier.aspectRatio(2f / 3f)` if using poster. Never fix pixel height for images.

#### Genre chips — overflow
`Row { genreNames.forEach { … } }` will overflow silently. Replace with `FlowRow`:

```kotlin
FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.padding(horizontal = 10.dp)
) {
    mediaItem.genreNames.take(4).forEach { genre -> GenreChip(genre) }
}
```

Cap at 4 genres to avoid visual clutter.

#### Metadata null safety
Every metadata `Text` must be guarded:

```kotlin
// WRONG
Text("• ${item.originCountry?.firstOrNull()}")  // renders "• null"
Text("• ${formatTime(item.runtime!!)}")          // crashes if null

// CORRECT
item.originCountry?.firstOrNull()?.let { Text("• $it", …) }
item.runtime?.let { Text("• ${formatTime(it)}", …) }
item.releaseDate?.substringBefore("-")?.let { Text("• $it", …) }
```

#### Overview null + blank
```kotlin
mediaItem.overview
    ?.takeIf { it.isNotBlank() }
    ?.let { Text(text = it, …) }
```

#### `isFavourite` hardcoded
`isFavourite = false` is hardcoded in `ActionButtons`. This must come from the ViewModel's UI state, not a literal. The `DetailsSheetState.MovieSuccess` and `DetailsSheetState.TvSuccess` states should carry a `isFavourite: Boolean` field populated from the local favourites repository.

#### Loading state — centering
```kotlin
is DetailsSheetState.Loading -> {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        // Use ShimmerBox layout that mirrors the real content structure:
        // hero image shimmer + title shimmer + row of metadata shimmers
        DetailShimmerLayout()
    }
}
```

Create `DetailShimmerLayout` with `ShimmerBox` elements sized to match the real content.

#### Error state — add retry
```kotlin
is DetailsSheetState.Error -> {
    ErrorState(
        icon = Icons.Outlined.CloudOff,
        title = "Couldn't load details",
        subtitle = state.message,
        action = "Retry" to { sheetViewModel.fetchSheetDetails(mediaType, mediaId) }
    )
}
```

#### Trailing space
`"More Like this "` → `"More Like This"`.

---

### 3.2 `TvSeasonsAndEpisodesSection`

#### Episode title missing
The episode row shows only the overview, runtime, and air date. There is no episode number or title. Add them as the primary label:

```kotlin
Text(
    text = "${episode.episodeNumber}. ${episode.name}",
    style = MaterialTheme.typography.bodyMedium,
    fontWeight = FontWeight.SemiBold,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
Text(
    text = episode.overview,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    maxLines = 3,
    overflow = TextOverflow.Ellipsis
)
```

Verify `Episode` data class has `name` and `episodeNumber` fields; add them if missing (they exist in the TMDB episodes API response).

#### Episode image — fixed size
```kotlin
AsyncImage(
    model = "https://image.tmdb.org/t/p/w300${episode.stillPath}",
    contentDescription = "Episode ${episode.episodeNumber}: ${episode.name}",
    modifier = Modifier
        .width(120.dp)
        .height(70.dp)
        .clip(RoundedCornerShape(8.dp)),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(R.drawable.ic_image_placeholder),
    error = painterResource(R.drawable.ic_image_placeholder)
)
```

Use `w300` thumbnail, not `w500`, for small thumbnails (bandwidth + resolution match).

#### Episode click affordance
`.clickable { /* handle episode click */ }` shows a ripple but does nothing. This is a broken affordance. Either:
- Wire it up: navigate to the episode player, or
- Remove `.clickable` entirely until the feature is implemented.

Do not ship a clickable element that has no action.

#### Season switch loading state
When `onSeasonSelected` fires, the episode list must show loading state before the new episodes arrive. Add a `episodesLoading: Boolean` to the ViewModel UI state and render `ShimmerBox` episode rows (3 rows) while loading.

#### Episode empty state
```kotlin
if (episodesState.isEmpty()) {
    EmptyState(
        icon = Icons.Outlined.VideoLibrary,
        title = "No episodes available",
        subtitle = "This season has no episode data yet."
    )
} else {
    // episode list
}
```

#### Divider colour
```kotlin
// WRONG
Divider(color = Color.LightGray, thickness = 0.5.dp)

// CORRECT
HorizontalDivider(
    color = MaterialTheme.colorScheme.outlineVariant,
    thickness = 0.5.dp
)
```

Note: `Divider` is deprecated in Material 3 — use `HorizontalDivider`.

---

### 3.3 Home / Browse Screen

Verify the following on the home screen. Fix anything that does not meet the standard:

- **Hero section**: must use the glassmorphism treatment — layered gradient, noise grain or specular streak, rim light border. Confirm these effects are applied.
- **Media cards**: images must have `contentDescription` set to the title. Cards must have defined `width` and `aspectRatio` — never unconstrained.
- **Section headers**: `titleMedium` bold, `colorScheme.onBackground`.
- **Scroll state**: `LazyRow` and `LazyColumn` must preserve scroll position across recomposition with `rememberLazyListState()`.
- **Pull-to-refresh**: if refresh is supported, use `PullToRefreshBox` (Material 3) with a loading indicator.

---

### 3.4 Search Screen

- Search input must have `ImeAction.Search` and fire the query on keyboard submit.
- Debounce search input by 400 ms in the ViewModel before calling the API.
- Show a `ShimmerBox` grid while results load.
- Show `EmptyState` when the query returns no results.
- Show `ErrorState` with retry on network error.

---

## 4. Accessibility Checklist

Run through this for every composable you create or modify:

- [ ] Every `Image` / `AsyncImage` has a non-empty `contentDescription` that describes the content meaningfully. Use `null` only for purely decorative images.
- [ ] Every `IconButton` has a `contentDescription` on the `Icon` (the button itself inherits it).
- [ ] Touch targets ≥ 48 × 48 dp.
- [ ] No hardcoded colour in any text or icon — must derive from `colorScheme`.
- [ ] Text does not scale below `bodySmall` / 11sp.
- [ ] Loading states are announced semantically: wrap shimmer layouts in `Modifier.semantics { contentDescription = "Loading…" }`.

---

## 5. Commit Protocol

Follow this sequence after every group of related changes:

```
1. ./gradlew assembleDebug
   - If build fails: fix errors, do NOT commit.
   - If build succeeds: proceed.

2. git add -p   (review each hunk — do not blindly git add .)

3. git commit -m "<scope>: <what changed and why>"
```

### Commit message scopes

| Scope | Use for |
|---|---|
| `ui(sheet)` | Bottom sheet changes |
| `ui(home)` | Home / browse screen |
| `ui(search)` | Search screen |
| `ui(episodes)` | Season/episode section |
| `ui(theme)` | Shared theme or component changes |
| `fix(crash)` | Null safety or exception fixes |
| `fix(a11y)` | Accessibility fixes |
| `refactor(state)` | ViewModel/state changes |

### Example commit sequence

```
fix(crash): guard all nullable TMDB metadata fields with ?.let
fix(a11y): expand close button touch target to 48dp, fix episode contentDescriptions  
ui(sheet): replace hardcoded colors with colorScheme tokens, add drag handle
ui(sheet): add DetailShimmerLayout for loading state, ErrorState with retry
ui(episodes): fix episode image size, add title row, wire episode click
ui(theme): create ShimmerBox, EmptyState, ErrorState shared composables
ui(home): verify glassmorphism hero, card dimensions, scroll state preservation
```

---

## 6. Verification Checklist Before Final Commit

Before declaring the UI pass done, manually verify every item:

### Visual
- [ ] Dark and light theme both look intentional (test with `Force dark mode` in Developer Options).
- [ ] No visual element overflows its container or clips text unexpectedly.
- [ ] Genre chips wrap correctly with 4+ genres.
- [ ] Episode images are consistently sized regardless of image load state.
- [ ] The bottom sheet drag handle is visible and the sheet can be dismissed by swipe.

### States
- [ ] Loading state shows skeleton layout — not a spinner in the top-left corner.
- [ ] Error state shows human-readable message and a functional Retry button.
- [ ] Empty episode list shows `EmptyState`, not a blank screen.
- [ ] Switching seasons shows a loading state before new episodes render.
- [ ] Favourite toggle reflects actual saved state, not hardcoded `false`.

### Interaction
- [ ] Close button registers taps reliably (touch target ≥ 48 dp).
- [ ] No `clickable` modifier on any element that has no action.
- [ ] Search triggers on keyboard submit, debounces on text change.

### Null safety
- [ ] No `!!` operators on TMDB data fields anywhere in the codebase.
- [ ] `overview`, `runtime`, `originCountry`, `posterPath`, `backdropPath`, `releaseDate` all null-checked before use.

### Accessibility
- [ ] All interactive elements have `contentDescription`.
- [ ] No hardcoded `Color.*` anywhere in UI files.
- [ ] Shimmer containers have semantic loading descriptions.
