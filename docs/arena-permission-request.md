# Request to Arena (draft — sending it is the user's action)

To: Arena (support / business contact as published on arena.ai)
From: the app's owner
Subject: Request to associate an Android app with arena.ai (Trusted Web Activity), and a
question about your Terms on wrapping

Hello,

I have built a personal-use Android app that opens arena.ai in a Trusted Web Activity — the
standard Android mechanism that hands your URL to the phone's browser engine. It renders your
real site, at your real URL, with your real login. It is not an iframe, does not copy your
design, contains no Arena logo or name, collects no data, and requests no Android permissions.
It is used by me alone and will not be distributed publicly unless you say that is acceptable.

Two things I would like to ask:

1. **Asset links.** Would you publish an `assetlinks.json` statement (or allow me to propose
   the exact JSON) associating my package with arena.ai? Without it, the app always shows a
   URL bar and your links open in a browser rather than in the app.

   - Package name: `what.thefcuk.pankxu`
   - SHA-256 signing certificate fingerprint: *(to be inserted once the release key exists)*

2. **Terms.** Your Terms prohibit "framing or using framing techniques." I read that as
   directed at iframe-style embedding, which your `X-Frame-Options: SAMEORIGIN` and CSP
   `frame-ancestors` already prevent — and which a Trusted Web Activity does not do. Could you
   confirm that a browser-backed wrapper of this kind is acceptable for personal use, and say
   whether any conditions would apply (naming, iconography, distribution)?

I would also welcome answers to:

- Are there official entry URLs for each mode (Agent, Chat, Side-by-Side, Direct, Leaderboard,
  and Battle — I could not find a published Battle route)?
- Is there a supported sign-in route for apps, which might be preferable to the browser flow?
- Would you like any in-app affordance (a link back to arena.ai in a browser, an attribution
  line, anything else)?

If the answer is no, that is fine — I will keep it private and unchanged, and leave the URL bar
in place.

Thank you for building something worth wrapping.

*(This draft is not legal advice and makes no claim about Arena's position. Do not send it
without reading it as the owner of the app and, if you wish, taking advice.)*
