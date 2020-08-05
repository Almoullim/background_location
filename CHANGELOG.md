## 0.1.2

Added showsBackgroundLocationIndicator to iOS fixing [#29](https://github.com/Almoullim/background_location/issues/30#issuecomment-668540916)
Update permission handler to v5.0.1+1

## 0.1.1

Bug fixes

## 0.1.0

- `Location` class is now public and can imported
- `Location` class now contains a `toMap()` that will return JSON compatible map

## 0.0.11

Bug fixes

## 0.0.10

WIP: Add timestamp from location

## 0.0.9+3

Bug fixes

## 0.0.9+2

roll back on [22d08f58a2c0b29807ce9f8f20bb7f488fa3acad](https://github.com/Almoullim/background_location/commit/22d08f58a2c0b29807ce9f8f20bb7f488fa3acad) to fix background location service on android

## 0.0.9

Bump up permission_handler from ^3.2.1+ to ^4.0.0

## 0.0.7

Add `Future getPermissions(onGranted:onDenied:)`
Add `Future<PermissionStatus> checkPermissions()`

## 0.0.6

Allow Significant-change for longer location updates (iOS)

## 0.0.5

Fixed spelling mistakes.

## 0.0.4

Added iOS support.
