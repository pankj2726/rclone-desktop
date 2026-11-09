#!/usr/bin/env bash
# S2-04 / X-24 -- build app/src/main/res/raw/notices.txt from THIRD-PARTY-NOTICES.md.
#
# The file is generated rather than hand-written: the dependency list changes, and a
# licence screen that quietly falls out of date is worse than none. Run this after
# editing THIRD-PARTY-NOTICES.md; scripts/check-notices.py fails the build if the two
# drift apart.
set -euo pipefail
cd "$(dirname "$0")/.."

SRC=THIRD-PARTY-NOTICES.md
OUT=app/src/main/res/raw/notices.txt

{
  echo "PANKXU - OPEN-SOURCE NOTICES"
  echo
  echo "This app is built from the components listed below. Full notices are in"
  echo "THIRD-PARTY-NOTICES.md in the source repository."
  echo
  # Strip markdown emphasis and links: this text is shown in a plain TextView.
  sed -e 's/\[\([^]]*\)\](\([^)]*\))/\1/' \
      -e 's/\*\*//g' \
      -e 's/`//g' \
      "$SRC"
} > "$OUT"

printf 'wrote %s (%s bytes, %s lines)\n' "$OUT" "$(stat -c%s "$OUT")" "$(wc -l < "$OUT")"
