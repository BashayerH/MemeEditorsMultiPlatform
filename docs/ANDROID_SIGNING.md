# Android upload signing

1. Copy `keystore.properties.example` → `keystore.properties` at the repo root (gitignored).
2. Generate a keystore (or use the local one under `signing/` if you created it for testing):

```bash
mkdir -p signing
keytool -genkeypair -v \
  -keystore signing/meme-upload.jks \
  -storetype PKCS12 \
  -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass YOUR_STORE_PASS \
  -keypass YOUR_KEY_PASS \
  -dname "CN=MemeEditor Upload, O=MemeEditor, C=US"
```

3. Point `storeFile`, passwords, and `keyAlias` in `keystore.properties`.
4. Build: `./gradlew :composeApp:bundleRelease`
5. AAB: `composeApp/build/outputs/bundle/release/composeApp-release.aab`

**Never commit** `*.jks`, `*.keystore`, or `keystore.properties`. Back up the upload key — losing it blocks Play updates unless you use Play App Signing recovery.
