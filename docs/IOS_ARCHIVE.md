# iOS Archive checklist

1. Copy `Config.local.xcconfig.example` → `Config.local.xcconfig` and set `TEAM_ID`.
2. Open `iosApp/iosApp.xcodeproj` in Xcode.
3. Target **iosApp** → Signing & Capabilities → Team = your Apple team; Bundle ID `com.memeeditor.app`.
4. Destination: Any iOS Device (arm64).
5. Product → Archive.
6. Organizer → Distribute App → App Store Connect → Upload.
7. Wait for processing in App Store Connect → TestFlight.

Deployment target is **iOS 16.0**. Export compliance key is in Info.plist.
