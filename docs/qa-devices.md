# Test devices and targets

## Devices used so far

| Id | Device | Image | Chrome | Google account | Used for |
|---|---|---|---|---|---|
| D1 | Samsung SM-G9900 (Galaxy S21 FE) | Stock Android 12, SDK 32, patch 2022-06-01, arm64-v8a, 1080x1920 @ 320dpi, locale zh-HK | **none** (only `mark.via` and WebView Shell; system WebView 95.0.4638.74) | none, Play cannot sign in | First device pass, 2026-11-08. Gate, routing, blocked schemes, shortcuts, rotation, back, 5 launch cycles |

Details and evidence: `docs/qa-evidence/device-2026-11-08/`.

## Devices still needed

| Id | Target | Why | Owner |
|---|---|---|---|
| D2 | Emulator, Google Play system image, API 34 or newer | Only a Play image ships Chrome, and the TWA path (S1-02) is untestable without it. Needs a throwaway Google account to install Chrome from Play. | User (S1-01) |
| D3 | Physical phone with Chrome, ideally the user's own | R14 says only release-APK results count toward delivery, and TWA behaviour on real hardware is what users will see | User (S1-01) |
| D4 | Second API level (26 and 30) | minSdk is 26; the fallback chain and the theme fixes need checking on older platforms | Agent, once D2 exists |
| D5 | Tablet or foldable | Layout sanity on a large screen; the gate is a centred LinearLayout with no size qualifiers | Agent, once D2 exists |

## Rules for this list

* No Chrome from unofficial APK sites (S1-01). Play Store, or a build whose signature can be
  checked against Google's published certificate.
* Do not record account names, serial numbers or notification content in notes or screenshots
  (R13). Captures from D1 have had the status and navigation bars cropped for this reason.
* Every result is tagged with the APK SHA-256 it ran on. Debug results stay in the development
  column and never count toward delivery (R14).
