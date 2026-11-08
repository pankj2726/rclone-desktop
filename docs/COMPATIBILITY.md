# Compatibility matrix

Legend: **Supported** = implemented and evidenced. **Partial** = works with a stated limit.
**Requires Arena** = needs Arena to publish something. **Not available** = by design.
**Unverified** = not yet tested; no claim made.

| Area | State | Basis / evidence |
|---|---|---|
| Android 8.0-15 (API 26-35) | Unverified | minSdk 26 declared; no device run yet |
| Android 16 (API 36) | Unverified | targetSdk 36; no device run yet |
| Chrome as the TWA provider | Unverified | library picks a TWA-capable browser; needs a device |
| No default browser / Chrome disabled | Partial | falls back to Custom Tab, then any browser via `ACTION_VIEW` (`FALLBACK_STRATEGY=customtabs`) |
| No browser at all | Not available | nothing can render the site; by design (no WebView fallback, D-01) |
| URL bar hidden (full-screen) | **Requires Arena** | assetlinks.json is `{}` (P1-03, 2026-11-08) |
| Verified `arena.ai` deep links (no chooser) | **Requires Arena** | needs an asset statement for our package + cert (D-07) |
| Agent mode | Supported (URL) / Unverified (page) | `https://arena.ai/agent`, robots.txt Allow, 2026-11-08 |
| Chat | Supported (URL) / Unverified (page) | `https://arena.ai/chat`, robots.txt |
| Side-by-side | Supported (URL) / Unverified (page) | `https://arena.ai/side-by-side`, robots.txt |
| Direct | Supported (URL) / Unverified (page) | `https://arena.ai/direct`, robots.txt |
| Text / Image / Search / Video / Code | Supported (URL) / Unverified (page) | robots.txt route list |
| Leaderboard | Supported (URL) / Unverified (page) | home-page href + robots.txt |
| **Battle** | **Not available** | no published route found; not invented (D-02) |
| Max | Supported (URL) | robots.txt |
| Google sign-in | Unverified | runs in the browser engine, which is what Google requires; not yet tested on device |
| Email OTP | Unverified | same |
| Session persistence | Unverified | browser-owned; force-stop/reboot test outstanding |
| File upload / camera / mic | Unverified | delegated to the browser; no app permissions requested |
| Downloads | Unverified | browser handles them |
| URL allow-list | **Supported** | `UrlPolicy` + 9 JVM unit tests passing 2026-11-08; manifest host filter |
| No runtime permissions | **Supported** | our own manifest declares none; merged manifest contains exactly one auto-generated signature-level permission from `androidx.startup`, verified in CI run 42488658446 (P6-07) |
| Cleartext refused | **Supported** | `network_security_config.xml` + `UrlPolicy` rejects `http://` |

Every "Supported" row above is either a code fact checked in this repo or a route checked
against arena.ai on 2026-11-08. No row claims a device test that was not run (R7).

---

## Arena parity checklist (v3.1 section 9.3)

Every row is **Unverified**: no device has run this build. Nothing here is marked Pass without
evidence, per R7. Fill these in from the device pass; the evidence goes in `docs/qa-evidence/`.

| ID | Area | What is known | Status |
|---|---|---|---|
| PAR-01 | Battle | Two anonymous responses, vote, identities revealed afterwards (third-party source, unconfirmed) | Unverified |
| PAR-02 | Side-by-Side | **Pass** (2026-11-08) | `https://arena.ai/side-by-side` opens the Side-by-Side workspace with two model pickers (`gemini-4-argon`, `gemini-3.8-flash`). `docs/qa-evidence/device-2026-11-08/22-side-by-side.png`. Voting not exercised. |
| PAR-03 | Direct | One chosen model, no voting, prompts collected for research (Arena FAQ) | Unverified |
| PAR-04 | Agent | **Pass** (2026-11-08) | `/agent` loads "Agent Mode | Autonomous AI Agents for Real-World Tasks" inside the app. Evidence `05-twa-v32.png`, `06-arena-loaded.png`. |
| PAR-05 | Use cases | **Partial** (2026-11-08) | Home page shows the "Get started" cards (landing page, dashboard, game) and the prompt box; the full use-case list was not walked. |
| PAR-06 | Leaderboards | **Pass** (2026-11-08) | `/leaderboard` loads "Arena Leaderboard: Official AI Mode" with Start Voting and Agent / Chat / Code / Image tabs. `22-leaderboard.png`. The full 14-category list was not enumerated. |
| PAR-07 | Account | Google or email OTP sign-up; history and recovery | Unverified |
| PAR-08 | Uploads and media | File and image input; image and video output | Unverified |
| PAR-09 | Legal and consent | **Pass** (2026-11-08) | Cookie consent appears on first load and is dismissable; the page renders normally afterwards. `19-sidebyside.png` (prompt) and `20-cookies-accepted.png` (after). Terms and Privacy pages themselves not opened. |
| PAR-10 | Localization | Locale-prefixed paths exist (e.g. `/de/faq`) | Unverified |
| PAR-11 | Help | **Pass** (2026-11-08) | Non-`arena.ai` https opens the system chooser or the installed browser instead of loading in-app; measured in `18-negative-matrix.txt` (control row: two ACTIVITY STARTs). |
| PAR-12 | Changes over time | Changelogs listed in the site footer (to confirm) | Unverified |

### Routing behaviour that *is* testable without a device

| Behaviour | Status | Evidence |
|---|---|---|
| `https://arena.ai/...` loads in the TWA | **Supported** | `UrlPolicyTest`, 10/10 passing 2026-11-08 |
| `https://help.arena.ai/...` opens in the external browser | **Supported** | same tests, `EXTERNAL` decision |
| Google sign-in origin opens in the browser | **Supported** | same tests |
| `http://`, `intent:`, `javascript:`, `file:`, `content:`, `data:`, `tel:`, `mailto:` refused | **Supported** | same tests, `BLOCK` decision |
| Lookalike hosts never load in-app | **Supported** | `arena.ai.evil.com`, `notarena.ai`, `arena.ai@evil.com` all `EXTERNAL` or `BLOCK` |
| Offline shows a Retry screen | **Unverified** — coded and shipped, but this simulator would not go offline (Wi-Fi disabled and airplane mode on, `ping 8.8.8.8` still succeeded), so the screen never armed. See `docs/qa-evidence/device-2026-11-08/superseded/WHY.md` |
| Blocked schemes show a toast and load nothing | **Pass** (2026-11-08) | seven schemes/URLs, each producing exactly one ACTIVITY START; `18-negative-matrix.txt`, `07-block-toast.png` |
| Shortcuts open the app | **Pass** (2026-11-08) | "Open Agent" launches the gate, then arena.ai; `16-shortcut-disclosure.png`, `17-shortcut-arena.png` |

## S0-04 re-baseline, 2026-11-08

Every row above was re-derived from the device run rather than from intent. Rules used:

* **Pass** only where a screenshot or a log from device D1 shows the behaviour.
* **Partial** where part of the row was observed and part was not.
* **Unverified** where the row needs sign-in, a prompt submission, offline control, or Chrome -
  none of which device D1 can provide.
* Nothing is marked Pass on unit-test evidence alone; the unit tests are cited as design
  evidence, not as device evidence.

Device D1 column, summary: PAR-02, PAR-04, PAR-06, PAR-09, PAR-11 Pass; PAR-05 Partial;
PAR-01, PAR-03, PAR-07, PAR-08, PAR-10, PAR-12 Unverified. P4 rows (URL bar, splash, app
identity, back from the TWA) remain Unverified because no TWA provider exists on D1.
