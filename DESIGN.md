# CINEFLIX — System Design Document

> **Project:** `cine-flix-native` · **Application ID:** `tech.unrealistic.cineflix`
> **Platform:** Android (native) · **Language:** Kotlin · **UI:** Jetpack Compose
> **Status:** Active development · **Version:** 1.0 (`versionCode 1`)

---

## 1. Purpose

This document is the single source of truth for **what CINEFLIX is, how it is
architected, and every component and feature that must exist** for the product to
ship. It is written for engineers and reviewers: each feature lists its current
state (`Live` / `Partial` / `Planned`) so the remaining work is unambiguous.

---

## 2. Product Overview

CINEFLIX is a cinematic streaming-discovery client for Android. It aggregates
metadata from **TMDB (The Movie Database)** and streams video through an
embed-gateway resolver, wrapped in a glassmorphism, dark-first UI.

**Core user journey:**

```
Splash → Onboarding (first run only) → Home
   → Browse hero carousel & rows → Open details sheet
   → Pick season/episode → Resolve stream → Play
```

### Design pillars

| Pillar | Meaning in practice |
|---|---|
| **Cinematic** | Ken Burns imagery, animated splash, palette-reactive hero backgrounds |
| **Glassmorphism** | Four-layer frosted glass (`GlassMorphOverlay`), rim light, noise grain |
| **Responsive** | Shimmer skeletons, parallel network fan-out, lifecycle-aware collection |
| **Adaptive** | `NavigationSuiteScaffold` → bottom bar on phones, rail on tablets/foldables |

---

## 3. Technical Baseline

| Concern | Choice |
|---|---|
| Min / Target / Compile SDK | **24** / **36** / **37** |
| AGP / Gradle / Kotlin | **9.2.1** / **9.4.1** / **2.2.10** |
| UI toolkit | Jetpack Compose, **BOM 2025.12.00**, Material 3 **1.4.0** |
| Navigation | **Navigation 3** (`navigation3-runtime/ui` **1.1.2**) + `adaptive-navigation3` |
| Networking | Retrofit **2.11.0** + OkHttp **4.12.0** + kotlinx-serialization converter |
| Image loading | Coil **3.0.4** (`coil-compose`, `coil-network-okhttp`) |
| Playback | AndroidX Media3 ExoPlayer **1.10.1** (+ DASH, OkHttp DataSource, Compose UI) |
| Colour extraction | AndroidX Palette **1.0.0** |
| Typography | Lexend via **Google Fonts provider** (`ui-text-google-fonts`) |
| Secrets | `secrets-gradle-plugin` **2.0.1** → injects `TMDB_API_KEY` into `BuildConfig` |
| Persistence | `SharedPreferences` (`OnboardingPreferences`) |
| Tests | JUnit 4, Espresso, Compose UI test (scaffolding only) |

---

## 4. Architecture

CINEFLIX follows a lightweight **MVVM + unidirectional data flow** pattern with a
three-layer split. There is no DI framework yet — services are reached through the
`RetrofitClient` service-locator singleton.

```
┌─────────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER                                         │
│  Composables (feature/*)  ←collect──  ViewModels (StateFlow)│
│  core/components (reusable UI)                              │
├─────────────────────────────────────────────────────────────┤
│  DOMAIN / STATE                                             │
│  Sealed UI states: HomeUiState, DetailsSheetState           │
│  core/events (MediaActions), helpers (Genre/Palette/time)   │
├─────────────────────────────────────────────────────────────┤
│  DATA LAYER                                                 │
│  RetrofitClient → TmdbService → TmdbResponse models        │
│  VideoStreamResolver (WebView URL sniffing)                 │
│  OnboardingPreferences (SharedPreferences)                  │
└─────────────────────────────────────────────────────────────┘
```

### 4.1 Layer responsibilities

| Layer | Owner | Contract |
|---|---|---|
| **Presentation** | `feature/*`, `core/components` | Renders state; emits user intents; never calls the network directly |
| **Domain / State** | ViewModels, `core/events` | Owns `StateFlow<UiState>`, maps errors to user-facing messages |
| **Data** | `data/remote`, `core/preferences` | Fetches + serialises; exposes suspend functions / plain results |

