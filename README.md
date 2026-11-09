# GOD OF WATCH

Android companion app prototype for Amazfit GTS 2.

## Current scope
- Dark, gold-accented companion UI
- Watch-face gallery and editor preview foundation
- Bluetooth permission and device discovery groundwork
- GitHub Actions debug APK build

## Important GTS 2 compatibility note
Amazfit GTS 2 uses Zepp's device ecosystem and watch-face transfer formats/protocols. Android Bluetooth discovery alone does not install a watch face. Direct transfer will be implemented only after validating the GTS 2 protocol and compatible face package format on a real device. Until then, the app must not claim a face was installed.

## Build
GitHub Actions runs Gradle assembleDebug on pushes and pull requests. The debug APK is uploaded as an Actions artifact.
