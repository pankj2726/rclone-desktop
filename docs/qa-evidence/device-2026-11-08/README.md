# Device evidence — 2026-11-08

First time this app has run on a physical device. Everything in this folder was produced by
`adb` against a live handset; nothing here is a mock-up or a reconstruction.

## Device

| | |
|---|---|
| Model | Samsung SM-G9900 (Galaxy S21 FE), `r9qzc` |
| Android | 12, SDK 32, security patch 2022-06-01 |
| ABI | arm64-v8a |
| Screen | 1080x1920, density 320 |
| Locale | zh-HK |
| WebView | `com.android.webview` 95.0.4638.74 (SDK 32 uses the standalone provider) |
| Browsers | **no Chrome**, no Samsung Internet; `mark.via` (Custom Tabs provider) and `org.chromium.webview_shell` only |
| Google accounts | **0** — Play Store is installed but cannot sign in, so Chrome cannot be installed from it |

The absence of Chrome is what makes this device interesting: it exercises the
"no Trusted Web Activity provider" path that the docs previously described only in theory.

## Builds under test

| Build | SHA-256 | What it fixed |
|---|---|---|
| v31 (`cb4c36a`) | `d486a67c…1abfcea0` | first run of the v3.1 gate — **crashed**, see `03-crash-stacktrace.txt` |
| v32 (`a7d7689`) | `c75f2694…37252371` | declared `ManageDataLauncherActivity` — launches, reaches arena.ai |
| v33 (`da4e957`) | `7abf1884…dc1a6e9` | names the Custom Tab fallback instead of degrading silently |
| v34 (`2ca68ae`) | `5108bceb…52794dc7` | light theme parent — the gate was white-on-white |
| v36 (`24719b8`) | `5895069b…2f15c67` | per-build-type shortcut targets |
| v40 (`7fc0d38`+) | `e25d7da4…d79d0ee5` | About and licences screens, shared debug key, accessibility checks |

Every build was installed over `adb install` of a CI artifact, uninstalled first where the
debug signing key differed (CI generates a fresh debug keystore per run, so signatures do
not carry over between runs).

## Index

| File | Shows |
|---|---|
| `01-gate-first-run.png` | first-run research disclosure, before consent |
| `03-crash-stacktrace.txt` | the launch crash: missing `ManageDataLauncherActivity` |
| `04-first-run-v32.png` | disclosure again after the fix |
| `05-twa-v32.png`, `06-arena-loaded.png` | arena.ai loading through the Custom Tab path |
| `07-block-toast.png` | `http://` refused: "That link is not supported.", activity finished |
| `10-no-twa-notice.png` | **the white-on-white bug**, text present in the view tree but invisible |
| `13-gate-legible.png` | the same screen after the theme fix |
| `15-shortcut-tap-result.png` | `ActivityNotFoundException: Shortcut could not be started` |
| `16-shortcut-disclosure.png`, `17-shortcut-arena.png` | the shortcut path working after the fix |
| `18-negative-matrix.txt` | seven blocked schemes/URLs, measured by counting `ACTIVITY START` events |
| `21-chat.png` | `/chat` loaded: "Get started" cards and the prompt box |
| `22-side-by-side.png` | `/side-by-side` loaded with two model pickers (PAR-02) |
| `22-leaderboard.png` | `/leaderboard` loaded: "Arena Leaderboard: Official AI Mode" (PAR-06) |
| `20-cookies-accepted.png` | cookie consent dismissed (PAR-09) |
| `25-gate-with-about.png` | gate after the About button was added |
| `26-about-screen.png` | About: version, unofficial notice, research disclosure, four actions |
| `27-licences.png` | bundled open-source notices |
| `28-large-font-1.5x.png` | gate at the largest system font: nothing clipped |
| `29-dark-mode.png` | gate in dark mode: black background, legible text |
| `superseded/` | blank or misleading captures, with a note saying why each was dropped |

Status and navigation bars have been cropped from every capture (R13). The checksums in
`SHA256SUMS` are of the cropped files and were regenerated after the crop.

## How the measurements were taken

* Launch: `adb shell am start -W -n what.thefcuk.pankxu.debug/what.thefcuk.pankxu.LaunchGateActivity`
* Inbound URL: same, with `-a android.intent.action.VIEW -d <url>`
* Screen: `adb exec-out screencap -p`
* Text: `adb shell uiautomator dump` (so the screenshot is not the only record)
* Routing: count `ActivityTaskManager: START` lines in `logcat` for three seconds after launch.
  One START means the gate handled the URL and finished; two means it forwarded it. A known
  external URL was run as a control and produced exactly two.
* Crashes: `adb logcat | grep -c 'FATAL EXCEPTION'`

## What this device could not test

* **Chrome / real TWA.** Not installed, and not installable without a Google account on this
  image. The TWA-specific behaviour (no URL bar, splash screen, app identity) remains
  unverified — see `docs/LIMITATIONS.md`.
* **Offline pre-check.** Wi-Fi and mobile data were disabled, then airplane mode enabled; the
  simulator kept a live network (`ping 8.8.8.8` succeeded throughout), so the Retry screen
  never armed. Marked Unverified rather than claimed.
* **Sign-in.** Zero Google accounts, and email-OTP needs an inbox. Both untested.
* Anything requiring a prompt to be sent — no test prompts were submitted, per X-03.