### 4.2 Data flow (Home example)

```
HomeViewModel.init()
   └─ fetchAllHomeData()
        └─ viewModelScope.launch + supervisorScope
             ├─ async { getTrendingMovies() }
             ├─ async { getTrendingShows() }
             ├─ async { getTrendingMixed() }
             ├─ async { getRatedMovies() }
             └─ async { getRatedTv() }
                    │  (all awaited)
                    ▼
        _uiState.value = HomeUiState.Success(...)
                    │  StateFlow
                    ▼
HomeScreen  collectAsStateWithLifecycle()  →  Loading | Success | Error
```

- **Parallel fan-out:** five endpoints fetched concurrently with `async`.
- **Error taxonomy:** `SocketTimeoutException` → "server took too long";
  `UnknownHostException` → "no internet"; `Exception` → generic message.
- **Lifecycle safety:** `collectAsStateWithLifecycle()` pauses collection when
  the screen is backgrounded.

---

## 5. Application Lifecycle & Navigation

### 5.1 Top-level stages (`AppStage`)

Defined in `CineflixApp.kt`:

```
SPLASH ──(2.8 s)──▶ ONBOARDING (first launch only) ──▶ MAIN
   │                        │
   │                        └─ OnboardingPreferences.hasCompletedOnboarding()
   └─ SplashScreen.onFinished()
```

Cross-faded with a 500 ms `Crossfade`. The onboarding-completed flag is written
via `OnboardingPreferences.markOnboardingComplete()`.

### 5.2 Navigation 3 model

Routes are `@Serializable` `NavKey` objects (`core/navigation/Routes.kt`):

| Key | Type | Back-stack role |
|---|---|---|
| `Home` | object | Default root / tab |
| `Discover` | object | Tab |
| `Favourites` | object | Tab |
| `Profile` | object | Tab |
| `MediaDetailModal(valid: Int, type: String)` | data class | Detail route (**not yet wired into `NavDisplay`**) |
| `Player(id: Int, type: String)` | data class | Playback route (**not yet wired into `NavDisplay`**) |

`AppDestinations` enum binds each tab to a label, icon resource, and `NavKey`.

**Current back-stack behaviour** (`MainAppContent`):

- Tab click → `backStack.clear()` then push the tab route (single-root stack).
- `NavDisplay` `entryProvider` maps `Home | Favourites | Discover | Profile`;
  any other key falls through to a `Text("Not found")` placeholder.
- `onBack` → `backStack.removeLastOrNull()`.

> ⚠️ **Gap:** `MediaDetailModal` and `Player` are declared but have **no
> `NavEntry`**, so navigating to them renders the placeholder. Today the detail
> view is instead presented as a `ModalBottomSheet` owned by `HomeScreen`. The
> design intent (§7) is that both routes become first-class destinations.

### 5.3 Adaptive chrome

`NavigationSuiteScaffold` renders:
- **Bottom navigation bar** — compact width (phones)
- **Navigation rail** — medium/expanded (tablets, foldables)

Selected item tint is white, unselected grey; icons come from `res/drawable`
(`ic_home_`, `ic_explore`, `ic_bookmark`, `ic_profile`).

---

## 6. Component Inventory

### 6.1 Reusable UI — `core/components`

| Component | File | Purpose |
|---|---|---|
| `MediaCard` | `MediaCard.kt` | Poster card; hero variant overlays title, `RatingChip` (0–10 → "X.X" + amber star), and up to 3 `GenreChip`s over a bottom gradient |
| `FloatingTopBar` | `TopBar.kt` | `CINEFLIX` wordmark + glass pill search affordance; background animates transparent → `surfaceContainer` past the hero |
| `shimmerEffect()` | `Shimmer.kt` | `Modifier` that paints a travelling linear-gradient skeleton |
| `ExpandableText` | `ExpandableText.kt` | Collapsed (3-line) / expanded overview text |
| `ActionButtons` | `ActionButtons.kt` | Play / Favourite / Share row used by hero + detail sheet |
| `StylishIcon` | `StylishIcon.kt` | Decorative icon wrapper |

