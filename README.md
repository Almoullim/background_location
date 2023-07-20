# Background Location

A Flutter plugin to get location updates in the background for both Android and iOS (Requires iOS 10.0+). Uses `CoreLocation` for iOS and `FusedLocationProvider` for Android

## Getting Started

**1:** Add this to your package's pubspec.yaml file:

```yaml
dependencies:
  background_location: ^0.11.8
```

**2:** Install packages from the command line:

```bash
$ flutter packages get
```

Alternatively, your editor might support flutter packages get. Check the docs for your editor to learn more.

## How to use

Import the package where you wanna use it.

```dart
import 'package:background_location/background_location.dart';
```

Request permissions from the user. You can use [permission_handler](https://pub.dev/packages/permission_handler) for this

Set the notification title, message and icon **(Android only)**. Use `await` or `.then` if you wanna start the location service immediatly after becuase its an asynchronous method

```dart
BackgroundLocation.setAndroidNotification(
	title: "Notification title",
        message: "Notification message",
        icon: "@mipmap/ic_launcher",
);
```

Set the interval between localisations in milliseconds **(Android only)**. Use `await` or `.then` if you wanna start the location service immediatly after becuase its an asynchronous method

```dart
BackgroundLocation.setAndroidConfiguration(1000);
```

Start the location service. This will also ask the user for permission if not asked previously by another package.

```dart
BackgroundLocation.startLocationService();
```

Start location service by specifying `distanceFilter`. Defaults to `0` if not specified

```dart
BackgroundLocation.startLocationService(distanceFilter : 10);
```

You can also force the use of Android `LocationManager` instead of Google's `FusedLocationProvider` by setting the `forceAndroidLocationManager` property to `true`. If not specified, this defaults to `false`, which uses `FusedLocationProvider` if it is available, treating `LocationManager` as a fallback. This setting has no effect on iOS devices.

```dart
BackgroundLocation.startLocationService(forceAndroidLocationManager: true);
```

`getLocationUpdates` will trigger everytime the location updates on the device. Provide a callback function to `getLocationUpdates` to handle location update.

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

Make sure to delcare all required permissions for both your android and ios app

info.plist
```xml
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>This app needs access to location.</string>
<key>NSLocationAlwaysUsageDescription</key>
<string>This app needs access to location.</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app needs access to location.</string>
<key>UIBackgroundModes</key>
<array>
	<string>fetch</string>
	<string>location</string>
</array>
```

AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/> 
```
<!-- TODO: Fix example -->
<!-- ## Example -->
<!-- **[Complete working application Example](https://github.com/almoullim/background_location/tree/master/example)** -->
