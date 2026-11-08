# Legal notes

Not legal advice. A reading of public documents on 2026-11-08, recorded so that later
decisions can be traced to what was actually written at the time.

Sources: arena.ai Terms of Use and privacy policy, via help.arena.ai.

## Clauses that matter to this app

> (iii) framing or using framing techniques to enclose any trademark, logo or other
> portion of the Service, including images, text, page layout or form

> (vi) accessing the Services through programmatic or automated means, or automatically
> querying them

> (vii) using spiders, robots, scrapers, crawlers or similar to scrape, extract or
> download data

> "Arena, Arena Intelligence, and all related stylizations, graphics, logos, service marks
> and trade names … are the trademarks of Company and may not be used without permission
> in connection with your, or any third-party's, products or service[s]."

## How the chosen architecture sits against them

**Framing (iii): not implicated.** A TWA hands the URL to a full browser instance; it does
not enclose the site in an iframe or re-host its layout. The site's own headers enforce this
distinction: `X-Frame-Options: SAMEORIGIN` and `CSP frame-ancestors 'self' …` block iframe
embedding, and those headers do not apply to a top-level browser launch. The mechanism this
app uses is the one the site permits.

**Automated access (vi, vii): not implicated.** A person opening an app that opens a
browser is not automated access. This project performs no scraping, no automated login, no
cookie or token extraction, and no script injection. The only automated requests are single,
read-only fetches of public static files during research.

**Trademarks: the live risk, and why R6 exists.** Calling the app "Arena", using its logo, or
implying endorsement would breach the clause above. Hence: neutral name, original icon,
no Arena marks anywhere in the app, the icon, or any listing.

## Practical consequences baked into the build

* Launcher name "Pankxu"; original three-bar mark drawn for this project (D-05, P2-02).
* No analytics, no ads, no crash SDKs — nothing that would make the app a data controller
  in its own right.
* No permissions, so the app cannot itself reach camera, microphone or files.
* `docs/arena-permission-request.md` drafts the ask; sending it is the user's action.

## What would change this analysis

If Arena objects, states that wrapping requires permission, or changes the Terms, distribution
stops until the user decides (section 6 escalation trigger).
