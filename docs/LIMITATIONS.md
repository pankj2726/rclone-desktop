# Limitations

Known and deliberate, not defects.

1. **A URL bar is always visible.** arena.ai serves `{}` at `/.well-known/assetlinks.json`
   (2026-11-08), so no app can hold a verified association. Only Arena can fix this, by
   publishing an asset statement with our package name and certificate fingerprint.
2. **Battle mode has no shortcut.** No `/battle` route is published in robots.txt and no link
   to one exists on the home page (D-02). Nothing was invented.
3. **Session lives in the browser, not the app.** Signing out of the app means signing out in
   the browser. Clearing app data does not sign you out; clearing Chrome data does.
4. **The app is a launcher, not a client.** No offline mode, no notifications, no background
   work, no share-target, no API calls.
5. **Chrome (or another TWA-capable browser) must be installed.** Without one, the app falls
   back to a Custom Tab, and then to the default browser via `ACTION_VIEW`. If no browser
   exists at all, there is nothing to show and the app cannot function — by design, since the
   alternative would be an embedded WebView, which Google blocks for sign-in.
6. **No in-app "About" or settings screen** (D-06). Version and links live in these docs.
7. **Untested on any device by the agent.** See `QA-REPORT.md`.
