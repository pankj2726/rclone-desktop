# Grabbing "everything" of a website: tools, agent workflows, and where the line is

Researched 2026-11-08 for the Pankxu project. This is a survey, not a green light: the
boundaries section is the part that matters, and it is deliberately first.

---

## 1. Boundaries first, because the request was "everything"

"Grab everything (frontend, backend, functions, UI)" means four different things, and only
some of them are obtainable at all:

| Layer | Obtainable from outside? | Legitimate route |
|---|---|---|
| **UI** (layout, styles, components) | Yes — it is sent to the browser by design | Inspect, snapshot, or screenshot the page |
| **Frontend** (HTML, CSS, JS bundles) | Yes — same reason | Save, mirror, or capture as you browse |
| **Network surface** (endpoints, request shapes) | Partly — whatever the page calls while you use it | Your own DevTools Network tab, on your own session |
| **Backend** (server code, database, business logic) | **No.** It is not shipped to the browser | Only from whoever owns it |
| **Functions / secrets / keys** | **No**, and this is the point of not shipping them | Never legitimately extractable |

Two hard rules for this project:

1. **Never for arena.ai.** Roadmap v3.1 §9.1 and §9.9 prohibit scripted fetching, scraping,
   headless-browser tricks against it, TLS interception to reverse-engineer its endpoints, and
   cookie/session/token export. That prohibition is a project rule and also matches Arena's
   own terms. The app's tests never touch Arena's pages; a human drives them (R15).
2. **Never an account's credentials — including your own.** Session tokens, cookies and API
   keys are live authentication material. Copying them out of a browser profile into a
   document makes them stealable and is outside what I will do, even with permission. If you
   want to *keep* access safe, the right move is to control it at the identity provider, not
   to duplicate the secret into a text file.

Legal backdrop, for completeness: in India, unauthorised access or data extraction can engage
IT Act 2000 §43 and §66; elsewhere the equivalents are the CFAA (US), Computer Misuse Act (UK),
and Art. 5–6 of the EU directive on attacks against information systems. Separately, a site's
terms bind you contractually even where the technology would allow it. And UI code, text and
imagery remain copyrighted even though they are downloadable.

**Everything below is written for sites you own or are contracted to test.**

---

## 2. Static capture — get the bytes the page sent

Best when you want a faithful copy of a page or a small site.

| Tool | What it does | Notes |
|---|---|---|
| `wget --mirror --page-requisites --convert-links` | full-site download to disk | The classic; handles relative links well. Add `--wait` and a contactable user-agent; be polite |
| `wget2`, `httrack`, `SiteSucker` | GUI/CLI mirrors | HTTrack's rewrite of links is more aggressive than wget's |
| **SingleFile** (browser extension) | one self-contained HTML file per page | Best fidelity for a *single* page, inlines CSS/fonts/images as data URIs |
| Chrome "Save page as → Webpage, complete" | manual, per page | Fine for a handful of pages |
| Chrome DevTools → **Save all as HAR with content** | the full HTTP exchange | The single best artifact for understanding a site's network behaviour |

**When to use:** archiving a reference page, diffing a page you own over time, building a
fixture set for tests.

---

## 3. Browser automation — capture what only exists after JavaScript runs

Modern sites render client-side, so a downloader gets an empty shell. You need a real engine.

| Tool | Strength | Weakness |
|---|---|---|
| **Playwright** | Best all-round: multi-browser, auto-wait, HAR recording, screenshots, accessibility tree, tracing | Python/Node/Java/.NET API, not a click-to-use app |
| **Puppeteer** | Deep Chrome DevTools Protocol access | Chromium-centric |
| **Selenium** | Enormous ecosystem, grid support | Slower, flakier assertions |
| **Chrome DevTools Protocol directly** (`/json`, `Page.captureSnapshot`) | No wrapper, full control | You build everything yourself |

Useful Playwright capabilities for exactly this job:

