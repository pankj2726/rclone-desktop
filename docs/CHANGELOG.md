# Changelog

## 1.0.0 — unreleased

Initial build.

* Trusted Web Activity around arena.ai, Custom Tabs fallback, WebView fallback disabled.
* Default entry `https://arena.ai/agent`.
* Four launcher shortcuts: Agent, Chat, Compare (side-by-side), Rankings (leaderboard).
* URL allow-list restricted to `https://arena.ai`, enforced in code (`UrlPolicy`) and in the
  manifest intent filters; 9 unit tests.
* No permissions, `allowBackup=false`, cleartext traffic refused.
* Original adaptive icon with a monochrome layer.
* CI: build/test/lint on push, signed release on tag, weekly canary on Arena's public surface.
