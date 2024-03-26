import 'dart:async';
import 'dart:io' show Platform;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// BackgroundLocation plugin to get background
/// location updates in iOS and Android
class BackgroundLocation {
  // The channel to be used for communication.
  // This channel is also referenced inside both iOS and Android classes
  static const MethodChannel _channel =
      MethodChannel('com.almoullim.background_location/methods');

  /// Stop receiving location updates
  static stopLocationService() async {
    return await _channel.invokeMethod('stop_location_service');
  }

  /// Check if the location update service is running
  static Future<bool> isServiceRunning() async {
    var result = await _channel.invokeMethod('is_service_running');
    return result == true;
  }

  /// Start receiving location updated
  static startLocationService({
    double distanceFilter = 0.0,
    bool forceAndroidLocationManager = false,
  }) async {
    return await _channel
        .invokeMethod('start_location_service', <String, dynamic>{
      'distance_filter': distanceFilter,
      'force_location_manager': forceAndroidLocationManager
    });
  }

  static setAndroidNotification({
    String? title,
    String? message,
    String? icon,
  }) async {
    if (Platform.isAndroid) {
      return await _channel.invokeMethod(
        'set_android_notification',
        <String, dynamic>{'title': title, 'message': message, 'icon': icon},
      );
    } else {
      //return Promise.resolve();
    }
  }

  static setAndroidConfiguration(int interval) async {
    if (Platform.isAndroid) {
      return await _channel.invokeMethod('set_configuration', <String, dynamic>{
        'interval': interval.toString(),
      });
    } else {
      //return Promise.resolve();
    }
  }

  /// Get the current location once.
  Future<Location> getCurrentLocation() async {
    var completer = Completer<Location>();

    await getLocationUpdates((l) {
      final location = Location(
        accuracy: l.accuracy,
        altitude: l.altitude,
        bearing: l.bearing,
        isMock: l.isMock,
        latitude: l.latitude,
        longitude: l.longitude,
        speed: l.speed,
        time: l.time,
      );
      completer.complete(location);
    });

    return completer.future;
  }

  /// Register a function to receive location updates as long as the location
  /// service has started
  static getLocationUpdates(Function(Location) location) {
    // add a handler on the channel to receive updates from the native classes
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == 'location') {
        var locationData = Map.from(methodCall.arguments);
        // Call the user passed function
        location(
          Location(
            latitude: locationData['latitude'],
            longitude: locationData['longitude'],
            altitude: locationData['altitude'],
            accuracy: locationData['accuracy'],
            bearing: locationData['bearing'],
            speed: locationData['speed'],
            time: locationData['time'],
            isMock: locationData['is_mock'],
          ),
        );
      }
    });
  }
}

/// about the user current location
class Location {
  double? latitude;
  double? longitude;
  double? altitude;
  double? bearing;
  double? accuracy;
  double? speed;
  double? time;
  bool? isMock;

  Location({
    @required this.longitude,
    @required this.latitude,
    @required this.altitude,
    @required this.accuracy,
    @required this.bearing,
    @required this.speed,
    @required this.time,
    @required this.isMock,
  });

  toMap() {
    var obj = {
      'latitude': latitude,
      'longitude': longitude,
      'altitude': altitude,
      'bearing': bearing,
      'accuracy': accuracy,
      'speed': speed,
      'time': time,
      'is_mock': isMock,
    };
    return obj;
  }
}
