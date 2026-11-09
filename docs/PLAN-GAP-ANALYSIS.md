# Dossier v2.0 → current code: gap analysis

**Date:** 2026-11-08 · **Source:** `ARENA_COMPANION_DOSSIER.html` (7 parts, v2.0, 9 Nov 2026)
**Verdict:** the dossier is a **from-scratch build**, not a revision of what exists. It supersedes
the Pankxu roadmap (v4) wholesale.

---

## 1. What the dossier actually specifies

Three measurements decide everything, and they are all measurements I can independently confirm
from this project's own history:

| # | Measurement | My independent confirmation |
|---|---|---|
| 1 | `arena.ai/.well-known/assetlinks.json` → `{}` | **Confirmed early in this project** — no TWA, which is exactly why rc3 still shows a browser toolbar and why D-31 removed the probe |
| 2 | Google refuses OAuth in embedded WebViews (`403 disallowed_useragent`) | **Conflicts with the dossier's own P2 text** — see §5.1 below |
| 3 | Zero first-party API endpoints — companion, not client | **Confirmed by my live account audit** — the only API is the *developer* API (`api.arena.ai`), which is a model gateway, not arena.ai's product API |

Seven non-negotiables (R1–R7) map onto rules this project already follows: no token export
(R7), no scraping of authenticated pages (R1), own applicationId (R3).

**R6 is the one that changes the app's architecture:** auth happens only in a real browser.

---

## 2. Current state of the code

`arena-client` @ `eb42d33`, tag `v1.0.0-rc3`, signed and device-verified.

| Dimension | Now | Dossier requires | Δ |
|---|---|---|---|
| Application ID | `what.thefcuk.pankxu` | `com.<org>.arenacompanion` | **rewrite** |
| Language | 7 Java files | Kotlin | **rewrite** |
| UI | Activities + XML | Jetpack Compose + Material 3 | **rewrite** |
| Modules | 1 (`:app`) | trimmed nowinandroid graph (`core/*`, `feature/*`) | **rewrite** |
| DI | none | hand-written `AppContainer` (A1: Hilt rejected) | new |
| compileSdk | 36 | 37 | change |
| targetSdk | 36 | 36 | same |
| Tests | 25 JVM unit tests | NavigationPolicy ×30 + Roborazzi goldens + T1–T13 | **rewrite** |
| Screens | 3 (gate, About, Licences) | 6+ (Home, Compare, Reader, Settings, …) | **rewrite** |
| Navigation | intent to Custom Tab | Navigation Compose + deep links + predictive back | new |
| Release pipeline | ✅ CI, signed, v2+v3, fingerprint-pinned | same requirement | **reuse** |

**Nothing in the current source survives except as design precedent.** That is not a criticism of
the dossier — it is what "6 screens in Compose with a design system" means versus "a gate, an About
page and a licences page".

---

## 3. What transfers — and this is the expensive part, already paid

| Asset | Status | Reuse |
|---|---|---|
| **Release keystore** (`pankxu-release.jks`, RSA 4096 → 2054) | Restore-tested | When the new package ships to Play it needs **its own** key; this one can serve the sideload track meanwhile |
| **CI release pipeline** (tag → sign → verify v2+v3 → fingerprint assert → assets) | Green through rc1–rc3 | Copy the workflow, change the package |
| **Signing secrets in GitHub** | Set | Reusable verbatim |
| **Device access** (`scripts/device-tunnel.sh` + adb over SSH) | Working | Directly reusable for P2 "run against the real site on a physical device" |
| **`UrlPolicy` → `NavigationPolicy`** | 18 tests, port/non-ASCII rules | Concept and test cases port over; the dossier wants 30 |
| **Evidence discipline** (R13 crops, redaction, checksums) | Established | Becomes the T1–T13 evidence format |
| **Honest-fallback pattern** (D-31 gate notice) | Proven on device | Becomes P2's "this page isn't available in the app" state |

Reusing these is roughly the whole of P6 (release engineering) and half of P2's harness.

---

## 4. Environment reality check

| Requirement | Sandbox | Impact |
|---|---|---|
| RAM 16 GB | **1 GB** | **Local Gradle builds are not viable.** CI remains the build path — already proven through rc1–rc3 |
| Disk 25 GB | 19 GB free | Tight but workable for source; SDK not installed locally |
| JDK 17 | JDK **21** | AGP 9 accepts 21; no action |
| Node ≥22.12 | **20.20.2** | Only matters for `pptr_grab.mjs` (P8 capture loop), not for the app build |
| AGP 9.5.1 / Kotlin 2.4.21 / Compose BOM 2026.09.00 | all resolve | ✅ verified against Google + Maven Central |
| `/dev/kvm` for emulator | **absent** | No local emulator; API 36 testing must run on the remote device (Android 12 / API 32) — **not API 36**, so P5's predictive-back and edge-to-edge gates cannot be device-verified here |
| Android Studio | n/a | CI + `sdkmanager` only |

