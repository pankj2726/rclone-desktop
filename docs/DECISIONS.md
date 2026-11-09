# Decisions

R12: anything outside the roadmap, or any locked decision that changed, is recorded here
before work starts.

| id | Decision | Reason | Status |
|---|---|---|---|
| D-01 | TWA + Custom Tabs fallback; `FALLBACK_STRATEGY=customtabs`, never `webview` | Google blocks sign-in in embedded WebViews; the library's own docs list hardened `WebViewFallbackActivity`, but we do not need it and its Terms exposure is worse | locked, implemented |
| D-02 | **Battle is not shipped as a shortcut or an entry URL** | No `/battle` route in arena.ai/robots.txt and no href to one on the home page (P1-04). R5 forbids inventing a URL | open, needs user |
| D-03 | Bar/splash colour is theme-following (`#FFFFFF` day, `#000000` night) rather than Arena's exact background | arena.ai publishes no `theme-color` meta (P1-01) and ships both light and dark apple-touch-icons, so no single colour is correct | implemented, may be revised once someone sees it on a device |
| D-04 | **Java instead of Kotlin** for the two source files | Roadmap locked Kotlin, but the app has one activity override and one pure class. Adding KGP introduces a second version matrix (KGP x Gradle 9.6 x AGP 9.5) into a build nobody can run locally. Removes a whole class of first-CI failure | deviation, R12 recorded |
| D-05 | Package id `what.thefcuk.pankxu`, display name "Pankxu" | Supplied by the user. Display name is one string and can change; the package id cannot | locked |
| D-06 | P3-09 Help/About screen deferred | The TWA renders Chrome's UI; there is no in-app surface for a menu without adding a second launcher entry or a WebView, both of which are worse. Legal and privacy docs ship as files instead | deferred |
| D-07 | `autoVerify` left off | arena.ai serves `{}` at /.well-known/assetlinks.json, so verification cannot succeed. Enabling it now would only produce failed-verification noise | implemented |
| D-08 | Only `arena.ai` is allow-listed, no subdomains | No subdomain has been confirmed to us as an entry point. Conservative by design | implemented |
| D-09 | Java sources compile as UTF-8 explicitly | A real failure caught while testing: em dashes in comments broke `javac` under a C locale | implemented |
| D-10 | `lint { abortOnError = false }` for the first CI run | The project has never been built by Gradle. Turning lint into a hard gate before the first successful compile makes no sense; flip it once green | temporary |

| D-11 | Default branch is `main`, and the bundle is built from it | Found by cloning the bundle: `git clone` of a `master` repo produced `master`, while every workflow triggers on `main`. A push would have created a default branch no CI listens to | implemented |
| D-12 | `artifacts/*.bundle` and `artifacts/*.zip` are git-ignored | Committing a bundle puts the previous bundle inside the new one; the repository grows recursively on every commit. `artifacts/` is snapshotted, `dist`/`build`/`out` are not, so they belong on disk and out of history | implemented |

| D-13 | CI uses the runner's preinstalled Android SDK; `android-actions/setup-android` removed | First CI run (42488344472) failed inside that action: it runs `sdkmanager tools`, and the `tools` package no longer exists, so it exits 1 before any of our steps. Fewer third-party actions is also better for R11 | implemented |

| D-14 | `androidx.core` removed from dependencies | CI run 42488423911 failed at `:app:checkDebugAarMetadata`: `androidx.core:core:1.19.1` requires compileSdk 37, and nothing in this app imports androidx.core at all. Removing an unused dependency beats raising compileSdk for no reason. Its transitive copy still arrives via androidbrowserhelper | implemented |

| D-15 | **Roadmap v3.1 section 9 supersedes earlier sections.** Allow-list is now three-way (IN_APP / EXTERNAL / BLOCK); a LaunchGateActivity owns the exported surface | 9.2 row 5 requires help.arena.ai, Terms and Privacy to open externally. The library's default would have silently redirected an untrusted URL to DEFAULT_URL, i.e. a Terms link would land the reader on the agent page | implemented |
| D-16 | The TWA activity is no longer exported; the gate is the only entry | Keeps every inbound intent behind one validated path, and makes P3-12 and X-03 possible in one place | implemented |
| D-17 | `ACCESS_NETWORK_STATE` added | P3-12 requires an offline pre-check. Normal-level, auto-granted, cannot read user data. Reverses the earlier "zero permissions" claim, which is now stated precisely instead | implemented |
| D-18 | Canary records blocked/challenged as **Unknown**, runs monthly | 9.2: bot challenges must not raise false alarms. Monthly matches the roadmap's human monthly capture better than weekly | implemented |
| D-19 | No scripted fetching of arena.ai from now on | 9.1 item 1 and 9.4. Earlier fetches succeeded (recorded 2026-11-08 in docs/research/) but repeated automated access is exactly what the site blocks and the Terms prohibit. Site facts now come from user capture only | implemented |
| D-20 | `LICENSE` is MIT for this repo's code only | X-01. Explicitly grants nothing in Arena's marks or content | implemented |

