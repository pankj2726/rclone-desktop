# Publishing to GitHub

The agent cannot create the repository on its own. Two facts, checked 2026-11-08:

* Arena offers no GitHub connector, so there is no app-level integration to authorise.
* `POST https://api.github.com/user/repos` without a token returns **HTTP 401**. GitHub requires a
  credential; an authorisation in chat is not one.

So there are two routes. Route B needs nothing from you but a terminal.

---

## Route A — one command here (you supply a token)

Create a **fine-grained** personal access token at
`github.com/settings/tokens?type=beta`:

| Setting | Value |
|---|---|
| Repository access | *All repositories* (account-level creation needs this) |
| Account permissions → **Administration** | **Read and write** |
| Repository permissions → **Contents** | Read and write |

Then paste into the chat, on its own line:

```
GH_TOKEN=<the token>
REPO_NAME=pankxu
VISIBILITY=private
```

The agent runs `scripts/publish-to-github.sh`, which:

1. identifies the account behind the token,
2. creates the repository if it does not exist,
3. pushes `main` using a credential helper that reads the token from the **environment** —
   so it never lands in `.git/config`, in `argv`, or in shell history,
4. prints the repository URL.

**Revoke the token immediately afterwards.** Fine-grained tokens can be set to expire in a day,
and you can kill it from the same settings page the moment the push lands. Realistically, treating
a pasted token as burned is the right default — same advice as for the OAuth token earlier.

## Route B — push from your own machine (no secret leaves your device)

Download `artifacts/pankxu-source-*.zip` from this workspace, then:

```bash
unzip pankxu-source-*.zip && cd pankxu
git remote set-url origin https://github.com/<you>/pankxu.git   # your new empty repo
git push -u origin main
```

The zip contains the full `.git` directory, so history, tags and the workflows all survive. Create
the empty repository in the browser first — **do not** initialise it with a README, or the first
push will be rejected as non-fast-forward.

---

## After the push, either route

1. **Watch the first `build` workflow.** It is Gate G0 — the first real proof the project compiles.
   Expect one or two red runs; that is what it is for.
2. **Add the four signing secrets** (Settings → Secrets and variables → Actions):
   `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
   `base64 -w0 release.jks` produces the first.
3. **Tag a release** once `build` is green: `git tag v1.0.0 && git push --tags`.
   `release.yml` then builds, signs, verifies with `apksigner`, checks alignment, and attaches the
   APK, `SHA256SUMS.txt`, the certificate dump and the dependency tree to the GitHub release.
4. **Install it** following `INSTALL.md`, then work through `QA-REPORT.md`.

Private repositories on a free account get 2,000 Actions minutes a month. A one-activity app
builds well inside that; the weekly canary costs a few minutes.
