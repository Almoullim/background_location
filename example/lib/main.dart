import 'package:background_location/background_location.dart';
import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  MyAppState createState() => MyAppState();
}

class MyAppState extends State<MyApp> {
  Location? _lastLocation;
  bool? _serviceRunning;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Background Location Service')),
        body: ListView(
          children: <Widget>[
            Text(
              'Latitude: ${_lastLocation?.latitude}\n'
              'Longitude: ${_lastLocation?.longitude}\n'
              'Altitude: ${_lastLocation?.altitude}\n'
              'Accuracy: ${_lastLocation?.accuracy}\n'
              'Bearing: ${_lastLocation?.bearing}\n'
              'Speed: ${_lastLocation?.speed}\n'
              'Time: ${_lastLocation?.time}\n'
              'IsServiceRunning: $_serviceRunning',
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
              textAlign: TextAlign.center,
            ),
            ElevatedButton(
                onPressed: _startLocationService,
                child: const Text('Start Location Service')),
            ElevatedButton(
                onPressed: _stopLocationService,
                child: const Text('Stop Location Service')),
            ElevatedButton(
              onPressed: _checkService,
              child: const Text('Check if service is running'),
            ),
            ElevatedButton(
              onPressed: _getCurrentLocation,
              child: const Text('Get Current Location'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _getCurrentLocation() async {
    final location = await BackgroundLocation().getCurrentLocation();
    debugPrint('Current Location: $location');
  }

  Future<void> _stopLocationService() async {
    await BackgroundLocation.stopLocationService();
  }

  Future<void> _startLocationService() async {
    await BackgroundLocation.setAndroidNotification(
      title: 'Background service is running',
      message: 'Background location in progress',
      icon: '@mipmap/ic_launcher',
    );
    //await BackgroundLocation.setAndroidConfiguration(1000);
    await BackgroundLocation.startLocationService();
    BackgroundLocation.getLocationUpdates(onLocationUpdate);
  }

  void onLocationUpdate(Location location) {
    debugPrint(
      '\n'
      'Latitude:  ${location.latitude}\n'
      'Longitude: ${location.longitude}\n'
      'Altitude: ${location.altitude}\n'
      'Accuracy: ${location.accuracy}\n'
      'Bearing:  ${location.bearing}\n'
      'Speed: ${location.speed}\n'
      'Time: ${location.time}',
    );
    if (mounted) setState(() => _lastLocation = location);
  }

  Future<void> _checkService() async {
    final isRunning = await BackgroundLocation.isServiceRunning();
    if (mounted) setState(() => _serviceRunning = isRunning);
    debugPrint("Is Running: $isRunning");
  }

  @override
  void dispose() {
    BackgroundLocation.stopLocationService();
    super.dispose();
  }
}
