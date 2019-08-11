#import "BackgroundLocationPlugin.h"
#import <background_location/background_location-Swift.h>

@implementation BackgroundLocationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBackgroundLocationPlugin registerWithRegistrar:registrar];
}
@end
