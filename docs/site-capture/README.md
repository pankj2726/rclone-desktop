# Site capture (roadmap v3.1 section 9.4)

The agent does **not** fetch arena.ai. Automated requests are refused by bot detection, and
bypassing that (stealth headers, proxies, headless tricks, user-agent spoofing) is prohibited
by 9.9 and by R4. Site facts come from you, once now and once a month.

Save one file per capture, named `YYYY-MM-DD-capture.md`, and tell the agent it exists.

## What to record

1. **Final URL after redirects** from a clean desktop Chrome profile, and what the cookie
   prompt does on first visit.
2. **DevTools → Application → Manifest** — screenshot the fields and the *Installability*
   section. (Recent Lighthouse drops the PWA category; use this panel, not Lighthouse.)
3. **DevTools → Application → Service workers** — registered or not.
4. **`https://arena.ai/.well-known/assetlinks.json`** opened in the browser — save the body,
   or note that it is missing.
5. **Phone, in Chrome** — does the menu say *Install app* or *Add to Home screen*?
6. **Each entry point** — Agent, Battle, Side-by-Side, Direct, leaderboards, account — copy
   the URL from the address bar and note any locale prefix.
7. **Page titles, theme colour, header background colour** — for the splash and status bar.

## Why this matters

`DEFAULT_URL` and the four shortcuts are currently `arena.ai/agent`, `/chat`,
`/side-by-side`, `/leaderboard`, taken from `/robots.txt` on 2026-11-08. If a real capture
shows different paths — or a locale prefix — the shortcuts are wrong and I need the file.
