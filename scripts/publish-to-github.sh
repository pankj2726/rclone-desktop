#!/usr/bin/env bash
# Create the GitHub repository and push this source tree to it.
#
# Usage (either works):
#   GH_TOKEN=ghp_... ./scripts/publish-to-github.sh                # private, name "pankxu"
#   GH_TOKEN=... REPO_NAME=pankxu VISIBILITY=public ./scripts/publish-to-github.sh
#
# R3: the token is read from the environment only. It is never echoed, never written
# to .git/config, never placed in a command line (where `ps` and shell history would
# capture it), and never printed on failure.
set -euo pipefail
cd "$(dirname "$0")/.."

REPO_NAME="${REPO_NAME:-pankxu}"
VISIBILITY="${VISIBILITY:-private}"
DESCRIPTION="${DESCRIPTION:-Android Trusted Web Activity container for arena.ai}"

die() { printf 'error: %s\n' "$1" >&2; exit 1; }

[ -n "${GH_TOKEN:-}" ] || die "GH_TOKEN is not set. Create a token with 'Administration: write' and 'Contents: write', then export it."
[ -d .git ] || die "not a git repository"

command -v curl >/dev/null || die "curl is required"
git rev-parse --verify HEAD >/dev/null 2>&1 || die "no commits yet"

api() {
    # $1 = method, $2 = path, $3 = optional json body
    if [ -n "${3:-}" ]; then
        curl -sS -X "$1" -m 60 \
            -H "Authorization: Bearer ${GH_TOKEN}" \
            -H "Accept: application/vnd.github+json" \
            -H "X-GitHub-Api-Version: 2022-11-28" \
            -d "$3" "https://api.github.com${2}"
    else
        curl -sS -X "$1" -m 60 \
            -H "Authorization: Bearer ${GH_TOKEN}" \
            -H "Accept: application/vnd.github+json" \
            -H "X-GitHub-Api-Version: 2022-11-28" \
            "https://api.github.com${2}"
    fi
}

echo "== identifying the account behind the token =="
me=$(api GET /user)
login=$(printf '%s' "$me" | python3 -c 'import sys,json;print(json.load(sys.stdin).get("login",""))' 2>/dev/null || true)
[ -n "$login" ] || die "the token was rejected by GitHub (401/403). Check that it has not expired and has 'Administration: write'."
echo "   account: $login"

echo "== checking whether $login/$REPO_NAME already exists =="
code=$(curl -sS -o /dev/null -w '%{http_code}' -m 30 \
    -H "Authorization: Bearer ${GH_TOKEN}" \
    -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/$login/$REPO_NAME")

if [ "$code" = "200" ]; then
    echo "   exists already; will push into it"
elif [ "$code" = "404" ]; then
    echo "   creating $VISIBILITY repository"
    body=$(python3 -c 'import json,sys; print(json.dumps({"name": sys.argv[1], "private": sys.argv[2] == "private", "description": sys.argv[3], "has_issues": True, "has_wiki": False, "auto_init": False}))' \
        "$REPO_NAME" "$VISIBILITY" "$DESCRIPTION")
    created=$(api POST /user/repos "$body")
    html=$(printf '%s' "$created" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("html_url",""))' 2>/dev/null || true)
    if [ -z "$html" ]; then
        msg=$(printf '%s' "$created" | python3 -c 'import sys,json;print(json.load(sys.stdin).get("message",""))' 2>/dev/null || true)
        die "repository creation failed: ${msg:-unknown error}. A fine-grained token needs 'Administration: write' at account level."
    fi
    echo "   created: $html"
else
    die "unexpected status $code while checking for the repository"
fi

echo "== pushing =="
git remote remove origin 2>/dev/null || true
git remote add origin "https://github.com/$login/$REPO_NAME.git"
# The credential helper below reads GH_TOKEN from the environment at push time,
# so the token never lands in .git/config, in argv, or in shell history.
git -c credential.helper='!f() { echo "username=x-access-token"; echo "password=$GH_TOKEN"; }; f' \
    push -u origin HEAD:main
git branch --set-upstream-to=origin/main main >/dev/null 2>&1 || true

echo
echo "== done =="
echo "   https://github.com/$login/$REPO_NAME"
echo
echo "Remaining manual step: add the four signing secrets before tagging a release."
echo "   KEYSTORE_BASE64  base64 -w0 for your release.jks"
echo "   KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD"
echo "   Settings -> Secrets and variables -> Actions -> New repository secret"