### 6.2 Presentation primitives — `feature/home/components`

| Component | Purpose |
|---|---|
| `HeroSection` | `HorizontalCenteredHeroCarousel` + palette-reactive `GlassHeroBackground`; focused-card scale (1.0 ⇄ 0.8) and alpha (1.0 ⇄ 0.5) animations |
| `GlassMorphOverlay` | Four-layer glass: frosted base → specular gloss + diagonal streak → rim border → seeded noise grain (400 dots, `Random(42)`) |
| `MediaSection` | Titled horizontal `LazyRow` of `MediaCard`s; **stable keys** (`key = { it.id }`); self-hides when empty |
| `HomeScreenSkeleton` | Shimmer loading layout for Home |

### 6.3 Feature screens — `feature/*`

| Feature | File(s) | State |
|---|---|---|
| **Splash** | `splash/SplashScreen.kt` | 🟢 Live — pulse rings, play glyph, staggered `CINEFLIX` letter fade, sine-wave dots; auto-dismiss 2.8 s |
| **Onboarding** | `onboarding/OnboardingFlow.kt` + 3 screens | 🟢 Live |
| **Home** | `home/HomeScreen.kt`, `HomeViewModel.kt` | 🟢 Live (play/share intents 🔴 unwired) |
| **Details** | `details/DetailsSheet.kt`, `DetailSheetViewModel.kt` | 🟢 Live |
| **Player** | `player/VideoPlayerScreen.kt`, `HotlinkDefense.kt` | 🟡 Partial — resolves stream, but no player surface rendered |
| **Discover** | `discover/DiscoverScreen.kt` | 🔴 Placeholder (`"This is Discover"`) |
| **Favourites** | `favorites/FavoritesScreen.kt` | 🔴 Placeholder |
| **Profile** | `profile/ProfileScreen.kt` | 🔴 Placeholder |

### 6.4 Details sub-components — `feature/details/components`

| Component | Purpose |
|---|---|
| `DetailSheetHeroSection` | Backdrop, close button, genre chips, title, year • runtime/seasons • country, `ActionButtons`, `ExpandableText` |
| `TvSeasonSelector` | `FilterChip` row — "Season N"; triggers episode refetch |
| `TvEpisodeItem` | Episode row (still, number, name, runtime, overview) |

### 6.5 Data layer — `data/remote`

| Component | Purpose |
|---|---|
| `RetrofitClient` | Singleton; base `https://api.themoviedb.org/3/`; API-key query interceptor; logging interceptor; 10 s connect/read/write timeouts |
| `TmdbService` | Retrofit interface (§6.6) |
| `VideoStreamResolver` | Headless `WebView` URL sniffer (§8) |
| `TmdbResponse.kt` | Models + polymorphic `MediaSerializer` |

### 6.6 TMDB endpoints consumed

| Endpoint | Method | Used by |
|---|---|---|
| `trending/movie/week` | `getTrendingMovies` | Home row |
| `trending/tv/week` | `getTrendingShows` | Home row |
| `trending/all/day` | `getTrendingMixed` | Hero carousel |
| `movie/top_rated` | `getRatedMovies` | Home row |
| `tv/top_rated` | `getRatedTv` | Home row |
| `movie/{id}` | `getMovieDetail` | Detail sheet |
| `tv/{id}` | `getTvDetail` | Detail sheet |
| `movie/{id}/similar` | `getSimilarMovie` | "More Like This" |
| `tv/{id}/similar` | `getSimilarTv` | "More Like This" |
| `tv/{id}/season/{n}` | `getSeason` | Season selector / episodes |

> **Required additions:** `search/movie`, `search/tv`, `search/multi`
> (Discover), `movie/{id}/credits` & `tv/{id}/credits` (cast), `genre/movie/list`
> (filtering), `trending/{media_type}/day` with paging.

### 6.7 Domain models

- **`MediaItem`** — sealed interface (movie | tv) with a
  `JsonContentPolymorphicSerializer` that dispatches on `media_type`, then falls
  back to field sniffing (`name`/`first_air_date` → TV, `title`/`release_date` → movie).
