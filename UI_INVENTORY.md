# CineFlix UI Inventory

Checklist for the restyle: **no screen, tab, button, element, string or state may be added or removed.**
Recorded before Stage 2 (UI) began. Elements are listed in the order they appear.

## Navigation (app shell — `CineflixApp.kt`)

Bottom navigation, four destinations, this order and these labels:

| # | Label | Route | Icon drawable |
|---|-------|-------|---------------|
| 1 | Home | `Home` | `ic_home_` |
| 2 | Discover | `Discover` | `ic_explore` |
| 3 | Favourites | `Favourites` | `ic_bookmark` |
| 4 | Profile | `Profile` | `ic_profile` |

- Tapping a tab clears the back stack and opens that destination's root.
- Player is an overlay, not a destination; it covers the whole screen (bar hidden).
- Onboarding and splash are pre-main stages (bar not composed there).
- Existing implementation: `NavigationSuiteScaffold` with icon + label items.

Shell states: splash → onboarding (first launch only) → main.

---

## 1. Splash — `feature/splash/SplashScreen.kt`

Purpose: brand moment on every cold start; auto-dismisses after 2.8 s.
Elements:
- Pulsing glow ring behind the mark (two rings, phase-offset).
- Play-triangle button (rounded square) with hairline rim.
- `CINEFLIX` wordmark, per-letter staggered fade-in.
- Three animated loading dots.
States: only the entrance animation; reduce-motion freezes ring, letters and dots.

## 2. Onboarding — `feature/onboarding/OnboardingFlow.kt` (first launch only)

Host animates between three steps; [onFinished] leaves onboarding.

### 2a. Cinematic intro — `CinematicIntroScreen.kt` (step 1)
- Full-bleed image with Ken Burns zoom, top scrim, bottom scrim, vignette.
- Top row: `CINEFLIX` wordmark + `Skip` pill (exits onboarding).
- Two feature chips per slide (`Trending Now`, `4K Ultra HD`, …) — existing copy.
- Feature card: slide title + subtitle.
- Progress dots (active expands to a pill).
- Primary button: `Next` / `Get Started` + forward arrow.
- Three slides with existing copy.

### 2b. File access — `FileAccessScreen.kt` (step 2)
- 3D tilted folder artwork (tab, body, file lines, download glyph, ground shadow).
- Permission card: title `Allow access to your files?`, body copy, bullet + note.
- Primary button `Allow Access`.
- Secondary outlined button `Not Now`.
Both advance to step 3.

### 2c. GitHub star — `GitHubStarScreen.kt` (step 3)
- Glass sphere artwork (two pulse rings, specular highlights, ground shadow, GitHub mark).
- Title `Like CINEFLIX?` + body copy.
- Social-proof pill: star icon + `Starred by 2k+ developers`.
- Primary button `Star on GitHub` (opens repo, then completes).
- Text link `Maybe Later` (completes).

## 3. Home — `feature/home/HomeScreen.kt`

States: Loading (skeleton), Error (centred `ErrorState` with Retry), Success.
Success elements:
- Floating top bar: `CINEFLIX` wordmark + Search icon button (opens Discover).
- Hero carousel: paged posters, focused card shows title, star rating chip and up to three genre chips.
- Action row under the hero: `Play`, `Show details` (info), favourite heart, save bookmark.
- Sections, each a horizontal poster row with title:
  `Trending Movies`, `Trending TV Shows`, `Rated Movies`, `Rated Tv Shows`.
  Each poster: poster image, bottom fade, bookmark button, title under poster.
  Tapping a poster or the info button opens the details sheet.
- Details sheet (see 7) is hosted by this screen.
Loading skeleton mirrors: top bar, hero track, chips, action row, two poster rows.

## 4. Discover — `feature/discover/DiscoverScreen.kt`

Elements:
- Screen title `Discover`.
- Search field (hint `Search movies & TV shows…`, leading search icon, clear button when non-empty).
- Category chips (discover categories, e.g. Movies / TV Shows).
- `Sort` label + sort chips (only when the search field is blank).
- `Genre` label + genre chips (only when the search field is blank).
- Results poster grid (adaptive columns; infinite scroll).
- Loading more: inline circular spinner tile.
States: Loading (grid skeleton), Error (`ErrorState` + Retry), Success empty (`EmptyState`), Success list.
- Tapping a poster opens the details sheet.

## 5. Favourites ("My Library") — `feature/favorites/FavoritesScreen.kt`

Elements:
- Screen title `My Library`.
- Favourites row: title `Favourites`, subtitle `Movies & shows you loved`, poster cards with heart (removes).
- Saved row: title `Saved for Later`, subtitle `Your watchlist`, poster cards with bookmark (removes).
- Liked episodes row: title `Liked Episodes`, subtitle `Episodes you want to revisit`, 16:9 stills with heart (unlikes), episode name + air date.
- Tapping a card opens the details sheet.
States: Loading (skeleton), Error (wifi-off icon, message, `Try Again`), Empty (`Your library is empty`, explanation, `Explore Titles` → Discover), Success.

## 6. Profile — `feature/profile/ProfileScreen.kt`

