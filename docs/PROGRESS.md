# Progress log

Updated at the end of every phase (R2). Newest at the top.

## 2026-11-08 — v3.1 build published, SBOM and verification metadata closed

* **New APK built from the v3.1 sources.** CI run 42499683401 (commit cb4c36a) then
  42500268168. Artifact `app-debug.apk`, 4,916,825 bytes, SHA-256
  `d486a67c1fc87d8ed06f462761da11dc764553d6bbba9241ab9899531abfcea0`, v2 signature.
  Delivered as `delivery/pankxu-1.0.0-debug-v31.apk` and as an extra asset on the
  `v1.0.0-debug` release. **This is the build the device pass must use**; the older
  `pankxu-1.0.0-debug.apk` predates the launch gate and cannot run the PAR tests.
* **gitleaks fix.** Run 42499631142 failed at the secret scan with `stderr is not empty` — the
  action cannot scan a shallow clone. `fetch-depth: 0` added; green since.
* **SBOM (P6-06).** `scripts/sbom.init.gradle` + `scripts/make-sbom.py` produce CycloneDX 1.5
  from the resolved debug runtime classpath with per-artifact SHA-256. 48 components. Uploaded
  with every build.
* **R11 dependency verification closed.** `verification-metadata.yml` run 42500068102 produced
  272 pinned components; committed; build green (42500268168); a deliberate checksum
  corruption failed the build (42500469892) and was reverted (19af292).
* **D-21, D-22** recorded. `docs/LIMITATIONS.md` items for SBOM and verification metadata
  rewritten to what is actually true.

## 2026-11-08 — roadmap v3.1 reconciliation

Section 9 supersedes earlier sections, so the app and the paperwork were reworked against it:

* **P3-06 rewritten.** Three-way routing (`IN_APP` / `EXTERNAL` / `BLOCK`). Terms, help and
  Google sign-in open in the browser instead of silently falling back to DEFAULT_URL.
  10 unit tests, passing on a JVM.
* **P3-12 / X-10 added.** `LaunchGateActivity` is the sole exported entry: connectivity
  pre-check with Retry, one-time research disclosure, then routing.
* **X-02 / X-03 added.** Unofficial-client notice and Arena's research-collection disclosure,
  in the app and in the README.
* **P6-03 rewritten.** PRIVACY.md now states what Arena collects about prompts, not just what
  the app does not.
* **P7-04 corrected.** Canary records blocked/challenged responses as Unknown and runs monthly.
* **X-21 added.** gitleaks runs first in every build.
* **X-01 / X-24 added.** LICENSE (MIT, code only), THIRD-PARTY-NOTICES.md, SECURITY.md.
* **X-17 / X-18 / X-19 written.** docs/KEY-MANAGEMENT.md with a backup-restore test and a
  v3-lineage rotation procedure.
* **9.3 / 9.12 tracked.** PAR-01..12 and N-01..N-10 exist as Unverified rows; status summary
  in docs/DOD-V31.md.
* **D-19.** No further scripted fetches of arena.ai. Site facts come from user capture only.

**CI:** build run 42499447209 green with the new gate activity, the first-run dialog and
gitleaks. Merged manifest reviewed component by component (docs/THREAT-MODEL.md).

## 2026-11-08 — artefacts published

* **`v1.0.0-unsigned`** — R8 release build, 196 920 bytes, unsigned by design, SHA-256 verified
  after round-tripping through GitHub. Full details in `docs/PUBLISHED.md`.
* **`v1.0.0-debug`** — installable debug build for the Phase 4 device pass.
* Signed release still blocked on the owner's keystore. Four repository secrets required.
* Google Play: not attempted, reasons recorded in `docs/PUBLISHED.md`.

## 2026-11-08 — repository published, first CI runs

**Published:** https://github.com/pankj2726/rclone-desktop (private, branch `main`).

CI history so far, each failure fixed rather than worked around:

| run | failure | fix |
|---|---|---|
| 42488344472 | `android-actions/setup-android@v3` exits 1: `Failed to find package 'tools'` | use the runner's preinstalled SDK (D-13) |
| 42488423911 | `checkDebugAarMetadata`: androidx.core 1.19.1 needs compileSdk 37 | dependency was unused; removed (D-14) |
| 42488658446 | **none — success** | Gate G0 met |

### Gate G0 — MET (run 42488658446)

Green from a clean runner: unit tests, lint, `assembleDebug`, merged-manifest report, artifact upload.
Evidence pulled back and re-checked locally:

* `app-debug.apk` — 4 906 811 bytes, 402 entries
* SHA-256 `e185a41f987da7c7e2f7ce07709d10e98714e8eaacdbeac9bb11d75b6dbbe673`
* APK Signing Block present, **v2 scheme** (debug key) — no v1/JAR signature needed at minSdk 26
* launcher activity `what.thefcuk.pankxu.PankxuLauncherActivity` present in the binary manifest
* merged manifest holds exactly one permission: the auto-generated signature-level
  `…DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` from androidx.startup — not a device capability

APK copied to `artifacts/pankxu-1.0.0-debug.apk` for the device pass.

## 2026-11-08 — Phases 0-3 (partial), Phase 7 workflows, docs

**Done**

* P0-01/02 — environment and reachability recorded; all 7 toolchain hosts reachable.
* P0-05 — repo, `.gitignore` (keystores, local.properties, build outputs), `scripts/make-bundle.sh`; bundles written under `artifacts/`.
* P0-06 — `docs/research.md` with sources and dates.
* P1-01..04 — home page, manifest (none), assetlinks (`{}`), entry URLs from robots.txt.
* P1-05 — Terms clauses extracted, `docs/legal-notes.md`.
* P2-01/02 — package id and versioning locked; original vector icon with monochrome layer.
* P3-01..08, P3-10 — Gradle project, pinned versions, manifest, allow-list, shortcuts, theme, network config.
* P3-06/P3-11 — `UrlPolicy` + 9 unit tests, **compiled and run on a real JVM: 9/9 pass**.
* P7-01/02/04 — CI workflows written (build, release, canary).

**Evidence**

* `docs/research/` — raw headers, robots.txt, assetlinks body, saved home page.
* Unit test run: `javac -Xlint:all -Werror` clean, `JUnitCore` → `OK (9 tests)`.

**Not verified / blocked**

* No Gradle build has run. This sandbox has JDK 11, no Android SDK, 1.4 GB RAM, and no
  `/dev/kvm`. Gate G0 is therefore **not met** — the CI path was chosen instead (user
  decision), so G0 moves to "first green CI run".
* No device, no emulator → **Gate G3 and all of Phase 4 are outstanding**. User will test
  on their phone.
* Phase 2 Gate G2 incomplete: no keystore exists yet, so no fingerprint to record.

**Next step**

1. User creates the GitHub repo and pushes; first CI run becomes the real Gate G0.
2. Fix whatever the first build reports.
3. User installs the debug APK and answers the Phase 4 checklist in `docs/QA-REPORT.md`.
4. Keystore generated by the user → record fingerprint → G2.
