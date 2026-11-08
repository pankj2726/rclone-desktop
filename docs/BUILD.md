# Building

## Prerequisites

| Tool | Version | Why |
|---|---|---|
| JDK | **17** | AGP 9.5.1 requires it |
| Android SDK platform | **36** | compileSdk/targetSdk 36 |
| Build-tools | **36.0.0** | AGP 9.5.1 default |
| Gradle | **9.6.0** | pinned; the wrapper downloads and checksum-verifies it |

Install the SDK pieces with `sdkmanager`:

```bash
sdkmanager --install "platforms;android-36" "build-tools;36.0.0" "platform-tools"
yes | sdkmanager --licenses
```

Nothing else is needed. There is no NDK, no native code, and therefore no 16 KB
page-size work to do (section 7 checklist item, confirmed not applicable).

## Build

```bash
./gradlew :app:assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:testDebugUnitTest    # allow-list unit tests
./gradlew :app:lintDebug            # lint report
./gradlew :app:assembleRelease      # unsigned unless signing env vars are set
```

### Resource limits (R10)

`gradle.properties` already sets `-Xmx1536m`, `workers.max=2`, and `--no-daemon` is
used in every CI command. On a machine with less than about 2 GB free, Gradle may be
killed by the OOM reaper; that is a memory problem, not a code problem.

## Release signing

The keystore never enters this repository. Two supported routes:

**Locally, by the key holder:**

```bash
export PANKXU_KEYSTORE_PATH=~/keys/pankxu-release.jks
export PANKXU_KEYSTORE_PASSWORD='…'
export PANKXU_KEY_ALIAS='release'
export PANKXU_KEY_PASSWORD='…'
./scripts/sign-release.sh
```

The script builds, aligns, verifies, and writes `artifacts/pankxu-release.apk`,
`SHA256SUMS.txt`, and `certificate.txt`. It prints only the certificate digest.

**In CI (GitHub Actions):** add four repository secrets —
`KEYSTORE_BASE64` (`base64 -w0 release.jks`), `KEYSTORE_PASSWORD`, `KEY_ALIAS`,
`KEY_PASSWORD`. `.github/workflows/release.yml` refuses to build if the keystore
secret is missing, rather than quietly producing an unsigned release.

## Generating the release key (run this yourself, offline)

```bash
keytool -genkeypair -v -keystore pankxu-release.jks -alias release \
        -keyalg RSA -keysize 4096 -validity 10000
keytool -list -v -keystore pankxu-release.jks -alias release
```

Keep two offline backups. Losing this key means you can never update an installed
copy of the app — Android will refuse an update signed by a different key.

## Reproducibility (P7-05)

`gradle/wrapper/gradle-wrapper.properties` pins the Gradle distribution by SHA-256.
Dependency versions are pinned in `gradle/libs.versions.toml`. A byte-identical rebuild
is *not* claimed: APK signing and timestamps make that false by default.

## Recovery

```bash
git bundle verify artifacts/repo-*.bundle
git clone artifacts/repo-YYYYMMDDTHHMMSSZ.bundle restored/
```

`./scripts/make-bundle.sh` writes a fresh bundle after every milestone (R1).