- **`Movie`** / **`Tv`** — list + detail fields (runtime, tagline, status, season/episode counts).
- **`Season`** / **`Episode`** — episode list payloads.
- **`displayTitle`** extension — uniform title accessor.
- **`GenreHelper.genreNames`** extension — resolves genre IDs → names (28-entry map), caps at 3.

### 6.8 Helpers

| Helper | Purpose |
|---|---|
| `GenreHelper` | ID → name mapping, `fromIds` / `fromGenres` / `nameOf` |
| `PaletteHelper` | Downloads a `w92` thumbnail (ARGB, non-hardware), generates a 16-colour `Palette` on `Dispatchers.IO` |
| `formatTime()` | Minutes → `"1h 30m"` / `"45m"` |

### 6.9 Design system — `ui/theme`

| File | Contents |
|---|---|
| `Color.kt` | `md_dark_*` / `md_light_*` token palettes |
| `Theme.kt` | `CineflixTheme` — dark & light schemes; **dynamic colour off by default** |
| `Type.kt` | `AppTypography` — Lexend (Light/Normal/Medium/SemiBold/Bold) with a 1.25 modular scale (16 → 20 → 25 → 31 → 39 → 49 sp); body = Light 16sp, heading line-height 110%, body 140% |
| `Shape.kt` / `Spacing.kt` | Corner + spacing tokens |

### 6.10 Navigation & cross-cutting

| Component | Purpose |
|---|---|
| `Routes.kt` | `@Serializable` NavKeys |
| `AppDestinations.kt` | Tab enum (label, icon, route) |
| `MediaActions.kt` | ⚠️ **Stub** — `onPlay/onFavourite/onSearch/onMediaClick` are empty functions |
| `OnboardingPreferences` | SharedPreferences wrapper for the onboarding flag |

---

## 7. Feature Specification

Status key: 🟢 Live · 🟡 Partial · 🔴 Not started

### F1 · Splash — 🟢
- **Must:** Animated play glyph with double pulse rings, staggered wordmark,
  sine-wave loading dots, auto-advance after 2.8 s, run on **every** cold start.

### F2 · Onboarding (first launch only) — 🟢
Three-step `AnimatedContent` flow with horizontal slide transitions:

1. **CinematicIntroScreen** — 3 Unsplash slides, Ken Burns zoom (1.0→1.12, 7 s),
   gradient scrims, radial vignette, glass feature card, expanding progress dots,
   `Next`/`Get Started`, `Skip` pill.
2. **FileAccessScreen** — 3D perspective folder (floating tilt 14°→22°),
   glass permission card, `Allow Access` / `Not Now`.
3. **GitHubStarScreen** — glass sphere with GitHub mark, dual pulsing rings,
   social-proof pill, `Star on GitHub` (opens repo via `ACTION_VIEW`), `Maybe Later`.

Both completion paths call `markOnboardingComplete()`.

### F3 · Home — 🟢 (play/share 🔴)
- **Must:** Hero carousel of trending-all, four titled rows, scroll-aware top bar,
  shimmer skeleton while loading, distinct offline/timeout/generic error states.
- **Must (gap):** wire `ActionButtons` **Play** → `Player` route and **Favourite**
  → favourites store; today `onPlayClick`/`onFavourite` default to `{}`.

### F4 · Media Details — 🟢
- `ModalBottomSheet` at 85% height, no drag handle, nav-bar insets.
- Loads detail + similar in parallel; TV additionally loads season 1 episodes.
- Hero with backdrop, genre chips, `year • runtime|seasons • country`,
  expandable overview, action row.
- **TV:** `TvSeasonSelector` chips refetch episodes per season; selection is
  `rememberSaveable` and resets to season 1 on open.
- **"More Like This"** row swaps the sheet content in place.

### F5 · Stream Resolution & Playback — 🟡
- **Live:** `VideoStreamResolver` loads the embed gateway in a headless WebView
  (desktop UA, JS + DOM storage), intercepts `.m3u8` / `.mpd` / `/video/source`
  requests, returns `(streamUrl, referrer)`, then destroys the WebView.
