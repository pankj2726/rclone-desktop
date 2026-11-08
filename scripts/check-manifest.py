#!/usr/bin/env python3
"""Assert the merged manifest still honours the contracts the app depends on.

    python3 scripts/check-manifest.py merged-manifest.txt

Written after the first device run crashed: androidbrowserhelper's
LauncherActivity requires ManageDataLauncherActivity to be declared, and the
library does not declare it itself. Nothing in the previous CI run would have
caught that, because the app compiled and packaged perfectly - it only failed at
runtime. This check turns that class of bug into a build failure.

Input is `aapt2 dump xmltree --file AndroidManifest.xml <apk>`, the format the
build workflow already produces.
"""
import pathlib
import re
import sys

REQUIRED_ACTIVITIES = {
    # name: exported value, or None when the attribute is not asserted
    "what.thefcuk.pankxu.LaunchGateActivity": True,
    "what.thefcuk.pankxu.PankxuLauncherActivity": False,
    "com.google.androidbrowserhelper.trusted.ManageDataLauncherActivity": False,
    "com.google.androidbrowserhelper.trusted.ShortcutTrampolineActivity": True,
}

FORBIDDEN_PERMISSIONS = {"android.permission.INTERNET"}
REQUIRED_PERMISSIONS = {"android.permission.ACCESS_NETWORK_STATE"}


def main(path):
    text = open(path, encoding="utf-8", errors="replace").read()

    # Each activity block starts at an "E: activity" line; attributes follow.
    activities = {}
    current = None
    for line in text.splitlines():
        stripped = line.strip()
        if stripped.startswith("E: activity"):
            current = {"exported": None}
            continue
        if stripped.startswith("E: ") and not stripped.startswith("E: activity"):
            current = None
        if current is None:
            continue
        m = re.search(r'name\(0x01010003\)="([^"]+)"', stripped)
        if m:
            activities[m.group(1)] = current
            continue
        m = re.search(r"exported\(0x01010010\)=(true|false)", stripped)
        if m:
            current["exported"] = m.group(1) == "true"

    names = set()
    m = re.search(r"<manifest[^>]*package=\"([^\"]+)\"", text)
    for name in list(activities):
        names.add(name)

    failures = []

    def has(suffix):
        return [n for n in names if n == suffix or n.endswith("." + suffix)]

    for want, exported in REQUIRED_ACTIVITIES.items():
        hit = [n for n in names if n == want]
        if not hit:
            # applicationId suffix in the merged manifest is the real one
            hit = [n for n in names if n.endswith(want.split(".")[-1]) and "browserhelper" in want]
        if not hit:
            failures.append(f"missing activity: {want}")
            continue
        if exported is not None and activities[hit[0]]["exported"] is not exported:
            failures.append(
                f"{hit[0]}: exported={activities[hit[0]]['exported']}, expected {exported}"
            )

    perms = set(re.findall(r'name\(0x01010003\)="(android\.permission\.[A-Z_]+)"', text))
    for perm in sorted(REQUIRED_PERMISSIONS - perms):
        failures.append(f"missing permission: {perm}")
    for perm in sorted(FORBIDDEN_PERMISSIONS & perms):
        failures.append(f"unexpected permission: {perm} (the app must not hold INTERNET)")

    # Shortcut targets are checked against the built APK by scripts/check-shortcuts.py,
    # which is the only place the packaged package name is authoritative.

    print(f"activities declared: {len(names)}")
    for name in sorted(names):
        flag = "exported" if activities[name]["exported"] else "not exported"
        print(f"  {name}  [{flag}]")
    print(f"permissions: {', '.join(sorted(perms)) or 'none'}")

    if failures:
        print("\nFAILED:")
        for failure in failures:
            print(f"  - {failure}")
        return 1
    print("\nmanifest contract: OK")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "merged-manifest.txt"))