Placeholder screen: centred text `This is Profile`. No other elements.

## 7. Details sheet — `feature/details/DetailsSheet.kt`

Modal bottom sheet opened from Home / Discover / Library.
Elements:
- Drag handle.
- Hero: 16:9 backdrop, bottom fade, close button (top-end).
- Genre chips (up to four).
- Title.
- Metadata row (`year • Runtime/Seasons • country`), only fields that exist.
- Action row: `Play`, `Show details` (only when a click handler exists), favourite, save, share (only when a handler exists).
- Expandable overview (`Read More` / `Show Less`).
- TV only: `Episodes` header, season chips (`Season N`), episode list (still, `N. Name`, overview, runtime, air date, like heart, hairline divider).
- `More Like This` poster row (opens the tapped title in the same sheet).
States: Loading (`DetailShimmerLayout`), Error (`Couldn't load details` + Retry), Success movie, Success TV, episodes loading (`EpisodeShimmerList`), episodes error, episodes empty (`No episodes available`).

## 8. Player — `feature/player/VideoPlayerScreen.kt` (full-screen overlay)

Elements:
- Loading layer: backdrop, scrim, `Playing` label, title, wavy bars, buffered progress + `%`.
- Tap anywhere shows controls for 3 s.
- Controls top bar: close button; Quality, Subtitles, Audio buttons (visibility/enabled per stream) and Layout button.
- Controls centre: Previous episode (when available), play/pause, Next episode (when available).
- Controls bottom: elapsed label, scrubber, duration label.
- Track sheets (Quality / Subtitles / Audio / Video Layout): title, radio option rows, “no subtitle tracks” note, layout options.
- Error state: `Couldn't start playback` + Retry.
- Standalone close button while loading/error and controls hidden.
- WebView fallback (no new chrome).
States: resolving stream, loading, playing, buffering, ended (auto-next/exit), error, web fallback.

---

## Reusable components (`core/components`, feature components)

| Component | Purpose | Elements |
|---|---|---|
| `FloatingTopBar` | Home top chrome | Wordmark, search icon button |
| `CineflixBottomBar` (was `NavigationSuiteScaffold`) | Tab bar | 4 icon+label tabs |
| `MediaCard` | Poster / hero card | Poster, bottom fade, favourite disc, save disc, hero title + rating + genres, title under poster |
| `ActionButtons` | Play / info / favourite / save / share row | Solid Play button, circular icon buttons |
| `HeroSection` | Home carousel | Carousel, action row |
| `MediaSection` | Titled poster row | Title, LazyRow of `MediaCard` |
| `StateViews` | Empty / error / offline | Icon, title, message, optional action |
| `Shimmer` | Skeleton block | Sweep gradient (static under reduce motion) |
| `ExpandableText` | Overview with clamp | Body text, `Read More`/`Show Less` |
| `DetailSheetHeroSection` | Details hero + actions | Backdrop, fade, close, genres, title, metadata, actions, overview |
| `TvEpisodeItem` / `TvSeasonSelector` | Episode list + season chips | Still, titles, metadata, like, divider / FilterChips |
| `DetailShimmerLayout` / `EpisodeShimmerList` | Details skeletons | Backdrop, chips, title, metadata, actions, episode rows |
| `PlayerControlsLayer` | Player chrome | Top bar, transport, scrubber |
| `PlayerLoadingLayer` | Playback loading | Backdrop, labels, wavy bars, progress |
| `GlassMorphOverlay` (deleted) | Decorative glass layer only | No user-facing content — replaced by solid surfaces; nothing lost |

## Copy strings (must not change)

`CINEFLIX`, `Search`, `Play`, `Show details`, `Add to favourites` / `Remove from favourites`,
`Save for later` / `Remove from saved`, `Add to favourites` / `Unlike episode`, `Like episode`,
`Trending Movies`, `Trending TV Shows`, `Rated Movies`, `Rated Tv Shows`, `More Like This`,
`Read More`, `Show Less`, `Discover`, `Search movies & TV shows…`, `Clear search`, `Sort`, `Genre`,
`My Library`, `Favourites`, `Movies & shows you loved`, `Saved for Later`, `Your watchlist`,
`Liked Episodes`, `Episodes you want to revisit`, `Your library is empty`, `Explore Titles`,
`Try Again`, `This is Profile`, `Something went wrong`, `Retry`, `Couldn't load details`,
`Episodes`, `Season N`, `No episodes available`, `This season has no episode data yet.`,
`Couldn't load episodes`, `Skip`, `Next`, `Get Started`, `Allow access to your files?`,
`Allow Access`, `Not Now`, `Your downloads stay on this device — used for offline playback only.`,
`Like CINEFLIX?`, `Starred by 2k+ developers`, `Star on GitHub`, `Maybe Later`,
`Playing`, `Close player`, `Quality`, `Subtitles`, `Audio`, `Layout`, `Video Layout`, `Auto`, `Off`,
`No subtitle tracks available for this stream`, `Previous episode`, `Next episode`,
`Couldn't start playback`, `The stream didn't load. Check your connection and try again.`,
`Loading…`, `Loading episodes…`, `Loading playback`, onboarding slide copy.
