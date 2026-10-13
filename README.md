<div align="center">

# 🎬 CINEFLIX

**A cinematic streaming client for Android — built natively with Jetpack Compose.**

*Browse trending titles, explore rich detail sheets, and stream through a
palette-reactive, glassmorphism UI.*

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/min%20SDK-24%20(7.0)-3DDC84)](https://developer.android.com/about/versions/nougat)
[![API](https://img.shields.io/badge/data-TMDB-01D277?logo=themoviedatabase&logoColor=white)](https://www.themoviedb.org/documentation/api)
[![Build](https://img.shields.io/badge/gradle-9.4.1-02303A?logo=gradle&logoColor=white)](https://gradle.org)

</div>

---

## 📖 Overview

CINEFLIX is a native Android app for discovering and watching movies and TV
shows. It pulls metadata and artwork from **TMDB**, resolves playable streams
through an embed gateway, and presents everything in a dark, glass-forward UI
with animated splash, onboarding, and colour-extracted hero backgrounds.

> 📐 **Architecture & full feature spec live in [DESIGN.md](DESIGN.md).**
> It details every component, the layer breakdown, and the roadmap.

---

## ✨ Features

| | Feature | Status |
|---|---|---|
| 🚀 | **Animated splash** — pulse rings, staggered wordmark, auto-advance | ✅ |
| 🎠 | **Onboarding** — 3-step cinematic intro, file-access, GitHub star | ✅ |
| 🏠 | **Home** — hero carousel + trending & top-rated rows | ✅ |
| 🎨 | **Palette-reactive hero** — poster colours drive the background | ✅ |
| 🧾 | **Details sheet** — backdrop, genres, overview, similar titles | ✅ |
| 📺 | **Season & episode browser** for TV shows | ✅ |
| ▶️ | **Stream resolver + ExoPlayer** playback with hotlink handling | 🟡 |
| 🔍 | **Discover / search** with filters | 🔜 |
| ❤️ | **Favourites** with persistence | 🔜 |
| 👤 | **Profile & settings** | 🔜 |

**UI highlights**

- 🧊 **Glassmorphism** — four-layer frosted glass (base, specular gloss, rim light, noise grain)
- 🎞️ **Ken Burns onboarding** — slow zoom, gradient scrims, depth-of-field vignette
- 🌈 **Dynamic hero** — `Palette` extracts vibrant/muted swatches per poster
- 💫 **Shimmer skeletons** while data loads, distinct offline/timeout error states
- 📱 **Adaptive layout** — bottom bar on phones, navigation rail on tablets/foldables

---

## 🛠️ Tech Stack

| Layer | Choices |
|---|---|
| **Language** | Kotlin 2.2.10 |
| **UI** | Jetpack Compose (BOM 2025.12.00), Material 3, Material 3 Adaptive |
| **Navigation** | Navigation 3 (`navigation3-runtime/ui` 1.1.2) |
| **Networking** | Retrofit 2.11.0 · OkHttp 4.12.0 · kotlinx-serialization |
| **Images** | Coil 3.0.4 |
| **Playback** | AndroidX Media3 / ExoPlayer 1.10.1 (DASH + OkHttp DataSource) |
| **Colour** | AndroidX Palette |
| **Fonts** | Lexend via Google Fonts provider |
| **Build** | Gradle 9.4.1 · AGP 9.2.1 · `secrets-gradle-plugin` |

**Requirements:** Android Studio (latest), JDK 11+, Android SDK 37.

---

## 🚀 Getting Started

### 1. Clone

```bash
git clone https://github.com/ShelbyG2/cine-flix-native.git
cd cine-flix-native
```

### 2. Add your TMDB API key

CINEFLIX uses the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin)
with its default configuration, so it reads your key from **`local.properties` in
the project root** and exposes it as `BuildConfig.TMDB_API_KEY`. That file is
already git-ignored, and `secrets.defaults.properties` provides a safe empty
fallback so the project still compiles without a key.

Grab a free key from [themoviedb.org → Settings → API](https://www.themoviedb.org/settings/api),
then add it to `local.properties` in the project root:

```properties
# local.properties  (git-ignored — never commit real keys)
sdk.dir=/path/to/your/Android/sdk
TMDB_API_KEY=your_key_here
```

> ⚠️ Keep `local.properties` out of version control. The key ships inside the APK,
> so add domain/package restrictions on the TMDB side where possible.

### 3. Build & run

```bash
# Debug build
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug
```

Or just open the project in **Android Studio** and hit **Run ▶**.

<details>
<summary><b>Alternative — a separate <code>secrets.properties</code> file</b></summary>

If you'd rather keep secrets out of `local.properties`, point the plugin at a
dedicated file in `app/build.gradle.kts`:

```kotlin
secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
}
```

Then create `secrets.properties` in the project root with `TMDB_API_KEY=...`
and add it to `.gitignore`.
</details>

---

## 📂 Project Structure

```
app/src/main/java/tech/unrealistic/cineflix/
├── MainActivity.kt              # edge-to-edge host
├── CineflixApp.kt               # app stage machine + NavDisplay + scaffold
├── core/
│   ├── components/              # MediaCard, FloatingTopBar, Shimmer, ActionButtons…
│   ├── events/                  # MediaActions (user intents)
│   ├── navigation/              # Routes, AppDestinations
│   └── preferences/             # OnboardingPreferences
├── data/remote/                 # RetrofitClient, TmdbService, models, stream resolver
├── feature/
│   ├── splash/                  # animated splash
│   ├── onboarding/              # 3-step first-launch flow
│   ├── home/                    # hero carousel, media rows, ViewModel
│   ├── details/                 # details bottom sheet + season/episode UI
│   ├── player/                  # stream resolution + ExoPlayer
│   ├── discover/  favorites/  profile/   # ← coming soon
├── helpers/                     # GenreHelper, PaletteHelper, timeConverter
└── ui/theme/                    # Color, Theme, Type, Shape, Spacing
```

---

## 🗺️ Roadmap

- [ ] **M3** — Player surface, controls, playback hardening
- [ ] **M4** — Discover: search, filters, paging
- [ ] **M5** — Favourites persistence + Profile/settings
- [ ] **M6** — Offline downloads
- [ ] **M7** — Tests, R8 shrinking, accessibility, deep links

See [DESIGN.md §12](DESIGN.md) for the full plan.

---

## 🧩 Contributing

Contributions are welcome!

1. Fork the repo
2. Create a feature branch — `git checkout -b feature/amazing-thing`
3. Commit your changes — `git commit -m "Add amazing thing"`
4. Push and open a Pull Request

Please keep changes focused and follow the existing Kotlin/Compose conventions.

---

## 📄 License

> No license has been specified yet. Add a `LICENSE` file before publishing or
> redistributing.

---

## 🙏 Acknowledgements

- **[TMDB](https://www.themoviedb.org/)** — movie & TV metadata
  *(This product uses the TMDB API but is not endorsed or certified by TMDB.)*
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** & **Material 3**
- **[ExoPlayer / Media3](https://developer.android.com/media/media3)**
- **[Coil](https://coil-kt.github.io/coil/)** — image loading
- **Lexend** typeface

---

<div align="center">
  <sub>Made with ❤️ and Jetpack Compose</sub>
</div>
