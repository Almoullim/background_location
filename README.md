# Background Location

A Flutter plugin to get location updates in the background for both Android and iOS (Requires iOS 10.0+). Uses `CoreLocation` for iOS and `FusedLocationProvider` for Android

## Getting Started

**1:** Add this to your `pubspec.yaml` file:

```yaml
dependencies:
  background_location: ^0.13.0
```

**2:** Install packages from the command line:

```bash
flutter pub get
```

Alternatively, your editor might support flutter packages get. Check the docs for your editor to learn more.

## Configuration

For using background_location package you need to add permissions to use location service. **Add** these permission to your platform.

### iOS Platform Permission

in `ios/Runner/Info.plist` add:

```xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>[Describe your purpose for using this permission]</string>
<key>NSLocationAlwaysUsageDescription</key>
<string>[Describe your purpose for using this permission]</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>[Describe your purpose for using this permission]</string>
<key>UIBackgroundModes</key>
<array>
    <string>fetch</string>
    <string>location</string>
</array>
```

> *Note*: You need to describe your purpose for using the permission in to `string` value. If not, attempts to access the resource fail, and might even cause your app to crash. And when publishing to the App Store, this can cause your app to be rejected by Apple. Refer to [Provide a purpose string (Apple Docummentation)](https://developer.apple.com/documentation/uikit/protecting_the_user_s_privacy/requesting_access_to_protected_resources#3037322).

### Android Platform Permission

In `android/app/src/main/AndroidManifest.xml` add:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/> 
```

## How to use

Import the package where you wanna use it.

```dart
import 'package:background_location/background_location.dart';
```

Request permissions from the user. You can use [permission_handler](https://pub.dev/packages/permission_handler) for this.

Set the notification title, message and icon **(Android only)**. Use `await` or `.then` if you wanna start the location service immediately after because it's an asynchronous method

```dart
BackgroundLocation.setAndroidNotification(
  title: "Notification title",
  message: "Notification message",
  icon: "@mipmap/ic_launcher",
);
```

Set the interval between localizations in milliseconds **(Android only)**. Use `await` or `.then` if you wanna start the location service immediately after because it's an asynchronous method

```dart
BackgroundLocation.setAndroidConfiguration(1000);
```

Start the location service. This will also ask the user for permission if not asked previously by another package.

```dart
// To ensure that previously started services have been stopped, if desired
BackgroundLocation.stopLocationService();

// Then start the service
BackgroundLocation.startLocationService();
```

> *Note:* There is currently an open issue (#10) where, if the location service is started multiple times, the location callback will get called repeatedly. This can be worked around by calling BackgroundLocation.stopLocationService(); to stop any previous running services (such as from a previous run of the app) before starting a new one.

Start location service by specifying `distanceFilter`. Defaults to `0` if not specified

```dart
BackgroundLocation.startLocationService(distanceFilter : 10);
```

You can also force the use of Android `LocationManager` instead of Google's `FusedLocationProvider` by setting the `forceAndroidLocationManager` property to `true`. If not specified, this defaults to `false`, which uses `FusedLocationProvider` if it is available, treating `LocationManager` as a fallback. This setting has no effect on iOS devices.

```dart
BackgroundLocation.startLocationService(forceAndroidLocationManager: true);
```

`getLocationUpdates` will triggered whenever the location is updated on the device. Provide a callback function to `getLocationUpdates` to handle location updates.

```dart
BackgroundLocation.getLocationUpdates((location) {
  print(location);
});
```

location is a `Class` exposing the following properties.

```dart
double latitude;
double longitude;
double altitude;
double bearing;
double accuracy;
double speed;
double time;
bool isMock;
```

To stop listening to location changes you can execute.

```dart
BackgroundLocation.stopLocationService();
```

<!-- TODO: Fix example -->
<!-- ## Example -->
<!-- **[Complete working application Example](https://github.com/almoullim/background_location/tree/master/example)** -->
