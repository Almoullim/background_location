### 0.13.2

- Fixed issues with android namespaces
- resolve flutter analyze lint errors and update flutter lint
- Clarify permissions requirements earlier in the documentation [#226](https://github.com/Almoullim/background_location/pull/#226)

### 0.13.1

- Fix License Typo [#201](https://github.com/Almoullim/background_location/pull/#201)
- Fix API issues stemming from Android 13/14 changes to location services [#210](https://github.com/Almoullim/background_location/pull/#210)

### 0.13.0

- Android upgrade and location running indicator [#196](https://github.com/Almoullim/background_location/pull/196)

### 0.12.0
- Android: Raise notification importance [#163](https://github.com/Almoullim/background_location/issues/167)

### 0.11.8
- Upgrade gradle (for compatibility) and upgrade location services (fixes crashes) #183

### 0.9.0
- Android: Set "exported" manifest setting to false [#163](https://github.com/Almoullim/background_location/pull/163)
- Fixed an overrides of onRequestPermissionsResult [#151](https://github.com/Almoullim/background_location/pull/151)
- Android: Update fastest interval during config change [#165](https://github.com/Almoullim/background_location/pull/165)

### 0.8.1
Bug Fixes

### 0.8.0
Bug Fixes

### 0.7.0
Implement android embedding v2 [#118](https://github.com/Almoullim/background_location/pull/118)

### 0.6.1
Only starts showing notification on Android after setAndroidNotification

### 0.6.0
Removed ACCESS_BACKGROUND_LOCATION permission and permission_handler plugin

### 0.5.0
Added support for Null safety

### 0.4.1
Bug fixes

### 0.4.0
Added support for choosing the rate at the which the location is fetched based on distance. [#74](https://github.com/Almoullim/background_location/pull/74)

### 0.3.1
Bug fixes

## 0.3.0
Allow updating android notification (title, message and icon) and update time interval between localisation. [#61](https://github.com/Almoullim/background_location/pull/61)

## 0.2.2
prevent setNotificationTitle form running in ios

## 0.2.1
Include swift version in podspec to avoid errors when the users don't

## 0.2.0
Allow changing notification title in Android

## 0.1.3
Update android plugin permissions
Update README

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
