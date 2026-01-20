# Android TV Kiosk App

A React Native application for Android TV (specifically Xiaomi Mi Box S Gen 3) that displays a web page in kiosk mode with auto-start on boot functionality.

## Features

- Display a configurable web URL in fullscreen
- **In-app settings menu** - Change URL without rebuilding (5 back presses)
- **Persistent configuration** - URL saved across app restarts
- Auto-start on device boot
- Prevent user from exiting the app (with 5-tap menu access)
- Keep screen awake
- Handle crash recovery (auto-restart)
- Override home button behavior
- Render at proper resolution (1080p/4K)
- Hide Android navigation bar and status bar

## Technical Requirements

- React Native 0.73+
- Target Android 9.0+ (API 28+)
- Support for Android TV/Mi Box S
- Optimize for TV input (remote control)

## Project Structure

```
Kiosk/
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/androidtvkiosk/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── MainApplication.java
│   │   │   │   ├── BootReceiver.java
│   │   │   │   ├── KioskModule.java
│   │   │   │   └── KioskPackage.java
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
├── src/
│   ├── components/
│   │   └── KioskWebView.tsx
│   └── config.ts
├── App.tsx
└── package.json
```

## Download Pre-built APK

The easiest way to get started is to download a pre-built APK from GitHub Actions:

1. Go to the [Actions tab](../../actions) in this repository
2. Click on the latest successful workflow run
3. Scroll down to "Artifacts" section
4. Download **app-release**
5. Extract the ZIP file to get `app-release.apk`
6. Install on your device: `adb install app-release.apk`

The APK is automatically built whenever code is pushed to the repository.

## Installation & Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Configure URL

Edit `src/config.ts` and set your desired URL:

```typescript
export default {
  kiosk_url: 'https://your-url-here.com',
  enable_debugging: false,
};
```

### 3. Build and Install

```bash
# For debug build
npx react-native run-android

# For release build
cd android
./gradlew assembleRelease
```

### 4. Post-Installation Setup (via ADB)

After installing the app, run these ADB commands for Mi Box S:

```bash
# Grant system alert window permission
adb shell appops set com.androidtvkiosk SYSTEM_ALERT_WINDOW allow

# Disable battery optimization
adb shell dumpsys deviceidle whitelist +com.androidtvkiosk

# Optional: Set as default launcher (forces boot launch)
adb shell cmd package set-home-activity com.androidtvkiosk/.MainActivity

# Optional: Disable Mi Box launcher for stricter kiosk
adb shell pm disable-user --user 0 com.mitv.tvhome
```

### 5. Enable Developer Options on Mi Box S

1. Settings → Device Preferences → About
2. Click on "Build" 7 times
3. Go to Developer Options
4. Set "Smallest width" to 960 or 1280

## Using the App

### Accessing the Settings Menu

The app includes a hidden menu that can be accessed without exiting kiosk mode:

1. **Press the back button 5 times quickly** (within 3 seconds)
2. A side menu will slide in from the left
3. From the menu you can:
   - **Settings**: Change the kiosk URL without rebuilding the app
   - **Exit App**: Close the application

### Changing the Kiosk URL

1. Press back button 5 times to open the menu
2. Tap "Settings"
3. Enter the new URL (must include http:// or https://)
4. Tap "Save & Reload"
5. The WebView will automatically reload with the new URL
6. The URL is saved and will persist across app restarts

**Note**: The default URL on first install is `https://example.com` - make sure to change this!

## Testing Checklist

- [ ] App launches correctly
- [ ] WebView displays the URL properly at full resolution
- [ ] Back button is disabled
- [ ] Navigation bar is hidden
- [ ] Screen stays awake
- [ ] App auto-starts on reboot
- [ ] App recovers from crashes
- [ ] Remote control works for web interaction

## Troubleshooting

### App doesn't auto-start on boot

- Verify RECEIVE_BOOT_COMPLETED permission
- Run ADB whitelist command
- Check logcat: `adb logcat | grep BootReceiver`

### Low resolution display

- Set smallest width in Developer Options
- Verify userAgent in WebView
- Check viewport settings in your web page

### Can still exit the app

- Verify KioskModule is registered in MainApplication
- Set app as default launcher via ADB
- Check if navigation bar hiding is working

### WebView not loading

- Check internet connectivity
- Verify URL in config.ts
- Check logcat for errors: `adb logcat | grep chromium`

## Development

### Running in Development Mode

```bash
# Start Metro bundler
npx react-native start

# In another terminal, run the app
npx react-native run-android
```

### Building Release APK

```bash
cd android
./gradlew assembleRelease

# APK will be at: android/app/build/outputs/apk/release/app-release.apk
```

### Installing Release APK

```bash
adb install android/app/build/outputs/apk/release/app-release.apk
```

## Dependencies

- `react-native-webview`: ^13.6.4
- `react-native-keep-awake`: ^4.0.0
- `@react-native-community/hooks`: ^3.0.0

## Configuration

### Changing the Kiosk URL

Edit `src/config.ts`:

```typescript
export default {
  kiosk_url: 'https://example.com', // Change to your URL
  enable_debugging: false,
};
```

### Modifying Kiosk Behavior

Edit `android/app/src/main/java/com/androidtvkiosk/MainActivity.java` to customize button blocking behavior.

Edit `src/components/KioskWebView.tsx` to customize WebView settings.

## Future Enhancements

- Remote configuration via API
- Offline mode with cached content
- Health monitoring and auto-restart
- Multi-page support with navigation
- Screenshot/logging capabilities
- OTA update support

## License

MIT

## Support

For issues and questions, please check the troubleshooting section above or create an issue in the repository.
