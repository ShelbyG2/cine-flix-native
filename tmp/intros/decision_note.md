# Decision gate — before implementing Option A

Open question to answer with the user before writing code:

1. Do we want the embedded player to remain the default for titles where no
   plain stream URL surfaces, or do we want to block playback entirely on those
   titles until extraction works?
2. For the subtitles we can intercept (WebVTT/SRT), do we want to surface an
   in-app subtitle picker, or just attach system subtitles via Media3 when a
   track is available?
3. Are we comfortable proxying stream requests through the app's OkHttp with the
   same headers the player used, given the upstream may still perform
   anti-hotlink checks? (The current HotlinkDefense already does this pattern
   but for a different, now-unused resolver.)

If the answer is:
- keep fallback + add subtitles when available + proxy through OkHttp with the
  same headers, then Option A is implementable now as an extract-then-play path
  with the embedded player as fallback.
- Otherwise, we need to scope what to change first.
