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

## Host privacy URL (GitHub Pages)

1. Repo → **Settings** → **Pages**
2. Build and deployment → Source: **Deploy from a branch**
3. Branch: **main**, folder: **/docs** → Save
4. Wait ~1 minute, then open:  
   https://bashayerh.github.io/MemeEditorsMultiPlatform/

That URL is already set in `StoreConfig.PRIVACY_POLICY_URL`.  
`docs/index.html` is the privacy policy (GitHub Pages always looks for `index.html`; without it you only see the repo README).
