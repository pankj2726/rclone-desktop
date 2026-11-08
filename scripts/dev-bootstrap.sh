#!/usr/bin/env bash
# Restore the bits a sandbox snapshot does not preserve.
#
# The workspace snapshot keeps file *contents* but drops the executable bit, drops
# .git/config, and drops ~/.config. Every restore therefore costs the same five
# fixes; this script is those five fixes.
#
# Safe to run any number of times.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "== executable bits =="
chmod +x gradlew scripts/*.sh 2>/dev/null || true
find scripts -name '*.sh' -exec chmod +x {} + 2>/dev/null || true
echo "   restored"

echo "== git identity (repo-local; .git/config is not snapshotted) =="
if ! git config user.email >/dev/null 2>&1; then
    git config user.email "agent@arena.local"
    git config user.name "Arena Agent"
    echo "   set repo-local identity"
else
    echo "   already set"
fi

echo "== git remote (also dropped with .git/config) =="
if ! git remote get-url origin >/dev/null 2>&1; then
    git remote add origin "${REPO_URL:-https://github.com/pankj2726/rclone-desktop.git}"
    echo "   origin re-added: $(git remote get-url origin)"
else
    echo "   already present: $(git remote get-url origin)"
fi

echo "== line endings guard =="
git config core.autocrlf false 2>/dev/null || true

echo "== state =="
printf '   HEAD    : %s\n' "$(git log --oneline -1 2>/dev/null || echo 'no commits')"
printf '   tracked : %s files\n' "$(git ls-files | wc -l)"
printf '   gradlew : %s\n' "$([ -x gradlew ] && echo executable || echo 'NOT executable')"
for f in scripts/*.sh; do
    printf '   %-26s %s\n' "$f" "$([ -x "$f" ] && echo executable || echo 'NOT executable')"
done
