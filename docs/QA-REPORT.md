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