```python
# Record every request/response for later analysis
context = await browser.new_context(record_har_path="site.har", record_har_content="embed")

# A single self-contained DOM snapshot (MHTML-style)
cdp = await context.new_page()
await cdp.goto(url)
snapshot = await cdp.context.new_cdp_session(cdp)
await snapshot.send("Page.captureSnapshot", {"format": "mhtml"})

# The accessibility tree - what a screen reader (or an LLM) actually "sees"
tree = await page.accessibility.snapshot()

# Computed styles = the real design tokens, not the stylesheet's intent
styles = await page.evaluate("""() => {
  const out = {};
  for (const el of document.querySelectorAll('*')) {
    const s = getComputedStyle(el);
    out[el.tagName] = {color: s.color, font: s.fontFamily, size: s.fontSize};
  }
  return out;
}""")
```

---

## 4. Network and API surface — what the page talks to

This is the closest you can legitimately get to "the backend", and it is still only the
*interface*, never the implementation.

1. **DevTools → Network → Save all as HAR.** Then read it: Google's HAR Analyzer, or a few
   lines of Python to list `method + URL + status + content-type`.
2. **Copy as cURL** on any request, then import into Postman/Insomnia to replay it. This is how
   most "reconstruct the API" work actually starts — on your own session, against your own
   account.
3. **mitmproxy / Charles / Proxyman** intercept HTTPS traffic from a device you control, with
   the proxy's certificate installed on that device. Legitimate for testing your own app;
   illegal and against the rules anywhere else.
4. **Schema discovery, where published:** `/openapi.json`, `/swagger.json`,
   `/.well-known/openapi`, `/graphql` introspection (usually disabled in production).
   Check `robots.txt`, `/sitemap.xml`, `/.well-known/` for what the owner intends you to see.
5. **JavaScript bundle reading.** The client already contains every endpoint string it calls:
   `grep -ohE '"/[a-z0-9/_-]{3,}"' bundle.js | sort -u`. Source maps (`.js.map`) reveal the
   original structure when published. This is reading code you were served — fine on your own
   site; on someone else's it is reconnaissance.

**What no tool can give you:** server-side logic, database contents, secrets, or keys. If a
tool claims otherwise it is either exploiting a vulnerability or lying. If an endpoint is
protected by a token, the token is the credential — see §1.

---

## 5. Design and UI reconstruction

| Tool | Purpose |
|---|---|
| **Figma → code** plugins (Anima, Locofy, Builder.io Visual Copilot) | handoff from designs you own |
| **Builder.io Visual Copilot**, **screenshot-to-code** (open source, `abi/screenshot-to-code`) | screenshot or mock-up → HTML/Tailwind/React |
| **html.to.design** | import a live URL into Figma as editable layers |
| **cssstats / Project Wallace** | quantify a stylesheet: palette, type scale, specificity |
| **BackstopJS / Percy / Applitools** | visual regression across breakpoints |

For extracting the *system* rather than the pixels: pull computed styles (§3), collect CSS
custom properties (`getComputedStyle(document.documentElement).getPropertyValue('--x')`),
enumerate font families actually loading (`document.fonts`), and record the breakpoints the
layout uses by resizing and logging `matchMedia` results.

---

## 6. AI agents that drive a browser

This is the part of the field that changed most in the last two years. The pattern is the
same everywhere: an LLM reads a page through a structured channel, decides an action, and a
browser executor performs it.

**Agent ↔ browser bridges**

| Project | What it is |
|---|---|
| **Playwright MCP** (Microsoft) | exposes Playwright as MCP tools so any MCP-capable agent can drive a browser deterministically (accessibility-tree based, no screenshots needed) |
| **Chrome DevTools MCP** | DevTools protocol as MCP tools — performance traces, network, DOM |
| **Browser MCP** | connects an agent to *your own* already-running, already-logged-in browser |
| **browser-use** (open source) | self-contained Python agent; strong at form-filling and multi-step flows |
| **Stagehand** (Browserbase) | "act / extract / observe" primitives — natural-language actions, typed extraction |
| **Skyvern**, **Agent-E**, **Nanobrowser** | agent frameworks aimed at web workflows |

