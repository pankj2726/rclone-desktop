# Compatibility matrix

Legend: **Supported** = implemented and evidenced. **Partial** = works with a stated limit.
**Requires Arena** = needs Arena to publish something. **Not available** = by design.
**Unverified** = not yet tested; no claim made.

| Area | State | Basis / evidence |
|---|---|---|
| Android 8.0-15 (API 26-35) | Unverified | minSdk 26 declared; no device run yet |
| Android 16 (API 36) | Unverified | targetSdk 36; no device run yet |
| Chrome as the TWA provider | Unverified | library picks a TWA-capable browser; needs a device |
| No default browser / Chrome disabled | Partial | falls back to Custom Tab, then any browser via `ACTION_VIEW` (`FALLBACK_STRATEGY=customtabs`) |
| No browser at all | Not available | nothing can render the site; by design (no WebView fallback, D-01) |
| URL bar hidden (full-screen) | **Requires Arena** | assetlinks.json is `{}` (P1-03, 2026-11-08) |
| Verified `arena.ai` deep links (no chooser) | **Requires Arena** | needs an asset statement for our package + cert (D-07) |
| Agent mode | Supported (URL) / Unverified (page) | `https://arena.ai/agent`, robots.txt Allow, 2026-11-08 |
| Chat | Supported (URL) / Unverified (page) | `https://arena.ai/chat`, robots.txt |
| Side-by-side | Supported (URL) / Unverified (page) | `https://arena.ai/side-by-side`, robots.txt |
| Direct | Supported (URL) / Unverified (page) | `https://arena.ai/direct`, robots.txt |
| Text / Image / Search / Video / Code | Supported (URL) / Unverified (page) | robots.txt route list |
| Leaderboard | Supported (URL) / Unverified (page) | home-page href + robots.txt |
| **Battle** | **Not available** | no published route found; not invented (D-02) |
| Max | Supported (URL) | robots.txt |
| Google sign-in | Unverified | runs in the browser engine, which is what Google requires; not yet tested on device |
| Email OTP | Unverified | same |
| Session persistence | Unverified | browser-owned; force-stop/reboot test outstanding |
| File upload / camera / mic | Unverified | delegated to the browser; no app permissions requested |
| Downloads | Unverified | browser handles them |
| URL allow-list | **Supported** | `UrlPolicy` + 9 JVM unit tests passing 2026-11-08; manifest host filter |
| No runtime permissions | **Supported** | our own manifest declares none; merged manifest contains exactly one auto-generated signature-level permission from `androidx.startup`, verified in CI run 42488658446 (P6-07) |
| Cleartext refused | **Supported** | `network_security_config.xml` + `UrlPolicy` rejects `http://` |

Every "Supported" row above is either a code fact checked in this repo or a route checked
against arena.ai on 2026-11-08. No row claims a device test that was not run (R7).
