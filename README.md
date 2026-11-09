# Arena Companion

Unofficial Android companion for arena.ai. Not affiliated with Arena.

Built to the ARENA_COMPANION_DOSSIER (v2.0).

## Status

- ✅ `NavigationPolicy` — 36 JVM tests, all green (R1 scope + R6 auth routing)
- ✅ Gradle/AGP/Compose skeleton, `applicationId com.pankxu.arenacompanion`
- ⬜ WebView host + hardening (P2)
- ⬜ Custom Tabs sign-in (P2, H1+ partial sheet)
- ⬜ Compare Tray / Reader (P4)

## Non-negotiables

R1 no arena API paths · R2 Inter only, no arena brand · R3 own applicationId ·
R4 zero analytics SDKs · R5 no API dependency · R6 auth in a real browser only ·
R7 no session import, ever.

## Build

No local Android SDK on this machine (1 GB RAM); builds run in CI.
