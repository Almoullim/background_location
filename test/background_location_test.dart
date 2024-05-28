import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
// import 'package:background_location/background_location.dart';

void main() {
  const channel = MethodChannel('background_location');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });
}
