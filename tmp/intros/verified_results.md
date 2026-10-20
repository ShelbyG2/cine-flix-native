# Instrumentation results (Option A probe)

Findings so far:
- The inner player (web.nxsha.app) negotiates streams through signed token API
  calls and feeds HLS per-quality playlists into an MSE video.js sourceBuffer.
- The visible <video> src is a blob URL, not a replayable URL.
- In a headless Chromium run, no plaintext .m3u8 master playlist surfaced in the
  intercepted-request list for the tested titles (movie 533535, TV 1399/1/1).
- The player did expose subtitle/quality/server-selector UI, but not a
  straightforward manifest URL.

Conclusion before implementation:
- We should still build the extraction scaffolding and prove that Media3
  playback works once a plain URL is available, but we cannot yet hand-write a
  stream URL for a real title; we need to either:
  - intercept a manifest emerging at runtime, or
  - confirm the page sometimes posts a URL that we can read out, or
  - fall back to the embedded player for titles that don't expose one.

Decision:
- Implement Option A in two parts:
  1. Extraction plumbing: a player resolver that tries to obtain a real
     stream URL from the WebView (via interception + optional JS readout) and
     falls back to "no stream available" when none surfaces.
  2. Media3 playback path: when a URL is available, play with ExoPlayer/Media3
     + Media3 UI, native fullscreen and subtitle support; when not available,
     keep the existing embedded WebView as the visible player.
- Verify with a real title only once the resolver can surface a URL (even if
  that requires an intercept at runtime). Until then, the build and unit path
  must pass, and the embedded path must remain unchanged in behavior.
