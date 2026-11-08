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
