# Link verification (roadmap 9.7)

Arena publishes `{}` at `https://arena.ai/.well-known/assetlinks.json` (as captured
2026-11-08). Until that changes, this app cannot be a *verified* TWA: a URL bar shows, and
`arena.ai` links open in the browser rather than the app on Android 12+.

`autoVerify` is therefore deliberately **off** in the manifest. Enabling it with no matching
statement produces a failed-verification state for no benefit.

## Checking the statement Google can see

```bash
curl -s 'https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=https://arena.ai&relation=delegate_permission/common.handle_all_urls'
```

`{}` or an empty list means no app is associated. This is a Google endpoint, not arena.ai, so
it is safe to query.

## Checking the state on a device (X-08)

```bash
adb shell pm get-app-links what.thefcuk.pankxu
adb shell pm verify-app-links --re-verify what.thefcuk.pankxu
```

Expected today: `none` / `1024` (unverified). After Arena publishes an asset statement
containing our package name **and** our release certificate fingerprint, this should read
`verified` for `arena.ai`, and the URL bar disappears.

## Local debugging only (X-07)

Chrome documents the flag `--disable-digital-asset-link-verification-for-url` for previewing
full-screen behaviour before the statement exists. It needs Chrome's command-line-flags toggle
on a non-rooted phone, applies only to a developer's own device, and must never be something
the shipped app depends on.

## If Play App Signing is ever used (P2-04)

Google re-signs with its own key, so the **Play signing certificate** fingerprint must also be
added to the same asset statement. The upload key and the Play signing key are different files
with different fingerprints, and only the Play one is on the installed app.
