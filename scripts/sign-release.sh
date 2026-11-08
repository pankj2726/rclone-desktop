#!/usr/bin/env bash
# Local release signing, run by the key holder.
#
# R3: this script never prints, echoes or logs any secret. It reads the four
# signing values from the environment, builds, and verifies. If any is missing it
# stops before touching the keystore.
#
#   export PANKXU_KEYSTORE_PATH=~/keys/pankxu-release.jks
#   export PANKXU_KEYSTORE_PASSWORD='…'
#   export PANKXU_KEY_ALIAS='release'
#   export PANKXU_KEY_PASSWORD='…'
#   ./scripts/sign-release.sh
set -euo pipefail
cd "$(dirname "$0")/.."

for v in PANKXU_KEYSTORE_PATH PANKXU_KEYSTORE_PASSWORD PANKXU_KEY_ALIAS PANKXU_KEY_PASSWORD; do
    if [ -z "${!v:-}" ]; then
        echo "missing environment variable: $v" >&2
        exit 1
    fi
done
if [ ! -f "$PANKXU_KEYSTORE_PATH" ]; then
    echo "keystore not found at the configured path" >&2
    exit 1
fi

./gradlew --no-daemon clean :app:assembleRelease

APK=$(ls app/build/outputs/apk/release/*.apk | head -1)
BT="${ANDROID_HOME:-$HOME/Android/Sdk}/build-tools/36.0.0"

mkdir -p artifacts
cp "$APK" artifacts/pankxu-release.apk
(cd artifacts && sha256sum pankxu-release.apk > SHA256SUMS.txt)

echo "== zipalign =="
"$BT/zipalign" -c -P 16 -v 4 artifacts/pankxu-release.apk | tail -2
echo "== apksigner =="
"$BT/apksigner" verify --verbose --print-certs artifacts/pankxu-release.apk | tee artifacts/certificate.txt
echo "== badging =="
"$BT/aapt2" dump badging artifacts/pankxu-release.apk | head -5

# P2-03: only the certificate SHA-256 digest is recorded, never the key.
echo "== certificate digest (safe to publish) =="
grep -i 'SHA-256 digest' artifacts/certificate.txt | tee artifacts/certificate-fingerprint.txt
