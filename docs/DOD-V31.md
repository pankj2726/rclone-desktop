# Definition of done — v3.1 additions (section 9.12)

Each line is the actual state, not the intended state.

| Requirement | State | Evidence |
|---|---|---|
| Human-in-the-loop capture files saved with dates (9.4) | **Not done** | `docs/site-capture/` has the template, the instructions and the superseded 2026-11-08 agent fetch. No human capture yet. |
| PAR-01..12 marked Pass / Fail / Unverified | **Done — all Unverified** | `docs/COMPATIBILITY.md` parity table; nothing has been run on a device |
| Privacy disclosure and unofficial-client notice visible in app and README | **Done in code** | `LaunchGateActivity` first-run dialog; `unofficial_notice` on the gate screen; README banner. Compiled and lint-clean in CI, never seen on a device. |
| Key backup restore test done and fingerprint published (X-17, X-19) | **Blocked** | no keystore exists yet; `docs/KEY-MANAGEMENT.md` has the procedure and an empty results table |
| Key rotation and incident plans written (X-18, X-23) | **Done** | `docs/KEY-MANAGEMENT.md` (rotation with v3 lineage), `docs/LEGAL-NOTES.md` (incident plan) |
| Secret scan clean | **Done** | gitleaks runs as the first step of `build`; green in run 42499447209 |
| SBOM and licence notices included | **Partial** | `THIRD-PARTY-NOTICES.md` written and `dependency-tree.txt` attached to releases. **No CycloneDX SBOM yet** — recorded in `docs/LIMITATIONS.md` |
| Link verification state recorded (9.7) | **Done — unverified origin** | `docs/LINK-VERIFICATION.md`; `assetlinks.json` is `{}` as captured 2026-11-08. Device state to be recorded from `pm get-app-links` |
| Negative test list (X-31) run or marked Unverified with reasons | **Marked Unverified** | `docs/QA-REPORT.md`, N-01..N-10 |

## What v3.1 changed in the shipping app

1. **New exported entry point** — `LaunchGateActivity` does the connectivity check (P3-12),
   the one-time research disclosure (X-03) and the URL routing, before anything loads.
2. **Three-way URL policy** — `IN_APP` / `EXTERNAL` / `BLOCK`. Terms, help and Google sign-in
   now open in the user's browser instead of silently redirecting to the agent page. 10 unit
   tests, passing.
3. **The TWA activity is no longer exported.** Only the gate is.
4. **One permission added** — `ACCESS_NETWORK_STATE`, normal-level, for the offline check.
   The earlier "no permissions at all" claim is corrected everywhere it appeared.
5. **Canary reports Unknown, not Failed** — a bot challenge is not an outage.
6. **gitleaks** runs before every build.
7. **No more scripted fetches of arena.ai** (D-19).

## Still blocked on you

1. **Human site capture** (`docs/site-capture/TEMPLATE.md`) — this is the only way the entry
   URLs and manifest status get confirmed, and it may correct the four shortcuts.
2. **The release key** — then G2, G8 and Definition of Done items 1-2.
3. **The device pass** — Phase 4, PAR-01..12, N-01..N-10 — then items 3-4.
