# App Store Connect listing (MemeEditor)

**Bundle ID:** `com.memeeditor.app`  
**Name:** MemeEditor  
**Primary category:** Photo & Video  
**Price:** Free  

## Subtitle (≤30 chars)

Templates, text, share

## Description

MemeEditor helps you make classic image macros in seconds.

• Browse meme templates  
• Or pick a photo from your gallery  
• Add, move, pinch-scale, and color text  
• Share via any app, or save to Photos  

Everything stays on your device. No account. No ads. No tracking.

## Keywords (≤100 chars, comma-separated)

meme,editor,template,caption,photo,funny,share,gallery,text

## URLs

- Support URL: your support page or email mailto landing  
- Privacy Policy URL: hosted `docs/privacy-policy.html`  
- Marketing URL: optional  

## App Privacy (nutrition labels)

- Data Not Collected from you for analytics/ads/account  
- **Photos or Videos:** used for App Functionality when user saves to Photos (write) / picks an image — not linked to identity, not used for tracking  

## Age rating

Answer questionnaire; typically 4+ / 9+ depending on UGC answers.

## Export compliance

`ITSAppUsesNonExemptEncryption = false` is set in Info.plist — select corresponding answer in App Store Connect.

## Screenshots

Required modern sizes (at least):
- 6.7" iPhone  
- 6.1" iPhone  

Show: template gallery, editor with text, share/save.

## Upload path

1. Set `TEAM_ID` in `iosApp/Configuration/Config.local.xcconfig`  
2. Open `iosApp/iosApp.xcodeproj` → select your Team  
3. Product → Archive → Distribute App → App Store Connect  
4. Add build to TestFlight → internal test  
5. Submit version 1.0.0 for Review  
