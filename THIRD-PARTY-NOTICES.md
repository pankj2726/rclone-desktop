# Third-party notices

The app is deliberately small. These are all of its runtime dependencies.

| Component | Version | Licence | Used for |
|---|---|---|---|
| `com.google.androidbrowserhelper:androidbrowserhelper` | 2.7.4 | Apache-2.0 | Trusted Web Activity launcher, Custom Tab fallback, inbound URL validation |
| `androidx.browser:browser` | 1.10.0 | Apache-2.0 | Custom Tabs APIs used by the helper |
| AndroidX transitive libraries (`androidx.core`, `androidx.startup`, …) | via the above | Apache-2.0 | Plumbing pulled in by the two above |

Test-only:

| Component | Version | Licence |
|---|---|---|
| `junit:junit` | 4.13.2 | EPL-1.0 |
| `org.hamcrest:hamcrest-core` | 1.3 | BSD-3-Clause |

No analytics, advertising, crash-reporting or attribution SDKs are present. The full resolved
graph for a given build is attached to every release as `dependency-tree.txt`; regenerate the
authoritative licence list with:

```bash
./gradlew :app:dependencies --configuration releaseRuntimeClasspath
```

A CycloneDX SBOM is **not yet attached** to releases — see `docs/LIMITATIONS.md`.

Arena, arena.ai and any related marks belong to their owner. Nothing here grants a licence to
them, and this project claims none.
