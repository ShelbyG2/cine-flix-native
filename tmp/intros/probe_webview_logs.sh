#!/usr/bin/env bash
# Debug-only run: launch the app variant that logs every shouldInterceptRequest
# URL to logcat under the tag "StreamIntercept" so we can see whether a plain
# HLS/DASH/MP4 manifest or segment URL is ever requested by the inner player.
# Remove or disable once we confirm what surfaces.

set -euo pipefail
ADB:=adb
EMULATOR_SERIAL:=emulator-5554

run() {
  echo "Starting instrumentation-run on $EMULATOR_SERIAL (debug APK expected in app/build/outputs/apk/debug/app-debug.apk)"
  "$ADB" -s "$EMULATOR_SERIAL" shell "am start -n tech.unrealistic.cineflix/.MainActivity" >/dev/null 2>&1 || true
  sleep 1
  echo "Instrumenting: tap the Tenzing movie hero play button to open the player, then wait."
  # coordinates: hero play button at ~ (423, 2645) in portrait — adjust if layout differs.
  "$ADB" -s "$EMULATOR_SERIAL" shell input tap 423 2645 || true
  sleep 10
  echo "Capturing intercepted-request log lines. Ctrl+C stops."
  "$ADB" -s "$EMULATOR_SERIAL" logcat -v brief -s StreamIntercept,VideoPlayer:* | head -n 200
}

# If run against a freshly built debug APK, ensure it is the instrumented variant.
if [ -f app/build/outputs/apk/debug/app-debug.apk ]; then
  echo "Debug APK present at app/build/outputs/apk/debug/app-debug.apk"
else
  echo "No debug APK found; build first with ./gradlew :app:assembleDebug"
fi

run
