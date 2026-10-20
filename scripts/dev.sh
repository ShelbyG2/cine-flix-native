#!/usr/bin/env bash
# Dev helper: install the debug build on a connected phone, launch it and tail its logcat.
#
#   ./scripts/dev.sh            # build + install + launch, then stream logcat
#   ./scripts/dev.sh run        # build + install + launch only
#   ./scripts/dev.sh logs       # stream logcat for the running app (re-attaches on restart)
#   ./scripts/dev.sh watch      # re-deploy whenever a Kotlin/resource file is saved
#   ./scripts/dev.sh devices    # show attached devices and connection help
#   ./scripts/dev.sh wireless   # set up wireless debugging (pair, then connect)
#   ./scripts/dev.sh pair <ip:port> <code>  # pair using the code from Wireless debugging
#   ./scripts/dev.sh connect [ip[:port]]    # connect; no args = find the phone via mDNS
set -euo pipefail

APP_ID="tech.unrealistic.cineflix"
ACTIVITY="$APP_ID/.MainActivity"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

# Prefer the SDK's platform-tools adb: the distro build is often compiled without
# mDNS support, which silently breaks wireless discovery. DEV_ADB overrides it.
resolve_adb() {
    local sdk candidate
    for sdk in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}"; do
        [[ -n "$sdk" && -x "$sdk/platform-tools/adb" ]] && candidate="$sdk/platform-tools/adb" && break
    done
    if [[ -z "${candidate:-}" && -f local.properties ]]; then
        sdk="$(sed -n 's/^sdk\.dir=//p' local.properties | tail -1)"
        [[ -n "$sdk" && -x "$sdk/platform-tools/adb" ]] && candidate="$sdk/platform-tools/adb"
    fi
    if [[ -z "${candidate:-}" && -x "$HOME/Android/Sdk/platform-tools/adb" ]]; then
        candidate="$HOME/Android/Sdk/platform-tools/adb"
    fi
    if [[ -z "${candidate:-}" ]]; then
        candidate="$(command -v adb || true)"
    fi
    [[ -n "$candidate" ]] || { echo "adb not found - install Android platform-tools" >&2; return 1; }
    printf '%s' "$candidate"
}

ADB="${DEV_ADB:-$(resolve_adb)}"
adb() { "$ADB" "$@"; }

usage() {
    sed -n '2,11p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
}

connection_help() {
    cat <<'EOF'
No device attached.

USB:
  1. Phone: Settings -> About phone -> tap "Build number" 7 times.
  2. Settings -> System -> Developer options -> enable "USB debugging".
  3. Plug the phone in, pick "File transfer" on the phone, and tap "Allow"
     on the "Allow USB debugging?" prompt.

Wireless (Android 11+), same Wi-Fi network:
  1. Developer options -> Wireless debugging -> Pair device with pairing code.
  2. adb pair <phone-ip>:<pair-port>     # then enter the 6-digit code
  3. adb connect <phone-ip>:<connect-port>
EOF
}

device_serial() {
    adb start-server >/dev/null 2>&1 || true
    local serial
    serial="$(adb devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')"
    if [[ -z "$serial" ]]; then
        connection_help >&2
        return 1
    fi
    printf '%s' "$serial"
}

launch() {
    adb shell am start -S -n "$ACTIVITY" \
        -a android.intent.action.MAIN \
        -c android.intent.category.LAUNCHER >/dev/null
    echo "Launched $APP_ID"
}

deploy() {
    local serial
    serial="$(device_serial)"
    export ANDROID_SERIAL="$serial"   # keep every later adb call on this device
    echo "Deploying to $serial ..."
    ./gradlew :app:installDebug
    adb logcat -c
    launch
}

stream_logs() {
    local serial
    serial="$(device_serial)"
    export ANDROID_SERIAL="$serial"
    echo "Streaming logcat for $APP_ID on $serial (Ctrl-C to stop) ..."
    while true; do
        local pid
        pid="$(adb shell pidof -s "$APP_ID" 2>/dev/null | tr -d '\r' || true)"
        if [[ -z "$pid" ]]; then
            sleep 1
            continue
        fi
        adb logcat --pid="$pid" -v color || true
        if ! adb shell true >/dev/null 2>&1; then
            echo "Device disconnected - waiting for it to come back ..." >&2
            sleep 2
        fi
        sleep 0.5
    done
}

