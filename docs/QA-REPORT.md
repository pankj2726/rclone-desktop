# QA report

**Date:** 2026-11-08
**Build under test:** none — no APK exists yet. This report is a framework with what is
actually known filled in, not a claim of passing tests.

## Why there is no device test

The agent's environment has no `/dev/kvm`, no virtualisation flag in `/proc/cpuinfo`, 1.4 GB
of RAM and no Android SDK. An emulator cannot run there. The user chose to test on their own
device; the checklist below is what they run.

## Automated evidence that does exist

| Check | Result | Evidence |
|---|---|---|
| `UrlPolicy` compiles clean | pass | `javac -Xlint:all -Werror`, 0 warnings |
| Allow-list unit tests | **pass, 9/9** | `JUnitCore … → OK (9 tests)` |
| URL lookalike attacks rejected | pass | `arena.ai.evil.com`, `notarena.ai`, `arena.ai@evil.com`, `?next=`, `#` |
| Non-https schemes rejected | pass | `http`, `intent`, `javascript`, `file`, `content`, `mailto`, `tel`, `data` |
| UTF-8 compile hazard found and fixed | pass | em dashes in comments failed under C locale; sources converted to ASCII, encoding pinned (D-09) |
| Gradle build | **not run** | no JDK 17 / SDK in sandbox; CI will be the first |
| Lint | **not run** | same |
| APK produced | **none** | same |
| Installed / launched | **not done** | no device |
| Google login, email OTP | **not done** | no device |

## Phase 4 checklist — for the user to fill in after installing

Answer Pass / Fail / Unverified. Attach screenshots where useful. Do not mark Pass without
having actually done it.

| # | Test | Result | Notes |
|---|---|---|---|
| 4-01 | App launches and the site loads. A URL bar is visible (expected — assetlinks is empty) | | |
| 4-02 | Google sign-in completes and returns to the chat | | |
| 4-03 | Email OTP: enter email → switch to mail app → return → enter code; the page is still waiting | | |
| 4-04 | Session survives force-stop, then reboot | | |
| 4-05 | Agent, Chat, Side-by-Side, Direct, Leaderboard each load | | |
| 4-06 | Upload an image and a PDF; camera and mic prompts appear | | |
| 4-07 | Download an image and an export | | |
| 4-08 | Back navigation sensible; rotation, split-screen, font scaling, dark mode | | |
| 4-09 | Offline shows a browser error, not a crash; slow network usable | | |
| 4-10 | Chrome disabled, or Samsung Internet / Firefox / Brave as default browser | | |
| 4-11 | Launch from a shortcut; open an arena.ai link from another app | | |
| 4-12 | (security) `adb shell am start -a android.intent.action.VIEW -d "https://evil.com" what.thefcuk.pankxu` does not load evil.com inside the app | | |

Row 4-12 is the P6-02 fuzz check in its simplest form; it should be refused by the allow-list.

---

## v3.1 additions to the device checklist

### Parity (PAR) — run these on the phone

| ID | Check | Result |
|---|---|---|
| PAR-01 | Battle: prompt, two streams, vote, identities appear, follow-up works | |
| PAR-02 | Side-by-Side: pick two models, both stream, independent follow-ups | |
| PAR-03 | Direct: pick a model, long chat survives backgrounding | |
| PAR-04 | Agent: multi-step task, file output, long session survives backgrounding | |
| PAR-05 | One smoke test per listed use case | |
| PAR-06 | All 14 leaderboard categories load; tables scroll; filters work | |
| PAR-07 | Google and email OTP; history list; recover history; sign out | |
| PAR-08 | Upload image and PDF; preview; download | |
| PAR-09 | Consent prompt appears once, dismissable; Terms/Privacy/Cookies open in the browser | |
| PAR-10 | Device language set to a non-English locale; site still works | |
| PAR-11 | Help links open in the external browser | |
| PAR-12 | Footer changelogs reachable | |

### Negative tests (X-31) — each one is Pass / Fail / Unverified

| ID | Scenario | Result |
|---|---|---|
| N-01 | Airplane mode at launch → Retry screen appears, not a blank page | |
| N-02 | Airplane mode during sign-in | |
| N-03 | Kill the browser while waiting for the OTP email | |
| N-04 | Low storage during a download | |
| N-05 | Battery saver and data saver on | |
| N-06 | VPN active, and private DNS set | |
| N-07 | Device without Google Play services | |
| N-08 | Android Go device | |
| N-09 | Work profile / secondary user (X-30) | |
| N-10 | Android 12+ unverified link opens in the browser, not the app (X-29) | |

### Security and stability

| ID | Check | Result |
|---|---|---|
| S-01 | Fuzz: `adb shell am start -a android.intent.action.VIEW -d "https://evil.com" what.thefcuk.pankxu` → nothing loads in-app | |
| S-02 | Fuzz: `intent://`, `javascript:`, `file://` variants → refused with a toast, no crash | |
| S-03 | `adb shell monkey -p what.thefcuk.pankxu -v 500` → no crash (X-28) | |
| S-04 | `adb shell pm get-app-links what.thefcuk.pankxu` → records the link-verification state (X-08) | |

---

## Merged manifest review, build 42499447209 (P6-07)

Extracted from the APK with `aapt2 dump xmltree`, not read from our source:

