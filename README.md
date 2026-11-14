# Website Hub (Android)

A small Kotlin/XML Android app that keeps popular and custom website shortcuts in one place and opens them in an in-app WebView.

## Features

- Google, YouTube, Wikipedia, Amazon, and Flipkart shortcuts in a two-column grid.
- Add custom sites with the + button; custom names and URLs are stored in `SharedPreferences`.
- In-app WebView with JavaScript, DOM storage, a page-load progress bar, and Back / Forward / Refresh / Home controls.
- HTTP(S)-only navigation, no cleartext traffic, and file/content access disabled in the WebView.

## Build

Open this repository's root in Android Studio and allow Gradle sync, then run the `app` configuration on a device/emulator (API 23+). The project uses Android Gradle Plugin 8.7.3, Kotlin 1.9.24, and compile SDK 35. A local JDK 17 and Android SDK platform 35 are required.

## Launcher icon note

The repository's existing root-level `Icon` file is empty and is not an Android image/resource, so it cannot be used directly as a launcher icon. This project supplies its own valid vector launcher drawable at `app/src/main/res/drawable/icon_launcher.xml`.
