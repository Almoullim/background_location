# Background Location

A Flutter plugin to get location updates in the background for both Android and iOS (Requires iOS 10.0+). Uses `CoreLocation` for iOS and `FusedLocationProvider` for Android

PS: This project was originaly created by [@shah-xad](https://github.com/shah-xad/flutter_background_location) for Android only.

## Getting Started

**1:** Add this to your package's pubspec.yaml file:

```yaml
dependencies:
  background_location: ^0.0.6
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
```

To stop listening to location changes you can execute.

```dart
BackgroundLocation.stopLocationService();
```

## Example

**[Complete working application Example](https://github.com/almoullim/background_location/tree/master/example)**

## Todo

- [ ] Add support for manually asking for permission.
- [ ] Add support for checking the permission status.
- [ ] Add support for getting the last location once without listening to location updates.
- [ ] Add support for chosing the rate at the which the location is fetched based on time and distance.

## Contributers

- Ali Almoullim ([@almoullim](https://github.com/Almoullim)) -- iOS implementation
- Shahzad Akram ([@shah-xad](https://github.com/shah-xad)) -- Android implementation
