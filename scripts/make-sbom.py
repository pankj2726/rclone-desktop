#!/usr/bin/env python3
"""Build a CycloneDX 1.5 SBOM from `gradlew :app:sbomCoordinates` output.

    python3 scripts/make-sbom.py coordinates.txt sbom.cdx.json

Only what Gradle actually resolved is listed: no component is invented, and each
library carries the SHA-256 of the exact file that was on the compile/runtime
classpath. Licences are deliberately not asserted here - the reviewed,
human-readable list is THIRD-PARTY-NOTICES.md.
"""
import hashlib
import json
import sys
import uuid
from datetime import datetime, timezone

NAME = "pankxu"
VERSION = "1.0.0"
APP_ID = "what.thefcuk.pankxu"


def parse(path):
    components, seen = [], set()
    with open(path, encoding="utf-8") as handle:
        for line in handle:
            line = line.rstrip("\n")
            if not line.startswith("COORD\t"):
                continue
            parts = line.split("\t")
            if len(parts) != 4:
                raise SystemExit(f"malformed row: {line!r}")
            _, coord, filename, digest = parts
            group, name, version = coord.split(":")
            ref = f"pkg:maven/{group}/{name}@{version}"
            if ref in seen:
                continue
            seen.add(ref)
            components.append(
                {
                    "type": "library",
                    "bom-ref": ref,
                    "group": group,
                    "name": name,
                    "version": version,
                    "purl": ref,
                    "hashes": [{"alg": "SHA-256", "content": digest}],
                    "properties": [{"name": "gradle:artifact", "value": filename}],
                }
            )
    components.sort(key=lambda c: (c["group"], c["name"], c["version"]))
    return components


def main():
    if len(sys.argv) != 3:
        raise SystemExit(__doc__)
    components = parse(sys.argv[1])
    if not components:
        raise SystemExit("no COORD rows found - refusing to write an empty SBOM")
    seed = "".join(c["purl"] + c["hashes"][0]["content"] for c in components)
    serial = uuid.UUID(hashlib.sha256(seed.encode()).hexdigest()[:32])
    document = {
        "$schema": "http://cyclonedx.org/schema/bom-1.5.schema.json",
        "bomFormat": "CycloneDX",
        "specVersion": "1.5",
        "serialNumber": f"urn:uuid:{serial}",
        "version": 1,
        "metadata": {
            "timestamp": datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
            "tools": [
                {
                    "vendor": "pankxu",
                    "name": "make-sbom.py",
                    "version": "1",
                }
            ],
            "component": {
                "type": "application",
                "bom-ref": f"pkg:apk/{APP_ID}@{VERSION}",
                "name": NAME,
                "version": VERSION,
                "purl": f"pkg:apk/{APP_ID}@{VERSION}",
                "properties": [
                    {"name": "android:versionCode", "value": "10000"},
                    {"name": "android:minSdk", "value": "26"},
                    {"name": "android:targetSdk", "value": "36"},
                ],
            },
        },
        "components": components,
    }
    with open(sys.argv[2], "w", encoding="utf-8") as handle:
        json.dump(document, handle, indent=2)
        handle.write("\n")
    print(f"wrote {sys.argv[2]}: {len(components)} components")


if __name__ == "__main__":
    main()
