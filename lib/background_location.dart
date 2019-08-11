import 'dart:async';
import 'package:flutter/services.dart';

class BackgroundLocation {
  static const MethodChannel _channel =
      const MethodChannel('almoullim.com/background_location');

  static stopLocationService() {
    _channel.invokeMapMethod("stop_location_service");
  }

  static startLocationService() {
    _channel.invokeMapMethod("start_location_service");
  }

  Future<_Location> getCurrentLocation() async {
    Completer<_Location> completer = Completer();

    _Location _location = _Location();
    await getLocationUpdates((location) {
      _location.latitude = location.latitude;
      _location.longitude = location.longitude;
      _location.accuracy = location.accuracy;
      _location.altitude = location.altitude;
      _location.bearing = location.bearing;
      _location.speed = location.speed;
      completer.complete(_location);
    });

    return completer.future;
  }

  static getLocationUpdates(Function(_Location) location) {
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      if (methodCall.method == "location") {
        Map locationData = Map.from(methodCall.arguments);
        location(
          _Location(
            latitude: locationData["latitude"],
            longitude: locationData["longitude"],
            altitude: locationData["altitude"],
            accuracy: locationData["accuracy"],
            bearing: locationData["bearing"],
            speed: locationData["speed"],
          ),
        );
      }
    });
  }
}

class _Location {
  _Location(
      {this.longitude,
      this.latitude,
      this.altitude,
      this.accuracy,
      this.bearing,
      this.speed});

  double latitude;
  double longitude;
  double altitude;
  double bearing;
  double accuracy;
  double speed;
}
