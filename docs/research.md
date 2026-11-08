# Research — Phase 0 & Phase 1

All fetches are single, read-only requests against public static files, per R4.
Recorded 2026-11-08. No page was fetched twice, no content was scraped, no login or
cookie was used.

---

## Phase 0 — Environment (P0-01) and connectivity (P0-02)

| Item | Value |
|---|---|
| OS | Debian GNU/Linux 13 (trixie), kernel 6.1.189+, x86_64 |
| CPU | 2 cores |
| RAM | 1.9 GiB total, 1.4 GiB available |
| Disk | 20 GB free of 25 GB |
| JDK | OpenJDK **11** (AGP 9.x requires 17+) |
| Gradle / Android SDK / adb | not installed |
| `/dev/kvm` | **ABSENT** — no VT-x/SVM exposure |

### Reachability (P0-02)

| Host | HTTP | Verdict |
|---|---|---|
| dl.google.com | 302 | reachable |
| maven.google.com | 200 | reachable |
| services.gradle.org | 200 | reachable |
| plugins.gradle.org | 200 | reachable |
| repo.maven.apache.org | 200 | reachable |
| github.com | 200 | reachable |
| api.adoptium.net | 200 | reachable |

Spot check: `dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml`
returned HTTP 200, 25 337 bytes in 22 ms. Artifacts are genuinely downloadable, not just
DNS-resolvable.

Latest AGP versions visible in the metadata (2026-11-08): 9.3.3, 9.4.0, 9.4.1, 9.5.0, **9.5.1**.

### Consequence

**No blocked domains → the sandbox build path is viable on the network side.** The
constraints are memory and virtualisation, not connectivity.

### Emulator feasibility (affects Gate G3 and all of Phase 4)

`/dev/kvm` is absent and the CPU exposes no `vmx`/`svm` flag. Software-emulated AVDs
(API 26+) need roughly 2 GB of RAM for the guest alone, against 1.4 GB available, on
2 cores. **An Android emulator cannot realistically run here.** Gate G3 and Phase 4
cannot be satisfied inside this sandbox; they need a device farm, CI emulator runner, or
the user's own device.

---

## Phase 1 — Site discovery

### P1-01 Home page

| Item | Value |
|---|---|
| URL | `https://arena.ai/` |
| Status | HTTP/2 **200** |
| Size | 124 082 bytes |
| Title | `Arena AI: The Official AI Ranking & LLM Leaderboard` |
| `strict-transport-security` | `max-age=15552000; includeSubDomains; preload` |
| `content-security-policy` | `frame-ancestors 'self' https://huggingface.co/ https://*.static.hf.space/` |
| `x-frame-options` | `SAMEORIGIN` |
| `referrer-policy` | `same-origin` |
| `x-content-type-options` | `nosniff` |
| `<meta name="theme-color">` | **absent** |
| Icons | `/images/favicon-rebrand.svg`, `/apple-touch-icon.png` (180×180), `/apple-touch-icon-dark.png` |
| `rel="manifest"` link | **absent** |

Note on framing headers: `X-Frame-Options: SAMEORIGIN` and `frame-ancestors` govern
**iframe embedding**. A Trusted Web Activity is not an iframe — it renders in a full
browser instance — so these headers do not block a TWA. They *would* block an
iframe-based or naive WebView shell, which is one more reason the TWA decision is right.

Because `theme-color` is absent, status/navigation bar colours for P3-04 must be taken
from the site's own background colour or left to the platform default. To be decided at
implementation time, recorded in DECISIONS.md.

### P1-02 Web manifest

Requested `https://arena.ai/manifest.webmanifest` (the conventional path, since the home
page declares no manifest link): HTTP **200** but `content-type: text/html` and a 114 486
byte HTML shell — a soft-404, not a manifest.

**Verdict: no web app manifest. Arena is not installable as a PWA.**

**Gate G1 is satisfied in favour of building.** Chrome's *Install app* cannot produce an
app-like install here, so the APK is not redundant with a zero-code option.

### P1-03 `/.well-known/assetlinks.json`

HTTP 200, body is exactly `{}` (3 bytes). **No asset statements. No package is
associated with arena.ai.**

Consequences, all expected:

* A TWA launched against arena.ai will show a **URL bar** (unverified association). P4-01 records this as expected, not a failure.
* `autoVerify` on the intent filter would do nothing yet (P3-07) — it must stay off until Arena publishes statements for our package + signing fingerprint.
* Full-screen, bar-free operation requires Arena cooperation — the P2-05 / Phase 5 track.

### P1-04 Entry URLs

The home page is a client-rendered Next.js app; it exposed only `/`, `/leaderboard` and
`/history/search` as literal hrefs. The authoritative public route list is `robots.txt`
(a public static file, explicitly allowed by R4), which declares:

```
Allow: /            /chat        /side-by-side   /direct        /max
       /text        /text/side-by-side   /text/direct
       /image       /image/side-by-side  /image/direct
       /search      /search/side-by-side /search/direct
       /video       /code        /code/side-by-side   /code/direct
       /agent       /how-it-works  /faq   /blog   /careers   /company/about
```

Plus, seen as hrefs on the home page: `/leaderboard`, `/history/search`.

Mode → entry URL (for P3-08 shortcuts and P3-04 DEFAULT_URL):

| Mode | Entry URL | Source |
|---|---|---|
| Agent | `https://arena.ai/agent` | robots.txt Allow; user-supplied |
| Chat (generic) | `https://arena.ai/chat` | robots.txt Allow |
| Side-by-Side | `https://arena.ai/side-by-side` | robots.txt Allow |
| Direct | `https://arena.ai/direct` | robots.txt Allow |
| Battle | not present in robots.txt or home hrefs | **unresolved** — see below |
| Leaderboard | `https://arena.ai/leaderboard` | home page href |
| Max | `https://arena.ai/max` | robots.txt Allow |

**Open item / R5 compliance.** *Battle* appears in the roadmap and is mentioned in the
page markup, but no `/battle` route is declared in robots.txt and no href to it exists on
the saved home page. Per R5 I will not invent `arena.ai/battle`. Either the mode lives at
a different path, is unsupported as a standalone route, exposes an anchor to it, or it is a
different label for side-by-side. Escalated to the user (see PROGRESS.md).

### P1-05 Terms / privacy — status

Not yet analysed. Two redirects to the help centre need following (single fetch each) when
this phase item is worked. Not a blocker for the sandbox work; it is a blocker for
distribution decisions, and it feeds the Phase 5 permission request.

### P1-06 Arena API docs — status

Not yet reviewed. Only needed for optional module M1, which is off by default.

---

## What this means for the build

1. Network is fine; the sandbox can build.
2. **No emulator, no device.** Every item in Phase 4 and Gate G3 must be marked
   *Unverified — no Android runtime available* unless the user supplies results or CI
   supplies a runner. R7 forbids claiming otherwise.
3. Missing assetlinks means a URL bar in the TWA until Arena cooperates. This is a
   known, documented limitation, not a defect in the build.
4. `theme-color` absent → status-bar colour needs a decision at P3-04.
