# Key management

The release key is the whole game. Lose it and you can never update an installed copy. Leak it
and someone else can ship a build Android will accept as an update.

## 1. Generate it (owner, offline, once)

```bash
keytool -genkeypair -v -keystore pankxu-release.jks -alias release \
        -keyalg RSA -keysize 4096 -validity 10000
keytool -list -v -keystore pankxu-release.jks -alias release
```

Store it offline. **Two backups, two locations**, both encrypted. Never in this repository —
`.gitignore` blocks `*.jks` and `*.keystore`, and the CI secret is base64 in the repository
settings, not a file.

## 2. Back up, then prove the backup works (X-17)

A backup you have never restored is a guess. Test it:

```bash
# export a portable copy
keytool -importkeystore -srckeystore pankxu-release.jks -destkeystore backup.p12 \
        -deststoretype PKCS12

# restore it under a different name and compare the certificate
keytool -importkeystore -srckeystore backup.p12 -destkeystore restored.jks \
        -srcstoretype PKCS12
keytool -list -v -keystore restored.jks -alias release | grep 'SHA256:'
keytool -list -v -keystore pankxu-release.jks -alias release | grep 'SHA256:'
```

The two digests must be identical. Then sign a throwaway APK with the restored copy and
verify it with `apksigner`. Record the date and result here:

| Date | Backup test result | Fingerprint matched |
|---|---|---|
| *(not yet run)* | | |

## 3. Rotate without stranding users (X-18)

Android 13+ supports a **signing lineage** carried in the v3 block, letting a new key be
trusted by installs signed with the old one:

```bash
# 1. new key
keytool -genkeypair -v -keystore pankxu-release-2.jks -alias release \
        -keyalg RSA -keysize 4096 -validity 10000

# 2. lineage from old to new
apksigner rotate --out lineage.bin \
        --old-signer --ks pankxu-release.jks --ks-key-alias release \
        --new-signer --ks pankxu-release-2.jks --ks-key-alias release

# 3. sign with the lineage so existing installs accept the update
apksigner sign --ks pankxu-release-2.jks --ks-key-alias release \
        --lineage lineage.bin --out pankxu-release.apk pankxu-release-unsigned.apk
```

Without the lineage file, a rotated key is a different app to Android and every existing
install must be uninstalled first. Keep `lineage.bin` with the same care as the keys.

## 4. If the key is compromised

1. Assume every artefact signed with it is suspect; withdraw the releases.
2. Publish a notice in the README and on every release page.
3. Rotate with a lineage **only if the old key is still in your control** — if it leaked,
   an attacker can sign a lineage too, so a clean break (new package-adjacent release, or
   accept the uninstall requirement) is honest.
4. Tell Play (if ever used) immediately.

## 5. Publish the fingerprint with every release (X-19)

`scripts/sign-release.sh` writes `artifacts/certificate-fingerprint.txt`, and the release
workflow attaches `signature.txt`. Users compare it before installing; see `docs/INSTALL.md`.
