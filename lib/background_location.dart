import 'dart:async';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'dart:io' show Platform;

/// BackgroundLocation plugin to get background
/// lcoation updates in iOS and Android
class BackgroundLocation {
  // The channel to be used for communication.
  // This channel is also refrenced inside both iOS and Abdroid classes
  static const MethodChannel _channel =
      const MethodChannel('almoullim.com/background_location');

  /// Stop receiving location updates
  static stopLocationService() async {
    return await _channel.invokeMethod("stop_location_service");
  }

  /// Start receiving location updated
  static startLocationService({double distanceFilter = 0.0}) async {
    return await _channel.invokeMethod("start_location_service", <String, dynamic>{
      "distance_filter": distanceFilter
    });
  }

  static setAndroidNotification({String title, String message, String icon}) async {
    if (Platform.isAndroid) {
      return await _channel.invokeMethod("set_android_notification", <String, dynamic>{
        "title": title,
        "message": message,
        "icon": icon
      });
    } else {
      //return Promise.resolve();
    }
  }

  static setAndroidConfiguration(int interval) async {
    if (Platform.isAndroid) {
      return await _channel.invokeMethod("set_configuration", <String, dynamic>{
        "interval": interval.toString(),
      });
    } else {
      //return Promise.resolve();
    }
  }


  /// Get the current location once.
  Future<Location> getCurrentLocation() async {
    Completer<Location> completer = Completer();

    Location _location = Location();
    await getLocationUpdates((location) {
      _location.latitude = location.latitude;
      _location.longitude = location.longitude;
      _location.accuracy = location.accuracy;
      _location.altitude = location.altitude;
      _location.bearing = location.bearing;
      _location.speed = location.speed;
      _location.time = location.time;
      completer.complete(_location);
    });

    return completer.future;
  }

  /// Ask the user for location permissions
  static getPermissions({Function onGranted, Function onDenied}) async {
    await Permission.locationWhenInUse.request();
    if (await Permission.locationWhenInUse.isGranted) {
      if (onGranted != null) {
        onGranted();
      }
    } else if (await Permission.locationWhenInUse.isDenied ||
        await Permission.locationWhenInUse.isPermanentlyDenied ||
        await Permission.locationWhenInUse.isRestricted ) {
      if (onDenied != null) {
        onDenied();
      }
    }
  }

  /// Check what the current permissions status is
  static Future<PermissionStatus> checkPermissions() async {
    PermissionStatus permission = await Permission.locationWhenInUse.status;
    return permission;
  }

  /// Register a function to recive location updates as long as the location
  /// service has started
  static getLocationUpdates(Function(Location) location) {
    // add a handler on the channel to recive updates from the native classes
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == "location") {
        Map locationData = Map.from(methodCall.arguments);
        // Call the user passed function
        location(
          Location(
            latitude: locationData["latitude"],
            longitude: locationData["longitude"],
            altitude: locationData["altitude"],
            accuracy: locationData["accuracy"],
            bearing: locationData["bearing"],
            speed: locationData["speed"],
            time: locationData["time"],
            isMock: locationData["is_mock"]
          ),
        );
      }
    });
  }
}

/// An object containing infromation
/// about the user current location
class Location {
  Location(
      {this.longitude,
      this.latitude,
      this.altitude,
      this.accuracy,
      this.bearing,
      this.speed,
      this.time,
      this.isMock});

  double latitude;
  double longitude;
  double altitude;
  double bearing;
  double accuracy;
  double speed;
  double time;
  bool isMock;
  
  toMap() {
    var obj = {
      'latitude': this.latitude,
      'longitude': this.longitude,
      'altitude': this.altitude,
      'bearing': this.bearing,
      'accuracy': this.accuracy,
      'speed': this.speed,
      'time': this.time,
      'is_mock': this.isMock
    };
    return obj;
  } 
}
