import 'dart:ui';

import 'package:background_location_2/background_location.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

const backgroundChannelID = 'com.almoullim.background_location/background';
const argCallback = 'ARG_CALLBACK';
const argLocation = 'ARG_LOCATION';
const argLocations = 'ARG_LOCATIONS';
const bcmLocation = 'BCM_LOCATION';
const bcmNotificationAction = 'BCM_NOTIFICATION_ACTION';

@pragma('vm:entry-point')
void callbackHandler() {
  const backgroundChannel = MethodChannel(backgroundChannelID);
  WidgetsFlutterBinding.ensureInitialized();

  backgroundChannel.setMethodCallHandler((MethodCall call) async {
    if (bcmLocation == call.method) {
      final Map<dynamic, dynamic> args = call.arguments as Map;

      int callbackArg = args[argCallback] as int? ?? 0;
      if (callbackArg != 0) {
        final callback =
            PluginUtilities.getCallbackFromHandle(CallbackHandle.fromRawHandle(callbackArg));
        if (callback != null) {
          var locs = List<Location>.empty(growable: true);
          var locations = args[argLocations] as Iterable?;
          if (locations != null && '$locations' != '[]') {
            for (var loc in locations) {
              locs.add(Location.fromJson(loc as Map));
            }
          } else {
            locs.add(Location.fromJson(args[argLocation] as Map));
          }
          callback(locs);
        }
      }
    } else if (bcmNotificationAction == call.method) {
      final Map<dynamic, dynamic> args = call.arguments as Map;

      int callbackArg = args[argCallback] as int? ?? 0;
      if (callbackArg != 0) {
        final callback =
            PluginUtilities.getCallbackFromHandle(CallbackHandle.fromRawHandle(callbackArg));
        final dynamic locationJson = args[argLocation];
        Location? location;
        if (locationJson != null) {
          location = Location.fromJson(locationJson as Map);
        }
        if (callback != null) {
          callback(location);
        }
      }
    }
  });
  backgroundChannel.invokeMethod('BackgroundLocationService.initialized');
}
