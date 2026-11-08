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