- **Live:** `createCustomStreamingPlayer()` builds an `ExoPlayer` with an
  `OkHttpDataSource` that spoofs `User-Agent` + `Referer`/`Origin` to pass hoster
  hotlink checks; `prepare()` is called; release on dispose.
- **🔴 Gap — player surface:** `VideoPlayerScreen` never renders the player.
  The `playerState != null` branch contains only comments. **Must** mount a
  `PlayerView`/`AndroidView` (or Compose `PlayerSurface`) with controls.
- **Must add:** loading/error states are present but need retry; orientation
  handling; seek/buffer UI; back-to-details navigation.

### F6 · Discover / Search — 🔴
Placeholder only. **Must include:**
- Debounced search bar wired to the `FloatingTopBar` search icon.
- `search/multi` (or separate movie/tv) with paging.
- Filters: media type, genre, year, rating; sort options.
- Results grid reusing `MediaCard`; empty + error + clear-query states.
- Tapping a result opens the details sheet (F4).

### F7 · Favourites — 🔴
Placeholder only. **Must include:**
- Persistent bookmark store (DataStore/Room) keyed by `(id, mediaType)`.
- Add/remove from hero, detail sheet, and card long-press.
- Grid/list of saved titles with metadata fetched lazily; empty state; sort/removal.

### F8 · Profile / Settings — 🔴
Placeholder only. **Must include:**
- Appearance (dark / light / system / dynamic colour toggle).
- About, version, licences, GitHub link.
- Cache management (clear image cache).
- TMDB attribution notice (required by TMDB terms).
- Content/quality preferences for the player.

### F9 · Offline Downloads — 🔴 (promised in onboarding)
Onboarding explicitly promises offline playback. **Must include** either a real
download manager (scoped storage, `DownloadManager`/Media3 DownloadService) **or**
the onboarding copy must be revised. Decide before release.

### F10 · Deep Linking / Back Navigation — 🟡
- System back pops the stack; root-tab restore already handled.
- **Must add:** predictive back, deep links (`cineflix://title/{id}`), and
  restore-on-process-death for the selected tab + sheet.

---

## 8. Streaming Subsystem (deep dive)

```
Player(id, type)
   └─ VideoStreamResolver.resolvedEmbedUrl("https://vidsrc.me/embed/{type}/{id}")
        └─ headless WebView (desktop UA, JS, DOM storage)
             ├─ shouldInterceptRequest → matches .m3u8 | .mpd | /video/source
             │      └─ listener.onStreamResolved(url, referrer)
             │             └─ createCustomStreamingPlayer()
             │                    └─ OkHttpDataSource + UA/Referer/Origin headers
             │                           └─ ExoPlayer.prepare()
             └─ on match → stopLoading() + destroy()  (memory hygiene)
```

**Design constraints:**
1. The WebView must be torn down as soon as a manifest URL is found — it is a
   temporary decoder, not a retained view.
2. The **embed URL is the referrer**, which the hoster validates; the player must
   forward it on every segment request.
3. ⚠️ `shouldOverrideUrlLoading` currently uses `url.all { embedUrl.contains(it) }`
   — a per-character check that does not express domain locking as intended.
   **Must** be rewritten as a proper host comparison to keep ad/redirect scripts out.
4. Errors must surface as a user-visible retry state (currently a plain `Text`).

---

## 9. Non-Functional Requirements

### Performance
- Parallel endpoint fan-out (`supervisorScope` + `async`).
- Stable `LazyRow` keys to avoid needless recomposition.
- `GlassMorphOverlay` pre-builds brushes in `drawWithCache`.
- Palette extraction runs on `Dispatchers.IO` against a 64×96 downscale.
- **Add:** paging for long lists, image-request throttling, baseline profiles.

### Error handling
- Typed, user-facing messages for offline / timeout / generic failures (Home, Details).
- **Add:** retry affordances, global error boundary, non-crashing handlers for
  `fetchEpisodesOnly` (currently swallows exceptions silently).

### Security
- 🔴 **`HttpLoggingInterceptor.Level.BODY` logs full URLs including the injected
  `api_key` query parameter.** This **must** be gated to `BuildConfig.DEBUG`.
