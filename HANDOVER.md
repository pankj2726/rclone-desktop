# Handover

**Date:** 2026-11-08 · **Package:** `what.thefcuk.pankxu` · **Version:** 1.0.0

## Verified (with evidence)

| What | Evidence |
|---|---|
| Source, build config, CI workflows, docs complete | this repo; `git ls-files` |
| Version pins real and current | `docs/research.md`, checked against dl.google.com metadata |
| arena.ai reachable, no manifest, empty assetlinks | `docs/research/` raw captures, 2026-11-08 |
| Entry URLs come from robots.txt, not invention | `docs/research/robots.txt` |
| URL allow-list logic correct | `UrlPolicyTest`, 9/9 pass on a JVM |
| XML resources well-formed; every resource reference resolves | checked with a parser, 0 errors |
| Workflows are valid YAML | parsed, 3/3 |
| Java sources compile clean | `javac -Xlint:all -Werror`, 0 warnings |

## Unverified (with reason)

| What | Why |
|---|---|
| Gradle build | never run — no JDK 17 / SDK / RAM here. First CI run is Gate G0 |
| APK exists at all | consequence of the above |
| Install, launch, login, OTP, modes, uploads, session | no device, no emulator, no `/dev/kvm` |
| Signed with your key | no key exists yet |
| Icon appearance on a real launcher | not renderable here |

## Blocked (with date checked)

| What | Date |
|---|---|
| Full-screen (no URL bar) | requires Arena assetlinks; checked 2026-11-08, returns `{}` |
| Battle shortcut | no published route; checked 2026-11-08 |

## Host changes (R8)

None outside this workspace. No packages installed into the host, no swap added.

## Decisions needed from you

1. **GitHub repo** — create one and push; the CI path you chose needs it. No repo URL was supplied and no GitHub connector is available to me.
2. **Keystore** — generate it yourself (commands in `docs/BUILD.md`), then give me only the certificate fingerprint for P2-03/Gate G2.
3. **Battle mode** — is there a real URL? If not it stays out (D-02).
4. **Bar colour** — D-03 is a guess; tell me once you've seen it on the phone.

## Next steps

1. Push, watch the first `build` workflow. Expect to fix one or two things; that run is Gate G0.
2. Install the debug APK, work through `docs/QA-REPORT.md`, row by row.
3. Generate the release key, run `scripts/sign-release.sh`, install the signed build.
4. Optionally send `docs/arena-permission-request.md` and log the reply.
