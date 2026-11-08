#!/usr/bin/env python3
"""Fail if the bundled licence text no longer matches THIRD-PARTY-NOTICES.md.

    python3 scripts/check-notices.py

Cheap drift guard: the licence screen reads res/raw/notices.txt, which is generated
from the markdown by scripts/make-notices.sh. If someone edits one and not the other,
the app shows stale attributions while the repository looks correct.
"""
import pathlib
import re
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
MD = ROOT / "THIRD-PARTY-NOTICES.md"
RAW = ROOT / "app/src/main/res/raw/notices.txt"


def normalise(text):
    text = re.sub(r"\[([^\]]*)\]\([^)]*\)", r"\1", text)
    text = text.replace("**", "").replace("`", "")
    return "\n".join(line.rstrip() for line in text.strip().splitlines())


def main():
    if not MD.exists() or not RAW.exists():
        print("missing THIRD-PARTY-NOTICES.md or res/raw/notices.txt")
        return 1
    bundled = RAW.read_text(encoding="utf-8")
    body = normalise(MD.read_text(encoding="utf-8"))
    if body not in bundled:
        print("res/raw/notices.txt does not contain the current THIRD-PARTY-NOTICES.md.")
        print("Run scripts/make-notices.sh and commit the result.")
        return 1
    print(f"notices in sync ({len(bundled)} bytes bundled)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