| Item | Value |
|---|---|
| Permissions | `ACCESS_NETWORK_STATE` (ours, normal-level) and `what.thefcuk.pankxu.debug.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (auto-generated, signature-level) |
| Activities | `LaunchGateActivity` exported; `PankxuLauncherActivity` not exported; `ShortcutTrampolineActivity` exported (library); `ColdShortcutActivity` not exported |
| Provider | `androidx.startup.InitializationProvider`, not exported |
| Receiver | `androidx.profileinstaller.ProfileInstallReceiver`, exported, system-permission protected |

No `INTERNET`, no camera, no storage, no location. Analysis and accepted risks:
`docs/THREAT-MODEL.md`.

## Dependency verification: negative test (R11)

Gradle dependency verification is committed at `gradle/verification-metadata.xml` (272 pinned
components, `verify-metadata=true`, no bypass mode). Because a verification file that is
silently ignored looks identical to one that works, it was tested by breaking it on purpose.

| Step | Build | Result |
|---|---|---|
| Metadata committed, build run unchanged | 42500268168 | **pass** |
| One SHA-256 (androidx.activity 1.7.0) edited to a wrong value, no other change | 42500469892 | **fail** — `Dependency verification failed for configuration ':app:debugCompileClasspath'`; `Caused by: DependencyVerificationException` |
| Corruption reverted | (final build, see PROGRESS) | expected pass |

So the file is load-bearing: an artifact whose bytes differ from the recorded digest stops the
build before anything is packaged. This is the only claim in this report that was tested by
deliberately making it fail.

## SBOM (P6-06)

`scripts/sbom.init.gradle` reads the resolved `debugRuntimeClasspath`, hashes each artifact file,
and `scripts/make-sbom.py` writes CycloneDX 1.5 (`sbom.cdx.json`, 48 components for this build).
The `build` workflow generates it on every push and uploads it with the artifact. Components
appear because Gradle resolved them and for no other reason; licence fields are intentionally
blank (see `THIRD-PARTY-NOTICES.md` for the reviewed list, D-22).

## Device pass — 2026-11-08

First execution on real hardware: Samsung SM-G9900 (Galaxy S21 FE), Android 12 / SDK 32. Full
method and index in `docs/qa-evidence/device-2026-11-08/README.md`.

### Three defects that only a device could find

| # | Defect | How it showed up | Fix |
|---|---|---|---|
| 1 | `ManageDataLauncherActivity` not declared (androidbrowserhelper 2.7.4 requires the app to declare it, and does not declare it itself) | Every launch that reached the TWA crashed: `IllegalArgumentException ... does not exist in what.thefcuk.pankxu.debug` | Declared it `exported=false`; added `scripts/check-manifest.py` to CI (D-23) |
| 2 | Gate text was white on a white background in light mode | Screenshot showed an empty screen while `uiautomator dump` listed the text; `Theme.Material` is the *dark* parent paired with a white background | Light parent in `values/`, dark kept in `values-night/` (D-24) |
| 3 | Shortcuts hard-coded `targetPackage="what.thefcuk.pankxu"` | Debug build: `ActivityNotFoundException: Shortcut could not be started`; the fix's first attempt (omitting the package) produced a chooser instead | Per-build-type override + `scripts/check-shortcuts.py` on the packaged APK (D-26) |

Defect 1 shipped in `v1.0.0-debug` and `v1.0.0-unsigned`. Every previous "not yet verified"
statement was accurate; this is what verification buys.

### What now passes on the device

| Item | Result | Evidence |
|---|---|---|
| Install and first launch | Pass | `adb install` of a CI artifact, 701 ms cold start, no crash |
| Research disclosure (X-03) | Pass | shown once, dismissable, contains the Arena FAQ wording |
| Unofficial-client notice (X-02) | Pass | footer of the gate |
| In-app routing of `arena.ai` | Pass | web content served by `mark.via` Custom Tab |
| External routing (PAR-11) | Pass | `help.arena.ai`, `accounts.google.com`, lookalikes leave the app |
| Blocked schemes (N-01..N-07) | Pass | `intent:`, `file:`, `tel:`, `mailto:`, `data:`, userinfo-bearing, cleartext |
| Shortcuts | Pass | all four registered with the correct component |
| No-TWA fallback notice (D-25) | Pass | names the fallback and continues deliberately |

### What is still unverified

* Real TWA behaviour — the device has no Chrome and cannot install it (0 Google accounts).
* Offline pre-check — the simulator would not drop its network.
* Google and email-OTP sign-in (PAR-07) — no account on the device.
* Sending an actual prompt (PAR-01, PAR-03, PAR-08) — deliberately not done.
* Link verification — `pm get-app-links` reports `arena.ai` **Disabled**, as expected without an
  `assetlinks.json` statement; `autoVerify` stays off.

## S2-05 accessibility and locale pass — 2026-11-08, device D1

Run on build `pankxu-1.0.0-debug-v40.apk`
(`e25d7da45c614f8e26b71bcef85ea7daf1528d9b58d602e18f471315d79d0ee5`).

| Check | Result | Evidence |
|---|---|---|
| Largest system font (1.5x) | **Pass** — all five gate elements still laid out, nothing clipped, buttons still tappable | `28-large-font-1.5x.png` |
| Dark mode (`cmd uimode night yes`) | **Pass** — black background, light text, both buttons legible | `29-dark-mode.png` |
| Light mode | **Pass** — this is the D-24 fix, regression-checked here | `25-gate-with-about.png` |
| Non-English locale (device is zh-HK) | **Pass for layout, app strings remain English** — no truncation or overlap observed; the app deliberately ships English-only for v1.0.0 | all captures |
| TalkBack | **Unverified** — not exercised; would need enabling the screen reader and walking the gate | — |
| RTL (ar-EG) | **Unverified** — Android 12 has no `wm set-locale`, and `debug.force_rtl` cannot be set on a production build, so this device cannot force an RTL layout. `supportsRtl="true"` is declared and the layouts use start/end-safe attributes, but that is design evidence, not a test | — |

Both failures in this area would be release blockers if they had occurred; neither did.
