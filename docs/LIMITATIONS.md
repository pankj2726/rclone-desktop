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
8. **The SBOM is generated per build, not republished.** `sbom.cdx.json` is uploaded with each
   `build` run; the file on a release page can lag behind `main`. Regenerate rather than trust
   an old copy.
9. **Automated checks against arena.ai are unreliable by design.** Arena blocks automated
    requests, so the canary reports Unknown rather than Failed. Site verification is the
    human capture in `docs/site-capture/`, not CI.
10. **The app shows a URL bar that disappears on third-party sign-in.** Leaving the Arena
    origin (Google sign-in, help centre, Terms) surfaces the browser toolbar briefly. That is
    Chrome's designed behaviour for an unverified origin, not a defect (X-12).
11. **No notifications.** Out of scope for v1 (X-13).
12. **No in-app settings, Help or About screen beyond the launch gate.** The gate carries the
    research disclosure and the unofficial-client notice; everything else lives in these docs.