**The blocker that matters:** the dossier's P2/P5 exit criteria assume an **API 36 emulator**. This
sandbox has no `/dev/kvm`, and the only physical device available is **Android 12 (API 32)**. The
plan's instruction — *"Testing only on API 34 will let both bugs through"* — applies with even more
force to API 32. I cannot close T-BACK/T-EDGE as written. That is a documented limitation, not
something I can engineer around.

---

## 5. Conflicts found in the dossier

### 5.1 R6 contradicts Part 4's own framing

R6 says *"Auth happens only in a real browser (Custom Tabs)"*. Part 4 §A3 **Option B** then
describes in-app magic-link login inside the WebView, and §A5.5's intent filter is described as
"unverified" and "withdrawn". The dossier flags the withdrawal in Part 4's header — but Option B
remains in the body.

**Resolution I will apply:** R6 wins. Option B is not implemented. The H1+ partial Custom Tab
(Option A) is the only login path.

### 5.2 "0 first-party API endpoints" vs an existing developer API

The dossier was written from a HAR of the **consumer product**. Separately, this project audited
`platform.arena.ai` and found a full OpenAI-compatible gateway at `api.arena.ai`, with 29 models
and a live key.

This does **not** change the dossier's conclusion — that API is a *model inference gateway*, not
arena.ai's product API (no chat history, no votes, no leaderboards). R1 and R5 stand. But it is
worth recording, because it is the one place the dossier's "no API" claim is narrower than reality.

### 5.3 Play track conflicts with the private-sideload decision

Roadmap v4 chose **private sideload**. The dossier's P7 requires **Play closed testing with 12
testers for 14 continuous days**, and A5 makes the tester roster a day-1 deliverable because it is
the critical path (earliest production = start + 14 days).

Both cannot be true. Either the app ships to Play (and the 14-day clock must start the day the
first signed AAB exists), or it stays sideload-only and P7 is deleted.

---

## 6. What I need decided before writing code

| # | Decision | Why it blocks |
|---|---|---|
| **D1** | **Organisation name** for `com.<org>.arenacompanion` | R3 is a non-negotiable; the applicationId is immutable once published. Placeholder is fine for local builds, but never for a release |
| **D2** | **New repo `arena-companion`, or rewrite `arena-client` in place?** | Determines whether v1.0.0-rc3 keeps its history and its release tags |
| **D3** | **Play, or sideload?** | Starts or deletes a 14-day calendar dependency (§5.3) |
| **D4** | **API 36 verifier** — is there a second device/emulator, or do I record P5 as partially unverified? | I cannot close T-BACK/T-EDGE on API 32 |

---

## 7. What I can start immediately, without any of the above

- P0a: repo skeleton, `gradle/libs.versions.toml` with the three pinned-unpinned entries resolved
  against live release pages
- P0c: dependency verification + version pinning
- `docs/audit/` and `docs/design/` provenance folders (the dossier's §2.3 layout)
- Port `UrlPolicy` test cases into the `NavigationPolicy` contract — the tests are the spec, and
  they are independent of the package name
- CI workflow for the new package, modelled on the green rc3 pipeline

These are real, reversible, and need no decisions. Everything else waits.

---

## 8. Day-0 version resolution (dossier §2.4 instruction, executed)

The dossier said: *"Verify the three unpinned-ish entries (roborazzi, previewScanner, detekt)
against their release pages on day 0 and write the resolved versions back into this file. Never
leave a range."* Done. Four pins in the dossier were wrong:

| Entry | Dossier says | Reality | Resolved |
|---|---|---|---|
| `roborazzi` | `"1.5x"` | **No 1.5x line exists.** Latest is 1.78.0 | **1.78.0** |
| `previewScanner` | `"1.x"` | Not on Maven Central — **JitPack only** | **JitPack `master-SNAPSHOT`** ⚠️ or drop the dep |
| `detekt` | `"2.x"` | **No 2.x published.** Latest is 1.23.8 | **1.23.8** |
| `browser` | `"1.9.0"` | 1.10.0 is current; 1.9.0 exists | **1.9.0** (keep — stable, and 1.10.0 is new) |
| all others | as pinned | verified 200 OK | **unchanged** |

⚠️ **ComposablePreviewScanner is the one unresolved risk.** It resolves only from JitPack, and the
only published coordinates there are snapshot-style. Adding JitPack as a repository for a single
test helper widens the supply chain for a project whose P0c requirement is *"Gradle dependency
verification"*. **Recommendation: drop ComposablePreviewScanner and write the preview registry by
hand** (it is a handful of `@Preview` functions in a list). That keeps every dependency on Google
or Maven Central and makes verification metadata tractable.

Verified present (HTTP 200): `agp 9.5.1`, `kotlin 2.4.21`, `compose-bom 2026.09.00`,
`activity-compose 1.13.0`, `lifecycle 2.11.0`, `navigation-compose 2.10.2`, `core-ktx 1.19.1`,
`datastore 1.3.0`, `okhttp 5.5.1`, `browser 1.9.0`, `roborazzi 1.78.0`, `detekt 1.23.8`.
