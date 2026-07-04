#!/usr/bin/env bash
# Capture Paradise Android TV screenshots via adb (emulator-5554)
set -euo pipefail

DEVICE="${ADB_DEVICE:-emulator-5554}"
PKG="com.halil.ozel.movieparadise"
MAIN="${PKG}/.ui.main.MainActivity"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="${ROOT}/screenshots"

DPAD_UP=19; DPAD_DOWN=20; DPAD_LEFT=21; DPAD_RIGHT=22; DPAD_CENTER=23; KEY_BACK=4

adb_cmd() { adb -s "$DEVICE" "$@"; }
key() { adb_cmd shell input keyevent "$1"; sleep "${2:-0.3}"; }
shot() { echo "📸 $1"; adb_cmd exec-out screencap -p > "$OUT_DIR/$1"; sleep 1; }

launch_home() {
  adb_cmd shell am force-stop "$PKG" || true
  sleep 1
  adb_cmd shell am start -n "$MAIN"
  sleep 6
}

open_search() {
  launch_home
  for _ in 1 2 3; do key $DPAD_LEFT 0.2; done
  for _ in 1 2 3 4 5 6; do key $DPAD_UP 0.2; done
  key $DPAD_CENTER 0.5
  sleep 2
}

mkdir -p "$OUT_DIR"
echo "🚀 Capturing screenshots on $DEVICE"

launch_home
shot "home.png"
key $DPAD_RIGHT 0.4; key $DPAD_RIGHT 0.4
shot "nowplaying_focus.png"

go_sidebar_row() {
  for _ in 1 2 3; do key $DPAD_LEFT 0.25; done
  for ((i=0; i<$1; i++)); do key $DPAD_DOWN 0.4; done
  sleep 1.5
}
go_sidebar_row 1 && shot "toprated.png"
go_sidebar_row 1 && shot "popular.png"
go_sidebar_row 1 && shot "upcoming.png"

launch_home
key $DPAD_RIGHT 0.4; key $DPAD_RIGHT 0.4; key $DPAD_CENTER 0.5
sleep 8
shot "detail.png"

for _ in 1 2 3 4 5; do key $DPAD_DOWN 0.55; done
shot "detail_area.png"
for _ in 1 2 3 4 5 6; do key $DPAD_DOWN 0.6; done
shot "detail_cast.png"
key $DPAD_DOWN 0.6; key $DPAD_DOWN 0.6; key $DPAD_RIGHT 0.5
shot "detail_recommend.png"

open_search
shot "search.png"
key $DPAD_RIGHT 0.3; key $DPAD_CENTER 0.3
for _ in $(seq 1 15); do key 67 0.05; done
adb_cmd shell input text "spider"
sleep 7
shot "search_result.png"
key $KEY_BACK 0.4; key $DPAD_DOWN 0.4; key $DPAD_RIGHT 0.4; key $DPAD_CENTER 0.5
sleep 5
shot "search_result_detail.png"

launch_home
shot "androidtv.png"
cp "${ROOT}/app/src/main/res/drawable/banner.png" "$OUT_DIR/paradise_banner.png"

echo "✅ Done → $OUT_DIR"
