# Research — Phase 0 & Phase 1

Single, read-only fetches of public files only (R4). No page fetched twice, no scraping,
no login, no cookie or token use, no JS injection. Recorded **2026-11-08**.

---

## Phase 0 — Environment (P0-01) and connectivity (P0-02)

| Item | Value |
|---|---|
| OS | Debian GNU/Linux 13 (trixie), kernel 6.1.189+, x86_64 |
| CPU | 2 cores |
| RAM | **1.9 GiB total, 1.4 GiB available** |
| Disk | 20 GB free of 25 GB |
| JDK | OpenJDK **11** (AGP 9.x requires 17) |
| Gradle / Android SDK / adb | not installed |
| `/dev/kvm` | **ABSENT**; no `vmx`/`svm` in `/proc/cpuinfo` |

### Reachability — all hosts up

| Host | HTTP |
|---|---|
| dl.google.com | 302 |
| maven.google.com | 200 |
| services.gradle.org | 200 |
| plugins.gradle.org | 200 |
| repo.maven.apache.org | 200 |
| github.com | 200 |
| api.adoptium.net | 200 |

Artifact spot check: `dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml`
→ HTTP 200, 25 337 B, 22 ms. Real artifacts, not just DNS.

**No blocked domains. The sandbox build path is viable on the network side.**

### The two constraints that actually bite

**1. No emulator, no device.** `/dev/kvm` is absent and the CPU exposes no virtualisation
flag. A software-emulated AVD needs ~2 GB for the guest alone, against 1.4 GB available on
2 cores. An Android emulator cannot run here at usable speed, if at all.

> Consequence: **Gate G3 and the whole of Phase 4 cannot be satisfied by this agent.**
> Definition of Done items 3 and 4 (installed and launched on a real device; Google login
> and email OTP verified end to end) require the user's phone, a device farm, or a CI
> emulator runner. R7 forbids claiming them otherwise.

**2. RAM.** AGP + Gradle + Kotlin on 1.4 GiB available is marginal at best. R10's
`-Xmx1536m` is itself larger than the free memory. A build may succeed for a project this
small, or may OOM — that is an empirical question, to be tested, not assumed.

---

## Version pins (section 7 checklist)

| Component | Pin | Source (2026-11-08) |
|---|---|---|
| AGP | **9.5.1** (latest stable) | dl.google.com maven-metadata.xml |
| AGP 9.5 minimums | Gradle **9.6.0**, JDK **17**, build-tools **36.0.0**, supports API 37 | developer.android.com/build/releases/agp-9-5-0-release-notes |
| Play target API | **API 36+ mandatory** for new apps and updates since 2026-08-31 | support.google.com/googleplay/android-developer/answer/11926878 |
| compileSdk / targetSdk | 36 (37 available; AGP 9.5 supports it) | as above |

minSdk stays **26** per the locked decisions.

---

## Phase 1 — Site discovery

### P1-01 Home page

| Item | Value |
|---|---|
| URL | `https://arena.ai/` |
| Status | HTTP/2 **200**, 124 082 bytes |
| Title | `Arena AI: The Official AI Ranking & LLM Leaderboard` |
| HSTS | `max-age=15552000; includeSubDomains; preload` |
| CSP | `frame-ancestors 'self' https://huggingface.co/ https://*.static.hf.space/` |
| X-Frame-Options | `SAMEORIGIN` |
| Referrer-Policy | `same-origin` |
| X-Content-Type-Options | `nosniff` |
| theme-color | **absent** |
| Icons | `/images/favicon-rebrand.svg`, `/apple-touch-icon.png` 180×180, `/apple-touch-icon-dark.png` |
| `rel="manifest"` | **absent** |

The framing headers matter for the legal question below, not for the TWA: a Trusted Web
Activity is not an iframe, so `frame-ancestors` and `X-Frame-Options` do not apply to it.
They *do* apply to iframe-based embedding — the technique Arena's Terms specifically ban.

`theme-color` being absent means P3-04's status/navigation bar colours need a decision at
implementation time (site background sample, or platform default). → DECISIONS.md.

### P1-02 Web manifest — **none**

`/manifest.webmanifest` returns HTTP 200 but `content-type: text/html` and 114 486 bytes of
the app shell: a **soft-404**, not a manifest. `/manifest.json` → 404. The home page declares
no `rel="manifest"`.

**Verdict: Arena publishes no web app manifest and is not installable as a PWA.**

> **Gate G1 resolved in favour of building.** The "Chrome Install app already does this with
> zero code" escape hatch does **not** apply here — that path is unavailable. The APK is not
> redundant.

