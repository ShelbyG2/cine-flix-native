# Option A probe plan

Goals:
1. Confirm whether EmbedWave's inner player (web.nxsha.app) ever exposes a plain,
   replayable HLS (.m3u8) / DASH (.mpd) / MP4 URL we can feed into Media3.
2. If yes: extract it via shouldInterceptRequest interception from inside a
   real WebView and/or read results out of JS via evaluateJavascript or
   WebMessagePort, then feed it to ExoPlayer/Media3 with OkHttpDataSource that
   carries the same referer/origin/headers the player uses.
3. If no: keep the embedded WebView as the fallback player and only switch to
   Media3 when we actually get a usable URL.

What to check first:
- The inner player runs a Next.js/React JS app. It makes signed token calls to
  api/servers and api/sources, then builds per-quality HLS playlist URLs and
  feeds them into an MSE video.js sourceBuffer. The visible <video> src is a
  blob URL, not a replayable URL.
- shouldInterceptRequest can capture the manifest/segment URLs as the page
  requests them. If the player fetches a master .m3u8 (or a direct MP4), that's
  our extraction point.
- There may also be subtitles loaded from web/JSON that we can intercept and
  attach to Media3 through MediaItem.Subtitle entries.

Known unknowns to resolve with instrumentation:
- Does the master playlist ever get requested (for any title/server)?
- Are segments fetched directly by domain, or inside blob/MSE?
- Is there a subtitle endpoint we can intercept?
- Do requests carry the same referer/origin/headers the old HotlinkDefense
  constructed? If so, our OkHttpDataSource can replay them exactly.

Instrumentation plan:
- Add a temporary build flag / debug-only WebViewClient that logs every
  shouldInterceptRequest target URL (without returning a response) so we can see
  the manifest vs segment pattern for a real title, e.g. movie 533535 and TV
  1399/1/1.
- Re-launch on emulator or inspect headless logs if browser layer is enough.
- Decide: if a plain .m3u8 shows up for either title, Option A is viable and we
  implement via Media3 + custom OkHttpDataSource. If not, we implement the
  extraction scaffolding anyway (so future provider changes can surface a URL)
  and keep the embedded player as the default.

Scope boundaries:
- We do NOT implement Option B (reimplementing server negotiation) at this stage —
  the JS is obfuscated/signed and that effort is risky and unverified.
- We do NOT change the EmbedWave URL builder; that stays as is.
- We keep the existing fallback-behind-flag behavior so nothing regresses while
  we prove extraction works.
