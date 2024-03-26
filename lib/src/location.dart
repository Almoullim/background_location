/// Location data
class Location {
  final double? latitude;
  final double? longitude;
  final double? altitude;
  final double? bearing;
  final double? accuracy;
  final double? speed;
  final double? time;
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

  factory Location.fromMap(Map<String, Object?> map) => Location(
        longitude: map['longitude'] as double?,
        latitude: map['latitude'] as double?,
        altitude: map['altitude'] as double?,
        accuracy: map['accuracy'] as double?,
        bearing: map['bearing'] as double?,
        speed: map['speed'] as double?,
        time: map['time'] as double?,
        isMock: map['isMock'] as bool?,
      );

  Map<String, Object?> toMap() => {
        'latitude': latitude,
        'longitude': longitude,
        'altitude': altitude,
        'bearing': bearing,
        'accuracy': accuracy,
        'speed': speed,
        'time': time,
        'is_mock': isMock,
      };
}
