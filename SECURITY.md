# Security policy

## Reporting a vulnerability

Open a private security advisory on this repository (Security -> Advisories -> Report a
vulnerability). Please do not open a public issue for anything exploitable.

Include: affected version (`versionCode` from the APK badging), Android version, browser,
reproduction steps, and the impact you believe it has. Expect an acknowledgement within a
week; this is a personal, single-maintainer project with no on-call.

## Supported versions

Only the newest release. Older APKs are not patched.

## What is in scope

* The URL routing logic (`UrlPolicy`, `LaunchGateActivity`) — bypasses of the three-way
  IN_APP / EXTERNAL / BLOCK decision.
* Exported components and intent handling.
* Anything that would let a link load a non-`arena.ai` origin *inside* the app.
* The signing and release pipeline.

## What is out of scope

* The content, behaviour or security of arena.ai itself — report that to Arena.
* Anything requiring a rooted device or a modified OS.
* Google account security.
* Bugs that require the user to deliberately install a modified APK.

## Verifying a release

Every release publishes the SHA-256 of the APK and of the signing certificate. Compare both
before installing; see `docs/INSTALL.md`. If the certificate fingerprint does not match the
one in `artifacts/certificate-fingerprint.txt`, stop.
