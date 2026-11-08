# Installing

## 1. Get the APK

From a GitHub release (if CI is set up), or `artifacts/pankxu-release.apk` if you signed
locally.

## 2. Verify it before installing

```bash
sha256sum -c SHA256SUMS.txt
```

Expect `pankxu-release.apk: OK`. If it does not match, stop — do not install.

Check the signer (optional, needs Android build-tools):

```bash
apksigner verify --print-certs pankxu-release.apk
```

The SHA-256 digest must equal the one recorded in `artifacts/certificate-fingerprint.txt`.

## 3. Install

**Over USB:**

```bash
adb install -r pankxu-release.apk
```

**On the phone:** copy the APK over, tap it, allow "Install unknown apps" for the app
doing the installing (Files, Chrome, etc.), confirm the Play Protect prompt
("Send app for scanning?" → either choice is fine), then Open.

Play Protect will warn about an unknown developer. That is expected for a sideloaded
app signed with a key Google has never seen.

## 4. First run

The app opens `https://arena.ai/agent` in a Chrome-backed window.

**A URL bar will be visible.** This is expected and not a bug: arena.ai publishes an
empty `/.well-known/assetlinks.json`, so no app can claim a verified association with
the domain. Full-screen operation needs Arena to publish an asset statement for this
package and signing certificate — see `arena-permission-request.md`.

## 5. Uninstall

Settings → Apps → Pankxu → Uninstall, or `adb uninstall what.thefcuk.pankxu`.

Nothing is stored on the device by the app. Clearing the app's data does not sign you
out of arena.ai, because the session lives in the browser, not in this app.

## Updating

Install a newer APK over the old one **only if it is signed with the same key**.
Same key: the update installs and your shortcuts are preserved. Different key: Android
refuses with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, and you must uninstall first, which
loses nothing here.
