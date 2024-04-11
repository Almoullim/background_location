import 'dart:async';
import 'dart:developer';
import 'dart:ui';

import 'package:background_location/background_location.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

const BACKGROUND_CHANNEL_ID = 'com.almoullim.background_location/background';
const ARG_CALLBACK = 'ARG_CALLBACK';
const ARG_LOCATION = 'ARG_LOCATION';
const ARG_LOCATIONS = 'ARG_LOCATIONS';
const BCM_LOCATION = 'BCM_LOCATION';
const BCM_NOTIFICATION_ACTION = 'BCM_NOTIFICATION_ACTION';

@pragma('vm:entry-point')
void callbackHandler() {
  const _backgroundChannel = MethodChannel(BACKGROUND_CHANNEL_ID);
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    if (BCM_LOCATION == call.method) {
      final Map<dynamic, dynamic> args = call.arguments;

      int callbackArg = args[ARG_CALLBACK] ?? 0;
      if (callbackArg != 0) {
        final callback = PluginUtilities.getCallbackFromHandle(
            CallbackHandle.fromRawHandle(callbackArg));
        if (callback != null) {
          var locs = List.empty(growable: true);
          var locations = args[ARG_LOCATIONS];
          if (locations != null && '$locations' != '[]') {
            for (var loc in locations) {
              locs.add(Location.fromJson(loc));
            }
          } else {
            locs.add(Location.fromJson(args[ARG_LOCATION]));
          }
          callback(locs);
        }
      }
    } else if (BCM_NOTIFICATION_ACTION == call.method) {
      final Map<dynamic, dynamic> args = call.arguments;

      int callbackArg = args[ARG_CALLBACK] ?? 0;
      if (callbackArg != 0) {
        final callback = PluginUtilities.getCallbackFromHandle(
            CallbackHandle.fromRawHandle(callbackArg));
        final dynamic locationJson = args[ARG_LOCATION];
        Location? location;
        if (locationJson != null) {
          location = Location.fromJson(locationJson);
        }
        if (callback != null) {
          callback(location);
        }
      }
    }
  });
  _backgroundChannel.invokeMethod('BackgroundLocationService.initialized');
}
