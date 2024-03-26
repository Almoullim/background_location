/// Location data
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
    required this.longitude,
    required this.latitude,
    required this.altitude,
    required this.accuracy,
    required this.bearing,
    required this.speed,
    required this.time,
    required this.isMock,
  });

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
