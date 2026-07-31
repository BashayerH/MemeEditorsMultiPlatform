# Device QA checklist (pre-submit)

Run on one physical Android phone and one iPhone.

## Cold start
- [ ] Splash / launch branding looks correct  
- [ ] Launcher / home screen icon is the new app icon  

## Gallery
- [ ] Templates load in stable order  
- [ ] Privacy policy icon opens hosted URL (after you host + update `StoreConfig.PRIVACY_POLICY_URL`)  
- [ ] FAB picks a photo; processing overlay appears; editor opens  

## Editor
- [ ] Add text → Latin uses Anton; Arabic uses Tajawal  
- [ ] Edit via Edit button / long-press / double-tap  
- [ ] Color swatches apply  
- [ ] Delete shows Undo snackbar; Undo restores  
- [ ] System back (Android) asks to leave when dirty  
- [ ] Share shows exporting overlay; share sheet opens; no forced “back to main” on cancel  
- [ ] Save to gallery succeeds; permission deny shows Settings path (iOS)  

## Fail paths
- [ ] Deny Photos add → clear message, no crash  
- [ ] Corrupt/unavailable custom image → Retry / Back  

## Font / export
- [ ] Shared / saved JPEG matches on-screen text style  
