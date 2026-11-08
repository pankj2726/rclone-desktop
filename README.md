# Pankxu

> **Unofficial client.** Not affiliated with, endorsed by, or supported by Arena. This is a
> personal launcher that opens arena.ai in your browser engine.
>
> **Install only from this repository's [Releases](../../releases) page.** Third-party APK
> mirrors and unrelated store listings using similar names are not from this project. Every
> release publishes the APK's SHA-256 **and** the signing certificate's SHA-256 fingerprint —
> verify both before installing (`docs/INSTALL.md`). A build whose certificate does not match
> the published fingerprint is not ours.
>
> **What Arena collects:** Arena's FAQ states that Side-by-Side and Direct prompts are
> collected for research, that those modes do not count as leaderboard votes, and that shared
> data is de-identified. Read `docs/PRIVACY.md` before using real content.

A thin Android container around **arena.ai**, so the site opens as a dedicated app.

Trusted Web Activity (TWA) with a Custom Tabs fallback. No WebView, no injected script,
no scraping, no credentials handled by the app. The browser engine does the rendering,
the login and the file handling; this app only supplies an icon, a launcher entry, four
shortcuts and a URL allow-list.

* Package id: `what.thefcuk.pankxu` (permanent)
* Version: 1.0.0 (versionCode 10000)
* minSdk 26, compileSdk/targetSdk 36
* No runtime or dangerous permissions. The merged manifest contains one auto-generated
  **signature-level** permission (`…DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, injected by
  `androidx.startup`); it is granted only to this app, cannot touch device data, and is not
  something a user can be prompted for. Verified in CI from the merged manifest (P6-07).

## Status — read this first

| Area | State |
|---|---|
| Source, build files, CI workflows, docs | **written** |
| Allow-list logic + unit tests | **passing** — 9/9 on a JVM, run 2026-11-08 |
| Full Gradle/Android build | **not yet run** — no JDK 17 + SDK here; CI runs it |
| Installed on a device | **not done** — no device or emulator available to the agent |
| Google login / email OTP verified | **not done** — same reason |
| Signed with your key | **not done** — requires your keystore |

Nothing in the table above is a claim that it works. See `docs/QA-REPORT.md` for the
evidence split and `docs/PROGRESS.md` for what is next.

## Layout

```
app/                     Android module (one activity, one policy class)
  src/main/java/…        PankxuLauncherActivity, UrlPolicy
  src/test/java/…        UrlPolicyTest (JVM, no emulator needed)
  src/main/res/…         icons, shortcuts, theme, network config
gradle/libs.versions.toml  every version pinned (R11)
.github/workflows/       build, release, weekly canary
scripts/                 make-bundle.sh, sign-release.sh
docs/                    research, decisions, QA, limitations, legal notes
artifacts/               git bundles and build outputs go here
```

## Quick start

```bash
./gradlew :app:assembleDebug          # debug build, unsigned
./gradlew :app:testDebugUnitTest      # allow-list tests
./scripts/sign-release.sh             # release signing, needs env vars (see BUILD.md)
```

Full instructions: `docs/BUILD.md` (build), `docs/INSTALL.md` (install).
