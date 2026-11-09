#!/usr/bin/env python3
"""Verify the packaged shortcuts point at the package that was actually built.

    python3 scripts/check-shortcuts.py <apk> <path-to-aapt2>

Two device-observed failures motivate this:

  * targetPackage hard-coded to the release id in a debug build -> the launcher logs
    "ActivityNotFoundException: Shortcut could not be started";
  * targetPackage missing -> the component is dropped, the intent resolves implicitly
    and the user gets a chooser instead of the app.

Neither is visible from compiling, linting or unit tests, so it is checked against the
built APK here.
"""
import re
import subprocess
import sys


def run(cmd):
    out = subprocess.run(cmd, capture_output=True, text=True)
    return out.returncode, out.stdout + out.stderr


def main(apk, aapt2):
    code, badging = run([aapt2, "dump", "badging", apk])
    if code != 0:
        print(badging)
        return 1
    match = re.search(r"package: name='([^']+)'", badging)
    if not match:
        print("could not read the package name from the APK")
        return 1
    package = match.group(1)

    code, tree = run([aapt2, "dump", "xmltree", "--file", "res/xml/shortcuts.xml", apk])
    if code != 0:
        print(tree)
        return 1

    targets = re.findall(r'targetPackage\(0x[0-9a-f]+\)="([^"]+)"', tree)
    classes = re.findall(r'targetClass\(0x[0-9a-f]+\)="([^"]+)"', tree)
    ids = re.findall(r'shortcutId\(0x[0-9a-f]+\)="([^"]+)"', tree)

    print(f"apk package: {package}")
    print(f"shortcuts: {len(ids)} ({', '.join(ids) or 'none'})")

    failures = []
    if not ids:
        failures.append("no shortcuts found in the packaged APK")
    if not targets:
        failures.append(
            "no targetPackage in the packaged shortcuts: the launcher will drop the "
            "component and show a chooser instead of opening the app"
        )
    for target in set(targets):
        if target != package:
            failures.append(
                f"shortcut targetPackage '{target}' does not match the built package "
                f"'{package}' - taps will fail with ActivityNotFoundException"
            )
    if not classes:
        failures.append("shortcuts have no targetClass")
    for cls in set(classes):
        if not cls.startswith(package.rsplit(".debug", 1)[0] + "."):
            failures.append(f"shortcut targetClass '{cls}' is outside the app's namespace")

    if failures:
        print("\nFAILED:")
        for failure in failures:
            print(f"  - {failure}")
        return 1
    print("\nshortcut targets: OK")
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit(__doc__)
    sys.exit(main(sys.argv[1], sys.argv[2]))
