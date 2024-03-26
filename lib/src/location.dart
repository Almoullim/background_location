import 'package:flutter/cupertino.dart';

/// Location data
@immutable
class Location {
  final double? latitude;
  final double? longitude;
  final double? altitude;
  final double? bearing;
  final double? accuracy;
  final double? speed;
  final DateTime time;
  final bool? isMock;

  const Location({
    required this.longitude,
    required this.latitude,
    required this.altitude,
    required this.accuracy,
    required this.bearing,
    required this.speed,
    required this.time,
    required this.isMock,
  });

  /// Parse the [Location] from a Map.
  ///
  /// [time] gets converted from a unix timestamp in milliseconds.
  factory Location.fromMap(Map<String, Object?> map) => Location(
        longitude: map['longitude'] as double?,
        latitude: map['latitude'] as double?,
        altitude: map['altitude'] as double?,
        accuracy: map['accuracy'] as double?,
        bearing: map['bearing'] as double?,
        speed: map['speed'] as double?,
        time: DateTime.fromMillisecondsSinceEpoch(
            (map['time'] as double).toInt()),
        isMock: map['isMock'] as bool?,
      );

  /// Convert the [Location] to a Map.
  ///
  /// [time] gets converted to a unix timestamp in milliseconds.
  Map<String, Object?> toMap() => {
        'latitude': latitude,
        'longitude': longitude,
        'altitude': altitude,
        'bearing': bearing,
        'accuracy': accuracy,
        'speed': speed,
        'time': time.millisecondsSinceEpoch,
        'is_mock': isMock,
      };

  @override
  String toString() => 'Location('
      'latitude: $latitude, '
      'longitude: $longitude, '
      'altitude: $altitude, '
      'bearing: $bearing, '
      'accuracy: $accuracy, '
      'speed: $speed, '
      'time: $time, '
      'is_mock: $isMock, '
      ')';

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Location &&
          runtimeType == other.runtimeType &&
          latitude == other.latitude &&
          longitude == other.longitude &&
          altitude == other.altitude &&
          bearing == other.bearing &&
          accuracy == other.accuracy &&
          speed == other.speed &&
          time == other.time &&
          isMock == other.isMock;

  @override
  int get hashCode => Object.hash(
        latitude,
        longitude,
        altitude,
        bearing,
        accuracy,
        speed,
        time,
        isMock,
      );
}