watch() {
    local serial
    serial="$(device_serial)"
    echo "Watching app/src for changes on $serial (Ctrl-C to stop) ..."
    inotifywait -m -r -q \
        -e close_write,create,delete,move \
        --format '%w%f' \
        app/src/main/java app/src/main/res |
    while IFS= read -r changed; do
        # collapse bursts of saves into one rebuild
        while IFS= read -r -t 0.4 _; do :; done
        echo "Changed: $changed"
        if deploy; then
            echo "Deployed. Rebuilding on next save."
        else
            echo "Build failed - fix the error above; will retry on next save." >&2
        fi
    done
}

connect_to() {
    local target="${1:-}"
    if [[ -z "$target" ]]; then
        discover_and_connect
        return
    fi
    [[ "$target" == *:* ]] || target="$target:5555"
    # adb connect exits 0 even when it fails, so inspect its output instead.
    local out
    out="$(adb connect "$target" 2>&1)"
    echo "$out"
    if [[ "$out" != *"connected to"* ]]; then
        echo "Could not reach $target - check the IP/port and that the phone is on this network." >&2
        return 1
    fi
    adb devices -l
    if ! adb devices | awk -v t="$target" '$1 == t && $2 == "device" { found = 1 } END { exit !found }'; then
        echo "$target was accepted but is not ready - check the debugging prompt on the phone." >&2
        return 1
    fi
    echo "Ready - ./scripts/dev.sh now deploys over Wi-Fi ($target)."
}

discover_and_connect() {
    echo "Looking for a phone advertising wireless debugging ..."
    local addr
    local mdns_out
    mdns_out="$(adb mdns services 2>&1 || true)"
    if [[ "$mdns_out" == *"not supported"* ]]; then
        echo "$ADB has no mDNS support - connect by address instead." >&2
        echo "Use the SDK adb (\$ANDROID_HOME/platform-tools/adb) for auto-discovery." >&2
        return 1
    fi
    addr="$(printf '%s\n' "$mdns_out" |
        awk '$1 ~ /_adb-tls-connect\._tcp\.?$/ { print $NF; exit }')"
    if [[ -z "$addr" ]]; then
        cat >&2 <<'EOF'
No wireless-debugging device found on the local network.
  1. Phone: Developer options -> Wireless debugging -> On
  2. Same Wi-Fi network as this machine
  3. Or connect by address: ./scripts/dev.sh connect <phone-ip>:<port>
The port is shown on the Wireless debugging screen and changes on every reconnect.
EOF
        return 1
    fi
    connect_to "$addr"
}

pair_device() {
    local target="${1:-}" code="${2:-}"
    if [[ -z "$target" || -z "$code" ]]; then
        echo "Usage: ./scripts/dev.sh pair <phone-ip>:<pair-port> <6-digit-code>" >&2
        echo "Both values are on the 'Pair device with pairing code' dialog." >&2
        return 2
    fi
    if ! adb pair "$target" "$code"; then
        cat >&2 <<'EOF'
Pairing failed. The code and port expire quickly:
  - reopen "Pair device with pairing code" on the phone and use the new values
  - both devices must be on the same Wi-Fi network
EOF
        return 1
    fi
    echo "Paired. Next: ./scripts/dev.sh connect"
}

wireless_help() {
    cat <<'EOF'
Wireless debugging (Android 11+), phone and laptop on the same Wi-Fi:
  1. Phone: Developer options -> Wireless debugging -> On.
  2. Tap "Pair device with pairing code"; note the IP:port and the 6-digit code.
  3. ./scripts/dev.sh pair <ip>:<pair-port> <code>
  4. ./scripts/dev.sh connect                 # auto-discovers via mDNS
     or ./scripts/dev.sh connect <ip>:<port>   # port from the Wireless debugging screen
     (the pairing port and the connect port are different)

Older Android without the pairing flow (USB cable needed once):
  1. Plug in with USB debugging enabled.
  2. adb tcpip 5555
  3. Unplug, then: ./scripts/dev.sh connect <phone-ip>:5555

Once connected the phone behaves like a USB device: ./scripts/dev.sh, watch, logs.
EOF
}

case "${1:-run-and-logs}" in
    run-and-logs)
        deploy
        stream_logs
        ;;
    run)
        deploy
        ;;
    logs)
        stream_logs
        ;;
    watch)
        watch
        ;;
    devices)
        adb devices -l
        device_serial >/dev/null
        ;;
    wireless)
        wireless_help
        ;;
    pair)
        shift
        pair_device "$@"
        ;;
    connect)
        shift
        connect_to "$@"
        ;;
    -h|--help|help)
        usage
        ;;
    *)
        echo "Unknown command: $1" >&2
        usage >&2
        exit 2
        ;;
esac
