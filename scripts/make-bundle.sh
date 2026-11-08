#!/usr/bin/env bash
# R1/P0-05: write a complete git bundle of the repo into artifacts/.
# A bundle is a single-file, self-contained copy of every ref — the recovery path
# after an environment reset. Never write deliverables into dist/, build/, out/ or target/.
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p artifacts
stamp=$(date -u +%Y%m%dT%H%M%SZ)
out="artifacts/repo-$stamp.bundle"
git bundle create "$out" --all
git bundle verify "$out" >/dev/null
printf 'wrote %s (%s)\n' "$out" "$(du -h "$out" | cut -f1)"
