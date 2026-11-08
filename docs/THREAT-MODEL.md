# Threat model (P6-01, refreshed for v3.1)

Short and specific. The attack surface is one exported activity and one exported library
component; there is no network code, no storage of user content and no background work.

## What is exported

From the merged manifest of build 42499447209 (aapt2 dump, not assumption):

| Component | Exported | Assessment |
|---|---|---|
| `what.thefcuk.pankxu.LaunchGateActivity` | **true** | Ours. Validates every inbound URL through `UrlPolicy` before anything loads. The only intentional entry point. |
| `what.thefcuk.pankxu.PankxuLauncherActivity` | false | The TWA. Unreachable except through the gate. |
| `com.google.androidbrowserhelper.trusted.ShortcutTrampolineActivity` | **true** | Library. Documented to run with `Theme.NoDisplay`, re-validate the URL against trusted origins and the `BROWSABLE` filters, and drop untrusted launches. Accepted as-is. |
| `com.google.androidbrowserhelper.trusted.ColdShortcutActivity` | false | Only reachable via `COLD_SHORTCUT_ACTIVITY`, which we do not set. |
| `androidx.startup.InitializationProvider` | false | Standard AndroidX initialisation. |
| `androidx.profileinstaller.ProfileInstallReceiver` | **true** | System-only actions; protected by `android.permission.DUMP` / `SEND_BROADCAST`. Cannot be triggered by another app. |

## Threats and what stops them

**1. A malicious link opens something that is not arena.ai inside the app.**
Two independent checks: `LaunchGateActivity` classifies before launching, and
`PankxuLauncherActivity.isTrustedIntentUrl` re-validates inside the library. `UrlPolicyTest`
covers lookalike hosts (`arena.ai.evil.com`, `notarena.ai`, `arena.aii`), userinfo tricks
(`arena.ai@evil.com`), and scheme abuse. A host-suffix bug is the classic failure mode and is
explicitly tested against.

**2. A malicious link launches an arbitrary external app.**
Non-web schemes (`intent:`, `javascript:`, `file:`, `content:`, `data:`, `tel:`, `mailto:`) are
`BLOCK`ed rather than forwarded. Only `https` URLs reach `ACTION_VIEW`, so this app cannot be
used as an intent launcher for another app's components.

**3. Redirect abuse after load.**
Off-origin navigations never render inside the TWA — the browser engine owns that decision and
the site's own CSP applies. The Custom Tab fallback shows its own toolbar for any origin
change. `docs/LIMITATIONS.md` documents the visible toolbar during third-party sign-in.

**4. Data exfiltration from the app.**
There is nothing to exfiltrate: no storage of chats, no network client, no analytics, and one
normal-level permission (`ACCESS_NETWORK_STATE`) that cannot read user data.

**5. A tampered APK.** Signing + the published SHA-256 of both the APK and the certificate
(`docs/INSTALL.md`). If the certificate fingerprint differs from the published one, it is not
our build.

## Accepted risks

* The `ShortcutTrampolineActivity` export is the library's design; we rely on its validation
  because it is the maintained implementation of exactly this check.
* A verified association cannot be claimed while `assetlinks.json` is `{}`, so a URL bar is
  always visible. That is a UX limitation, not a security control.
* The `androidx.startup` signature-level permission is auto-generated and relates only to this
  app's own receivers.