**Crawl-and-extract pipelines**

| Project | What it is |
|---|---|
| **Crawl4AI** | open-source crawler that emits LLM-ready markdown; handles JS pages |
| **Firecrawl** | hosted equivalent: crawl, scrape, structured JSON extraction |
| **Jina Reader** (`r.jina.ai`) | URL → clean markdown, no infrastructure |
| **ScrapeGraphAI** | graph-of-nodes pipeline where each node is an LLM extraction step |
| **Crawlee** | production crawling framework (queueing, retries, proxy rotation) |

**What these are actually good at, and where they fail**

- Good: enumerating what is on your own site, extracting structured data from pages you are
  allowed to read, writing Playwright tests by demonstration, turning a UI into a spec.
- Bad: anything behind a login you do not own (they will be blocked, and should be); heavy
  SPAs where the accessibility tree is empty; sites that deliberately detect automation.
- And: an agent that "reads everything" will happily exfiltrate whatever it reads into a log.
  Point it only at data you would be comfortable seeing in a transcript.

**The honest summary for this project:** the most valuable agent capability is not bulk
extraction, it is *deterministic action with evidence*. Playwright MCP + a real device
emulator is what would have caught the three bugs found by hand in this app's first day
(a crash, white-on-white text, dead shortcuts).

---

## 7. Multi-agent development — what actually works

Patterns with real evidence behind them:

1. **Orchestrator + specialists.** One agent plans and splits work; separate agents implement,
   review and test. Each specialist gets a narrow brief and its own success check.
2. **One worktree per agent.** `git worktree add ../agent-a feature/x` — two agents editing the
   same checkout corrupt each other's work. This is the single most common failure mode.
3. **CI as the referee, not the agent's opinion.** An agent that reports success is worth
   nothing until a pipeline agrees. Every claim in this project is tied to a run id for exactly
   that reason.
4. **Evidence over narrative.** Ask each agent for the artifact: a log, a hash, a screenshot,
   a failing test. Anything else is a story.
5. **Explicit ownership of files.** Two agents editing `strings.xml` and `build.gradle.kts`
   concurrently is how you get a two-hour merge fight (observed here today, during a snapshot
   restore, and it cost three commits).
6. **A human on the irreversible steps.** Signing keys, publishing, deleting things.

Tools that support this: Claude Code sub-agents, Codex CLI, Aider (per-file ownership and
auto-commit), OpenHands, SWE-agent. All of them are wrappers around the same discipline —
separate context, bounded task, verifiable output.

For *this* repository the useful split would be:

| Agent | Scope | Check |
|---|---|---|
| Build/CI | workflows, Gradle, signing pipeline | a green run id |
| App code | activities, policy, resources | unit tests + manifest guard |
| Docs/legal | README, PRIVACY, LEGAL-NOTES, handover | cross-links resolve, no invented URLs |
| Verification | runs the device checklist, writes evidence | screenshots + checksums, R13-redacted |

with the standing rule that **no agent drives Arena's own pages** (R15) and no agent ever
handles key material (§1).

---

## 8. If you want this applied to a specific site

Tell me which site and what you actually need, and the approach narrows fast:

- *"Rebuild my site's UI somewhere else"* → §3 + §5, with a capture of your own pages.
- *"Document my API"* → §4, from a HAR of your own session; I can turn that into an OpenAPI
  skeleton.
- *"Watch a competitor's pricing page"* → §2/§3 on public pages only, politely, respecting
  robots.txt — and tell me if the terms say no.
- *"Get everything from Arena"* → no, and that is a project rule, not a capability limit.
