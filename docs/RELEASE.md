# Release runbook (Play + App Store)

See also: [STORE_IDENTITY.md](STORE_IDENTITY.md), [ANDROID_SIGNING.md](ANDROID_SIGNING.md), [PLAY_LISTING.md](PLAY_LISTING.md), [APP_STORE_LISTING.md](APP_STORE_LISTING.md), [IOS_ARCHIVE.md](IOS_ARCHIVE.md), [DEVICE_QA.md](DEVICE_QA.md).

## Locked v1 identity

| | |
|--|--|
| Android applicationId | `com.memeeditor.app` |
| iOS bundle id | `com.memeeditor.app` |
| Version | 1.0.0 (1) |
| Latin meme font | Anton (OFL) — Impact removed |

## Your remaining offline steps

1. Host `docs/privacy-policy.html` → set URL in `StoreConfig.PRIVACY_POLICY_URL`  
2. Set Apple `TEAM_ID` in `Config.local.xcconfig`  
3. Change upload keystore passwords if the local defaults were only for CI/dev  
4. Play Console: create app → closed test → upload AAB  
5. Xcode Archive → TestFlight → App Review  
6. Complete [DEVICE_QA.md](DEVICE_QA.md) on hardware  
