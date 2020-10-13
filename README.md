# Background Location

A Flutter plugin to get location updates in the background for both Android and iOS (Requires iOS 10.0+). Uses `CoreLocation` for iOS and `FusedLocationProvider` for Android

## Getting Started

**1:** Add this to your package's pubspec.yaml file:

```yaml
dependencies:
  background_location: ^0.2.1
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

Request permissions from the user.

```dart
BackgroundLocation.getPermissions(
  onGranted: () {
    // Start location service here or do something else
  },
  onDenied: () {
    // Show a message asking the user to reconsider or do something else
  },
);
```

You can check if you have permissions at anytime with `checkPermissions()`

```dart
BackgroundLocation.checkPermissions().then((status) {
  // Check status here
});

```

Set the notification title (Android). Use `await` or `.then` if you wanna start the location service immediatly after becuase its an asynchronous method

```dart
BackgroundLocation.setNotificationTitle("Test Title");
```

Start the location service. This will also ask the user for permission if not asked previously by another package.

```dart
BackgroundLocation.startLocationService();
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

## Example

**[Complete working application Example](https://github.com/almoullim/background_location/tree/master/example)**

## Todo

- [x] Add support for manually asking for permission.
- [x] Add support for checking the permission status.
- [ ] Add support for getting the last location once without listening to location updates.
- [ ] Add support for chosing the rate at the which the location is fetched based on time and distance.