## D-21 — Dependency verification must be generated over the whole task set, not just `dependencies`

`--write-verification-metadata` only records what the tasks in that invocation actually
resolve. Generating it over `:app:dependencies help` leaves out the artifacts that `lintDebug`
and `testDebugUnitTest` pull, so `gradle/verification-metadata.xml` would pass generation and
then fail the next real build with "dependency verification failed". The generator therefore
runs the same three tasks as `build.yml` (`assembleDebug`, `testDebugUnitTest`, `lintDebug`)
and then re-runs them with the file in place. If the re-run fails, the file is discarded and
`docs/LIMITATIONS.md` keeps the item open — a verification file that breaks the build is worse
than none, because the next person deletes it under pressure.

## D-22 — The SBOM is generated from the resolved classpath, never hand-written

`scripts/sbom.init.gradle` reads `debugRuntimeClasspath`, hashes each artifact file, and
`scripts/make-sbom.py` turns those rows into CycloneDX 1.5. Components that did not actually
resolve cannot appear, and licence fields are left empty on purpose: the reviewed list is
`THIRD-PARTY-NOTICES.md`, and inventing SPDX identifiers from memory would be the same class
of error as writing verification checksums by hand.

**Outcome:** run 42500068102 generated 272 components and the confirm step passed; the file was
committed in `9fdf48d` and build 42500268168 stayed green. A deliberate checksum corruption in
`79fc7da` failed run 42500469892 with `DependencyVerificationException`, which is the evidence
that the file is enforced rather than ignored; the corruption was reverted in `19af292`.

## D-23 — Declare `ManageDataLauncherActivity`, keep `LauncherActivity`, and assert the manifest in CI

**The bug.** The first device run crashed before showing anything:

```
java.lang.RuntimeException: Unable to start activity ... PankxuLauncherActivity:
  java.lang.IllegalArgumentException: Component class
  com.google.androidbrowserhelper.trusted.ManageDataLauncherActivity does not exist
      at LauncherActivity.launchTwa(LauncherActivity.java:264)
```

Unpacking the published 2.7.4 AAR shows the class is present in `classes.jar` but the
library's own `AndroidManifest.xml` declares only `ShortcutTrampolineActivity` and
`ColdShortcutActivity`. `LauncherActivity.launchTwa()` builds a `ComponentName` for
`ManageDataLauncherActivity` and requires it to resolve, so the declaring app has to add it.
Compilation, lint, unit tests, the SBOM and dependency verification all pass without it —
they cannot see a component that only matters at runtime.

**Decision.** Declare the activity, `android:exported="false"`, with no intent filter and no
`MANAGE_SPACE_URL` metadata. Declaring it is the documented contract; the app never launches
it. Migration to the `TwaLauncher` API (which does not need it) is rejected for now: it moves
splash-screen and fallback handling into our code, which is a larger change than the bug
warrants, and D-16 wants the maintained fallback chain.

**Guard.** `scripts/check-manifest.py` asserts the four required activities and their export
states plus the permission set, and fails the build if any of them drifts. Run against the
manifest from the crashing build it fails with exactly the missing component; the shipped
build passes. Runtime-only contracts now have a build-time test.

**Honest note.** This bug existed in every release so far, including `v1.0.0-debug` and
`v1.0.0-unsigned`. None of them would have launched. The docs claiming "built, not yet
verified" were accurate, and this is what verification buys.

## D-24 — Light and dark themes must pair with light and dark bar colours

`Theme.Pankxu` inherited `@android:style/Theme.Material.NoActionBar`, which is the **dark**
platform theme, while `@color/bar` is `#FFFFFFFF` in light mode. The result on a device was a
white-on-white gate: the title, the status text and the unofficial-client notice were all
present in the view tree and invisible on screen. The parent is now `Theme.Material.Light`
in `values/` and stays `Theme.Material` in `values-night/`, matching the colour split that
already existed. Lesson recorded because the failure is silent to lint: the layout is valid,
the strings resolve, and only a screenshot catches it.

## D-25 — Say when the TWA is not a TWA

Without a browser declaring `androidx.browser.trusted.category.TrustedWebActivities`, the
library degrades to a Custom Tab in whatever browser is installed. That is a reasonable
fallback, but it is not the product described in the README, and the user had no way to know
why arena.ai opened in a tab with a URL bar. `TwaProviderCheck` asks the PackageManager; if no
provider is present the gate explains the difference and offers "Continue in browser". The
check runs on every launch, so installing Chrome later takes effect without clearing data.
Declared in `docs/LIMITATIONS.md` rather than hidden.

## D-26 — Shortcut targets are per build type, and checked against the packaged APK

`android:targetPackage` must name the applicationId of the build that ships the file. Two
failures were observed on the device, in this order:

1. hard-coded to the release id -> `ActivityNotFoundException: Shortcut could not be started`
   on the debug build;
2. omitted -> the launcher drops the component, the intent becomes implicit and a chooser
   opens instead of the app.

So `src/main` carries `what.thefcuk.pankxu`, `src/debug` overrides the same file with
`what.thefcuk.pankxu.debug`, and `scripts/check-shortcuts.py` asserts the value inside the
built APK equals the APK's own package name. A resource-file rule that neither unit tests nor
lint can see is now a build failure.

