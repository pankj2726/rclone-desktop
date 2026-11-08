# 2026-11-08 — agent-side fetch (superseded, retained for the record)

**Status: this is no longer how site facts are gathered.** Roadmap v3.1 (9.1 item 1, 9.2,
9.4) established that arena.ai blocks automated fetching and that the agent must not attempt
it. Recorded here so the provenance of what was collected before that rule is not lost (R5).

What was fetched once each, before the rule, and stored in `docs/research/`:

| Resource | Result |
|---|---|
| `https://arena.ai/` | HTTP 200, 124 082 B, title "Arena AI: The Official AI Ranking & LLM Leaderboard" |
| `/.well-known/assetlinks.json` | HTTP 200, body `{}` — no asset statements |
| `/manifest.webmanifest` | HTTP 200 but `text/html` (soft-404): **no web manifest** |
| `/robots.txt` | HTTP 200, declared route list used for entry URLs |

**Caveats now attached to all of it:**

* It was a scripted fetch. A scripted 200 today may be a challenge page tomorrow; treat the
  content as a snapshot, not a live guarantee.
* It says nothing about what a *human* sees — no capture of the cookie prompt, the consent
  banner, the Installability panel, or locale redirects.
* `Battle` has no `/battle` route in that snapshot, which remains unresolved.
* The header/CSP observations are from that snapshot; re-check from a real browser.

Everything below this line is superseded by a real capture in this folder.
