# Privacy

## What this app collects

**Nothing.** No analytics, no telemetry, no crash reporting, no advertising, no accounts of
its own, no server component. It has no storage beyond what Android gives every app, and it
reads no device data.

It does hold two things locally, both invisible and both on-device only:

* a boolean saying the one-time research disclosure has been acknowledged;
* whatever SharedPreferences the TWA library keeps internally for its browser session token.

Neither leaves the device, and `allowBackup=false` keeps Android from syncing them to a
Google account.

## What Arena collects — and this matters

You are using **arena.ai**, not this app. Arena's own FAQ (checked 2026-11-08 at
`arena.ai/de/faq`) states that:

* prompts in **Side-by-Side** and **Direct** modes are **collected for research**;
* those two modes **do not count as leaderboard votes** (Direct has no voting at all);
* data shared publicly, or with model providers, is **de-identified**.

So the useful mental model is: this app is a window, Arena is the service, and everything you
type goes to Arena under Arena's privacy policy. The app shows this in a one-time disclosure
on first launch, with a link to Arena's privacy page.

**Practical advice while evaluating the app:** use prompts containing no personal data, no
credentials, no client material. See also `docs/LEGAL-NOTES.md`.

## What the browser collects

The page renders inside Chrome (or whichever browser provides the Trusted Web Activity). That
browser's own history, cookies, sync and permission model apply. The app cannot read any of it.

## Permissions

One declared permission: `ACCESS_NETWORK_STATE`. It is normal-level, auto-granted, and used
solely to show the offline "Retry" screen instead of a blank browser error. It cannot read
user data. No camera, microphone, storage, location or contacts permission is requested — when
the site needs those, the **browser** prompts, under the browser's own grants.

## Removing your data

Uninstalling the app removes nothing of yours, because it holds nothing of yours. To clear site
data, sign out or clear data inside arena.ai / your browser.

## Contact

There is no data collection to report. For vulnerabilities, see `SECURITY.md`.