## D-27 — No official Arena Android app exists (S0-09, checked 2026-11-08)

Checked Play Store listings and Arena's public help pages on 2026-11-08. There is no
first-party Arena / LMArena Android application. The Play listings that match the word
"Arena" are unrelated products (an NHL venue app and an image-generator by a different
developer), and the public help pages still describe browser access only. **No stop
condition triggered**; the launcher remains a legitimate personal-use wrapper.

Re-check at the 30-day and 90-day reviews in section 8 of the roadmap, and re-run this task
immediately if Arena announces one.

## D-28 — One committed debug keystore, because CI was minting a new key per run

Consecutive CI builds could not upgrade each other: each ephemeral runner generated its own
debug key, so `adb install -r` failed with `INSTALL_FAILED_UPDATE_INCOMPATIBLE` and every
device test began with an uninstall that wiped app data - which also reset the first-run
disclosure, making the first-run path untestable in isolation.

`ci/debug.keystore` is now committed and wired into the `debug` build type. This is a
deliberate, documented exception:

* debug keys are not secrets - the password is the Android convention (`android`), and the
  certificate is valid only for builds marked debuggable, which are never shipped;
* the **release** key remains environment-variable-only and is never in the repository;
* `.gitleaks.toml` allowlists exactly the keystore path and the two convention passwords, so a
  real secret anywhere else still fails the secret scan.

Fingerprint of the shared debug certificate (SHA-256):
`8B:C6:CD:ED:D8:D0:5C:C5:D7:49:A6:71:C7:D7:18:F3:6A:8A:44:51:31:F6:6A:93:F7:E0:DD:35:91:20:D4:E4`

This is not the release fingerprint and must never be published as one.

## D-29 — S0-08 allow-list hardening: what changed and what the parser split means

Added to `UrlPolicy` and its tests (18 cases, all passing on a JVM):

* **Any port other than 443 is EXTERNAL.** `https://arena.ai:8443/` is not the published
  entry point, and allowing the app to frame arbitrary services on a trusted host is an
  allowance that ages badly.
* Uppercase scheme and host, a single trailing root dot, and the explicit default port stay
  IN_APP (already covered, now pinned by tests).
* Backslash tricks, userinfo in any position, look-alikes (`arena.ai.evil.example`,
  `evilarena.ai`, `arena-ais.example` style), percent-encoded dots, double trailing dots and a
  leading dot are all kept out of IN_APP.
* **Non-ASCII hosts are BLOCKed, not EXTERNAL.** `java.net.URI` returns `null` from
  `getHost()` for a non-ASCII authority, so the policy cannot name the host and refuses.
  Android's `Uri.parse()` is lenient and does not agree - which is why the same inputs are
  also exercised on the device in the S0-08 device run, rather than trusting the unit test to
  describe runtime behaviour. The two results are recorded together; if they ever diverge,
  the device wins and the policy gets an explicit punycode/IDN check.

## D-30 — The TWA-capability check asked about the wrong component type

`TwaProviderCheck` originally queried *services* for
`androidx.browser.trusted.category.TrustedWebActivities`. Installing Chrome 156 on the test
device and reading its manifest showed the category on
`org.chromium.chrome.browser.browserservices.ManageTrustedWebActivityDataActivity`, an
**activity**. The service query therefore always came back empty, and the gate told the user
"Chrome is not installed" on a device where Chrome was installed and signed in. It now queries
activities.

Two things worth keeping:

1. **A capability check is not a promise.** Even with Chrome present, arena.ai publishes no
   `assetlinks.json` for this package, so the browser shows its toolbar anyway - what the
   device produced was a Custom Tab inside `com.android.chrome`, not a Trusted Web Activity.
   The notice now says what is actually true ("Arena has not published a link-verification
   file, so the toolbar stays visible") instead of blaming the device.
2. **The earlier pass could not have caught this.** With no TWA-capable browser installed, a
   permanently-empty query and a correct answer look identical. Only installing Chrome
   separated them. The same trap applies to any check that is trivially true in the test
   environment.

## D-31 — No TWA-capability probe; state the limitation instead

A second attempt at detecting whether the device can host a Trusted Web Activity - this time
querying *activities* for `androidx.browser.trusted.category.TrustedWebActivities` - also
returned nothing on a device with Chrome 156 installed. The cause is Android 11+ package
visibility: an app cannot enumerate another package's components unless its manifest declares
a matching `<queries>` entry, and matching Chrome's internal intent filter would mean guessing
at Chromium's action name. Guessing wrong produces a confident, false message, which is worse
than no message.

So the probe is gone. The gate states the one thing that is true on every device today: Arena
has not verified this app, so the browser toolbar stays visible. When Arena publishes
`assetlinks.json` and `autoVerify` can be switched on (S3-02), this notice becomes false and
must be removed at the same time - noted here so the two changes stay together.

Diagnostics no longer reports a browser capability either, for the same reason: it would have
said "none" on a device with Chrome.
