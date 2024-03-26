import 'dart:async';
import 'dart:io' show Platform;

import 'package:background_location/background_location.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

/// BackgroundLocation plugin to get background
/// location updates in iOS and Android
class BackgroundLocation {
  /// The channel to be used for communication.
  /// This channel is also referenced inside both iOS and Android classes
  static const MethodChannel _channel =
      MethodChannel('com.almoullim.background_location/methods');

  /// Stop receiving location updates
  static Future<dynamic> stopLocationService() async =>
      _channel.invokeMethod('stop_location_service');

  /// Check if the location update service is running
  static Future<bool> isServiceRunning() async =>
      await _channel.invokeMethod<bool>('is_service_running') ?? false;

  /// Start receiving location updated
  static Future<dynamic> startLocationService({
    double distanceFilter = 0.0,
    bool forceAndroidLocationManager = false,
  }) async =>
      _channel.invokeMethod(
        'start_location_service',
        <String, Object>{
          'distance_filter': distanceFilter,
          'force_location_manager': forceAndroidLocationManager
        },
      );

  /// Set the notification on android devices.
  /// Does nothing if called on other platforms.
  static Future<dynamic> setAndroidNotification({
    String? title,
    String? message,
    String? icon,
  }) async {
    if (!Platform.isAndroid) return;

    return await _channel.invokeMethod(
      'set_android_notification',
      <String, Object?>{
        'title': title,
        'message': message,
        'icon': icon,
      },
    );
  }

  /// Set the android configuration.
  /// Does nothing if called on other platforms.
  static Future<dynamic> setAndroidConfiguration(int interval) async {
    if (!Platform.isAndroid) return;

    return await _channel.invokeMethod(
      'set_configuration',
      <String, dynamic>{
        'interval': interval.toString(),
      },
    );
  }

  /// Get the current location once.
  Future<Location> getCurrentLocation() {
    var completer = Completer<Location>();

    getLocationUpdates((location) {
      completer.complete(location);
    });

    return completer.future;
  }

  /// Register a function to receive location updates as long as the location
  /// service has started
  static void getLocationUpdates(void Function(Location location) callback) {
    // add a handler on the channel to receive updates from the native classes
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == 'location') {
        try {
          final map = methodCall.arguments as Map<Object?, Object?>;
          final location = Location.fromMap(map.cast<String, Object?>());
          // Call the user passed function
          callback.call(location);
        } catch (error, stack) {
          debugPrint(error.toString());
          debugPrintStack(stackTrace: stack);
        }
      }
    });
  }
}
