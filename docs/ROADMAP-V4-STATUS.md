# Roadmap v4 status

Last updated 2026-11-08. Every row states what is true now, not what is planned.
Owner letters: **A** agent, **U** user, **T** human tester.

## Stage 0 — freeze and fix foundations

| ID | Task | Owner | State | Where |
|---|---|---|---|---|
| S0-01 | Final package id and app name | U | **Blocked — needs your decision** | Three options in section 9 below; nothing changes until you pick |
| S0-02 | Release keystore, backup, restore test, fingerprint | U | **Blocked — only you can generate and hold it** | `docs/KEY-MANAGEMENT.md` has the exact commands and the empty results table |
| S0-03 | One shared debug keystore so CI builds upgrade | A | **Done** | `ci/debug.keystore`, wired into the `debug` build type, D-28. Proven: two consecutive installs of the same build upgraded in place with no uninstall, `firstInstallTime` unchanged |
| S0-04 | Re-baseline the compatibility matrix from device evidence | A | **Done** | `docs/COMPATIBILITY.md`; 5 Pass, 1 Partial, 6 Unverified, each with an evidence path |
| S0-05 | Redact the evidence folder (R13) | A | **Done** | Status and navigation bars cropped from all 34 captures; `SHA256SUMS` regenerated; `superseded/WHY.md` records what was dropped and why |
| S0-06 | Source safety: pushed, bundle, PROGRESS current | A | **Done** | Bundle `artifacts/repo-20261108T185106Z.bundle`, HEAD pushed; PROGRESS has entries per stage |
| S0-07 | CI test additions | A | **Partial** | (b) shortcut targets for both build types: **done**, 4 tests. (c) theme/contrast: **done** as a static pairing test that fails on the old white-on-white bug. (a) emulator smoke test: **not possible** — this environment has no `/dev/kvm`, so no emulator can run here; belongs with S1-01 on your machine. (d) URL fuzzing: **done** as the 18-case policy suite plus 7 on-device cases |
| S0-08 | URL allow-list hardening | A | **Done** | 18 unit cases; port rule added (non-443 is now EXTERNAL); non-ASCII hosts are BLOCK because `java.net.URI` returns a null host. D-29 |
| S0-09 | Does an official Arena Android app exist? | A | **Done — no** | D-27. No stop condition. Re-check at the 30-day review |

**Gate G-S0** — not met yet, and only because of S0-01 and S0-02, which are yours.

## Stage 1 — Chrome and the TWA path

| ID | Task | Owner | State |
|---|---|---|---|
| S1-01 | Chrome-equipped target | U | **Blocked.** The test phone has no Chrome and no Google account; Play cannot install it. Options in section 9 |
| S1-02 | Manual TWA checklist | T | Blocked on S1-01 |
| S1-03 | Full-screen preview with the test flag | T | Blocked on S1-01 |
| S1-04 | TWA vs fallback differences in LIMITATIONS | A | **Partly done** — the Via/WebView path is documented, the TWA side cannot be described until it is observed |
| S1-05 | Google and email-OTP sign-in | T | Blocked on S1-01 |

## Stage 2 — feature completion

| ID | Task | Owner | State |
|---|---|---|---|
| S2-01 | Offline pre-check with Retry | A, T | **Coded, unverified.** The simulator would not drop its network (Wi-Fi off and airplane mode both left `ping 8.8.8.8` succeeding), so the screen never armed. Needs a phone that really loses connectivity |
| S2-02 | No-TWA and old-engine notice | A | **Half done.** The no-TWA notice works on the device (screenshot `10`, `13`, `25`). The old-WebView warning is not written yet — see the note below |
| S2-03 | Shortcuts for the modes you choose | A | **Blocked on your choice.** Four are wired (`/agent`, `/chat`, `/side-by-side`, `/leaderboard`) and all four load; swap them when you decide |
| S2-04 | Help and About screen | A | **Done** | `AboutActivity`, `LicensesActivity`, diagnostics export. Verified on the device: `26-about-screen.png`, `27-licences.png`, chooser opens with the report text |
| S2-05 | Localisation and accessibility | A, T | **Partial** | 1.5x font and dark mode **Pass** on the device; TalkBack and RTL **Unverified** (Android 12 has no `wm set-locale`, and `debug.force_rtl` cannot be set on a production build) |
| S2-06 | Go/no-go on optional modules | U | **Blocked on you.** Default of no-go for v1.0.0 stands unless you say otherwise |
| S2-07 | Refresh the human site capture | U | **Blocked.** Still the only sanctioned way to confirm the entry URLs (D-19) |

### Why S2-02 is only half done

The notice appears when no browser declares the TWA category. Warning about an *old* WebView
is a different test: the engine behind a Custom Tab is the provider's, and this device's
system WebView is 95. Reading the version is possible, but a threshold would be a guess:
Chrome's in-app support floor moves, and Arena's own minimum is not published. Rather than
ship a nag with an invented cutoff, the honest version is to state the risk in the About
screen and LIMITATIONS, which is already there. Say the word if you want a threshold anyway
and I will wire it to a documented Chrome release rather than a number I choose.

## Stages 3 to 7

Blocked behind S0-01, S0-02 and S1-01 in that order. Nothing in Stage 4 can start without a
release key, and nothing in Stage 5 counts toward delivery without a release APK on a real
device (R14).