### P1-03 `/.well-known/assetlinks.json` — empty

HTTP 200, body exactly `{}` (3 bytes). No asset statements, no package associations.

Consequences:
* A TWA against arena.ai shows a **URL bar** (unverified association). P4-01 records that as
  expected, not a defect.
* `autoVerify` (P3-07) would achieve nothing yet and stays off until Arena publishes a
  statement for our package name **and** our signing certificate fingerprint.
* Full-screen, bar-free operation is blocked on Arena cooperation → the P2-05 / Phase 5 track.

### P1-04 Entry URLs

The home page is a client-rendered Next.js app and exposed only `/`, `/leaderboard` and
`/history/search` as literal hrefs. The authoritative public route list is `robots.txt`
(a public static file, explicitly permitted by R4), which declares:

```
/            /chat      /side-by-side   /direct    /max
/text        /text/side-by-side         /text/direct
/image       /image/side-by-side        /image/direct
/search      /search/side-by-side       /search/direct
/video       /code      /code/side-by-side  /code/direct
/agent       /how-it-works   /faq   /blog   /careers   /company/about
```

Plus `/leaderboard` and `/history/search` from home-page hrefs.

| Mode | Entry URL | Source |
|---|---|---|
| Agent | `https://arena.ai/agent` | robots.txt; also user-supplied |
| Chat | `https://arena.ai/chat` | robots.txt |
| Side-by-Side | `https://arena.ai/side-by-side` | robots.txt |
| Direct | `https://arena.ai/direct` | robots.txt |
| Max | `https://arena.ai/max` | robots.txt |
| Text / Image / Search / Video / Code | `https://arena.ai/{text,image,search,video,code}` | robots.txt |
| Leaderboard | `https://arena.ai/leaderboard` | home page href |
| **Battle** | **not found** | see below |

**Open item, R5 compliance.** "Battle" appears in the roadmap and is mentioned in the page
markup, but there is **no `/battle` route in robots.txt and no href to one on the home
page**. R5 forbids inventing `arena.ai/battle`. Either it lives at a different path, is a
mode inside another route, or is a label rather than a URL. **Escalated to the user.** Until
resolved, no Battle shortcut and no Battle shortcut URL will be shipped.

### P1-05 Terms and privacy — key clauses

Terms of Use (help.arena.ai, fetched 2026-11-08). Relevant prohibitions:

* **(iii)** framing or using framing techniques to enclose any trademark, logo or other
  portion of the Service, including images, text, page layout or form.
* **(vi)** accessing the Services through programmatic or automated means, or automatically
  querying them.
* **(vii)** using spiders, robots, scrapers, crawlers or similar to scrape, extract or
  download data.
* **Trademarks:** "Arena, Arena Intelligence, and all related stylizations, graphics, logos,
  service marks and trade names … are the trademarks of Company and may not be used without
  permission in connection with your, or any third-party's, products or service[s]."

How the chosen architecture sits against these:

* A **TWA / Custom Tab is not framing.** It hands the URL to the real browser engine and
  renders the real site in a top-level browsing context. No iframe, no re-hosting, no
  reproduction of layout. Clause (iii) is aimed at embedding, and the site enforces that with
  `X-Frame-Options: SAMEORIGIN` + `frame-ancestors`. The TWA does not trip either.
* Clauses (vi) and (vii) target automated access. A person using an app that opens a browser
  is not automated access. This project does no scraping, no token extraction, no injection
  — consistent with R4.
* **The trademark clause is why R6 exists.** Shipping something called "Arena" with Arena's
  logo is the one clearly prohibited thing here. Neutral name, original icon, no Arena marks
  in app name, icon, or store listing.

*This is a reading of public documents, not legal advice.*

Privacy policy was retrievable but is not yet analysed; scheduled with the Phase 5 track.

### P1-06 Arena API docs

Not yet reviewed. Only needed for optional module M1 (off by default). Deferred; not on the
critical path to a signed APK.

---

## What Phase 0/1 changes about the plan

1. **Build in sandbox: plausible but unproven.** Network is clean; RAM is the risk. Test
   before committing to it.
2. **Verification is the real blocker, not building.** No emulator, no device. Everything in
   Phase 4 and Gate G3 will be *Unverified* unless the user runs the QA checklist or a device
   farm does.
3. **URL bar in the app until Arena publishes assetlinks.json.** Documented limitation, not a
   bug.
4. **Battle mode URL unresolved.** Needs the user.
5. **Signing needs the user's key**, which R3 says never to handle — so either the user signs
   locally or supplies the keystore by environment variable. Decision needed.
