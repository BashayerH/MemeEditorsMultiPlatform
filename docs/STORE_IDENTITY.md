# Store identity (locked for v1.0.0)

| Field | Value |
|-------|--------|
| Android `applicationId` | `com.memeeditor.app` |
| Android `namespace` (R class / sources) | `com.example.memeeditor` (unchanged) |
| iOS bundle id | `com.memeeditor.app` |
| Display name | MemeEditor |
| Version | 1.0.0 (versionCode / CURRENT_PROJECT_VERSION = 1) |
| Privacy policy (source) | [docs/privacy-policy.md](privacy-policy.md) / [privacy-policy.html](privacy-policy.html) |
| Privacy policy URL (in app) | Update `StoreConfig.PRIVACY_POLICY_URL` after hosting |

## Apple Team ID

1. Copy `iosApp/Configuration/Config.local.xcconfig.example` → `Config.local.xcconfig`
2. Set `TEAM_ID=XXXXXXXXXX` from [Apple Developer → Membership](https://developer.apple.com/account)
3. `Config.local.xcconfig` is gitignored

## Host privacy URL

Publish `docs/privacy-policy.html` (GitHub Pages, Notion public page, or your domain), then set the public HTTPS URL in:

`composeApp/.../core/StoreConfig.kt` → `PRIVACY_POLICY_URL`
