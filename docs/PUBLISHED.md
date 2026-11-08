# Published artefacts

**Repository:** https://github.com/pankj2726/rclone-desktop (private)

---

## 1. `v1.0.0-unsigned` — release build, not signed

The R8-minified release variant, published so the exact artefact that needs signing is available.

| Property | Value |
|---|---|
| Asset | `pankxu-release-unsigned.apk` — **196 920 bytes** |
| SHA-256 | `1b950b1a5aa65d0e31bbf7847d81b1eaaac405cb070ab647050554d14e4ef2ee` |
| Package / version | `what.thefcuk.pankxu` · versionCode 10000 · versionName 1.0.0 |
| SDK | minSdk 26 · targetSdk 36 · compileSdk 36 |
| Signature | **none** — no APK Signing Block, no META-INF certs |
| Build | CI run 42489486512, `release-unsigned.yml`, JDK 17 / AGP 9.5.1 / Gradle 9.6.0 |

Download verified independently: pulled back from GitHub, SHA-256 matched the published
`SHA256SUMS.txt` (`OK`), and the APK was confirmed unsigned.

### Why it is unsigned

The release key belongs to the app's owner and never enters CI (R3). CI will not fabricate a
signing key, because a build signed with anything else cannot be upgraded to your key later
without an uninstall.

### Shrinking was checked, not assumed

Debug → release: 4 906 811 → 196 920 bytes; 402 → 59 entries. Aggressive, so the survival of
behaviour was verified rather than trusted:

| Must survive | Result |
|---|---|
| `PankxuLauncherActivity` in the dex | present |
| `androidbrowserhelper` launcher + `TwaLauncher` | present |
| `arena.ai` allow-list host | present |
| `CustomTabsService` action string (fallback path) | present |
| TWA metadata keys (`DEFAULT_URL`, `FALLBACK_STRATEGY`) | present |
| Shortcut URLs `agent`, `chat`, `side-by-side`, `leaderboard` | present |
| `cleartextTrafficPermitted` (network security config body) | present |

`UrlPolicy` and `CustomTabsIntent` do not appear by name in the minified dex — R8 merged them.
Their behaviour is present (the host string and the service action are both there). Verified by
string presence, not by decompilation, so this is strong evidence rather than proof; a device
run settles it (QA row 4-12 exercises the allow-list directly).

### Signing it

```bash
zipalign -p -f 4 pankxu-release-unsigned.apk pankxu-release.apk
apksigner sign --ks release.jks --ks-key-alias release \
  --v1-signing-enabled false --v2-signing-enabled true --v3-signing-enabled true \
  --out pankxu-release-signed.apk pankxu-release.apk
apksigner verify --print-certs pankxu-release-signed.apk
```

Or add the four repository secrets and run the `release` workflow to do it in CI.

---

## 2. `v1.0.0-debug` — installable now, for the device pass only

| Property | Value |
|---|---|
| Asset | `pankxu-1.0.0-debug.apk` — 4 906 811 bytes |
| SHA-256 | `e185a41f987da7c7e2f7ce07709d10e98714e8eaacdbeac9bb11d75b6dbbe673` |
| Signature | APK Signing Block, v2, **debug key** |

Install it, run `docs/QA-REPORT.md`, then **uninstall before installing a release-signed build** —
different signing key, same package id, so Android will refuse the update otherwise.

---

## What was not published

**Google Play.** Not attempted, and deliberately:

* Distribution choice in the project decisions is private sideload.
* Play requires a developer account, a Data Safety form, a content rating and a public privacy
  policy URL.
* A Play release of a wrapper around somebody else's website runs straight into Google's
  **repackaged-website policy**.
* Arena's Terms make distribution of a third-party product using their marks a permission
  question, and `docs/arena-permission-request.md` has not been sent.

Publishing to Play without Arena's written answer would be the single riskiest thing this project
could do, and it is not reversible once a listing is public.