- TMDB key injected via `BuildConfig` from `local.properties` (git-ignored; never hard-coded).
- **Add:** certificate pinning consideration, R8/ProGuard for release,
  disable WebView debugging in release.

### Privacy
- Only `INTERNET` permission is requested — no storage/camera/location.
- **Note:** onboarding shows a file-access screen but no permission is actually
  declared or requested; reconcile with F9 before release.

### Accessibility
- Content descriptions on icons/cards.
- **Add:** TalkBack traversal order, minimum touch targets (48 dp), contrast
  verification for glass surfaces, motion-reduction respect.

---

## 10. Testing Strategy

Current coverage is scaffolding only (`ExampleUnitTest`, `ExampleInstrumentedTest`).

| Layer | Target |
|---|---|
| Unit | `MediaSerializer` polymorphic dispatch, `GenreHelper`, `formatTime`, `HomeViewModel` state transitions (fake `TmdbService`) |
| UI (Compose) | Splash → onboarding → home navigation, details sheet open/dismiss, season switching |
| Integration | Retrofit contract tests against a recorded TMDB fixture |
| Manual | Stream resolution across gateways, tablet/rail layout, process-death restore |

**Recommendation:** inject `TmdbService` into ViewModels (constructor DI) to make
them testable; today `HomeViewModel` reaches the singleton directly.

---

## 11. Repository Layout

```
cine-flix-native/
├── app/src/main/java/tech/unrealistic/cineflix/
│   ├── MainActivity.kt              # edge-to-edge host
│   ├── CineflixApp.kt               # AppStage machine + NavDisplay + scaffold
│   ├── core/
│   │   ├── components/              # MediaCard, TopBar, Shimmer, ActionButtons…
│   │   ├── events/MediaActions.kt   # ⚠️ stubs
│   │   ├── navigation/              # Routes.kt, AppDestinations.kt
│   │   └── preferences/             # OnboardingPreferences
│   ├── data/remote/                 # RetrofitClient, TmdbService, models, resolver
│   ├── feature/                     # home, discover, favorites, profile,
│   │                                # details, player, onboarding, splash
│   ├── helpers/                     # GenreHelper, PaletteHelper, timeConverter
│   └── ui/theme/                    # Color, Theme, Type, Shape, Spacing
├── tools/generate_launcher_icons.py # adaptive-icon generator
├── secrets.defaults.properties      # TMDB_API_KEY placeholder
└── AGENTS.md / DESIGN.md / README.md
```

---

## 12. Delivery Roadmap

| Milestone | Scope | Status |
|---|---|---|
| **M1 — Foundation** | Splash, onboarding, theming, navigation shell, TMDB client | ✅ Done |
| **M2 — Browse** | Home rows, hero carousel, palette backgrounds, details sheet | ✅ Done |
| **M3 — Playback** | Player surface, controls, fix `shouldOverrideUrlLoading`, release-gate logging | 🟡 In progress |
| **M4 — Discovery** | Search, filters, paging | 🔴 Planned |
| **M5 — Library** | Favourites persistence, Profile/settings | 🔴 Planned |
| **M6 — Offline** | Downloads *or* remove onboarding promise | 🔴 Planned |
| **M7 — Hardening** | Tests, R8, accessibility pass, deep links, baseline profile | 🔴 Planned |

---

## 13. Open Decisions

1. **Offline (F9):** implement real downloads or drop the onboarding claim?
2. **Persistence:** DataStore vs Room for favourites (Room if search/filtering on
   saved items is wanted).
3. **DI:** introduce Hilt/Koin as feature count grows, or keep the service locator?
4. **`MediaDetailModal` / `Player`:** promote to real `NavEntry`s (recommended) vs
   keep bottom-sheet presentation.
5. **Details as sheet vs screen:** sheet is currently Home-scoped; Discover and
   Search will also need it — extract to a shared host.
6. **Source reliability:** the embed gateway is a third-party dependency; define a
   fallback source strategy and a graceful "no sources" state.

---

*This document is living — update it whenever a component or feature lands.*
