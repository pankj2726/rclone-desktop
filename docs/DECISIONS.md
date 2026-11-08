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
